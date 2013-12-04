package heufybot.core;

import heufybot.core.events.EventListenerManager;
import heufybot.utils.enums.ConnectionState;
import heufybot.utils.enums.PasswordType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class IRC 
{
	private static final IRC instance = new IRC();
	
	private Config config;
	
	private Socket socket;
	private BufferedReader inputReader;
	private OutputStreamWriter outputWriter;
	private Thread inputThread;
	private InputParser inputParser;
	private ConnectionState connectionState;
	private ArrayList<Channel> channels;
	private ServerInfo serverInfo;
	private List<String> enabledCapabilities;
	private EventListenerManager eventListenerManager;
	
	//Locking stuff for the output to server
	private final ReentrantLock writeLock = new ReentrantLock(true);
	private final Condition writeNowCondition = writeLock.newCondition();
	private long lastSentLine = 0;

	
	private String nickname;
	
	private IRC()
	{
		this.socket = new Socket();
		this.inputParser = new InputParser(this);
		this.connectionState = ConnectionState.Initializing;
		this.channels = new ArrayList<Channel>();
		this.serverInfo = ServerInfo.getInstance();
		this.enabledCapabilities = new ArrayList<String>();
		this.eventListenerManager = new EventListenerManager();
		
		this.nickname = "";
	}
	
	
	//Singleton stuff
	public static IRC getInstance()
	{
		return instance;
	}
	
	public boolean connect(String server, int port)
	{
		if(connectionState == ConnectionState.Connected)
		{
			Logger.error("IRC Connect", "Already connected to a server. Connection failed.");
			return false;
		}
		Logger.log(String.format("*** Trying to connect to %s on port %d", server , port));
		
		try
		{
			this.socket = new Socket(server, port);
			this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.defaultCharset()));
			this.outputWriter = new OutputStreamWriter(socket.getOutputStream(), Charset.defaultCharset());
			Logger.log("*** Connected to the server.");
			return true;
		}
		catch (UnknownHostException e)
		{
			Logger.error("IRC Connect", "Host could not be resolved. Connection failed.");
			return false;
		}
		catch (IOException e)
		{
			Logger.error("IRC Connect", "Unkown connection error. Connection failed.");
			return false;
		}
	}
	
	public void login()
	{
		if(config.getPasswordType() == PasswordType.ServerPass)
		{
			cmdPASS(config.getPassword());
		}
		
		cmdCAP("LS", "");
		
		cmdNICK(config.getNickname());
		cmdUSER(config.getUsername(), config.getRealname());
		
		startProcessing();
	}
	
	public void disconnect(boolean reconnect)
	{
		connectionState = ConnectionState.Disconnected;
		this.nickname = "";
		
		try 
		{
			serverInfo.clear();
			enabledCapabilities.clear();
			this.inputReader.close();
			this.outputWriter.flush();
			this.outputWriter.close();
			this.socket.close();
		} 
		catch (IOException e) 
		{
			Logger.error("IRC Disconnect", "Error closing connection");
		}
		
		if(reconnect && config.autoReconnect())
		{
			ArrayList<String> channelsToRejoin = new ArrayList<String>();
			for(Channel channel : channels)
			{
				channelsToRejoin.add(channel.getName());
			}
			channels.clear();
			reconnect(channelsToRejoin);
		}
		else
		{
			channels.clear();
		}
	}
	
	public void reconnect(ArrayList<String> channelsToRejoin)
	{
		int reconnects = 0;
		while(reconnects < config.getReconnectAttempts())
		{
			reconnects++;
			Logger.log("*** Reconnection attempt #" + reconnects + "...");
			boolean success = connect(config.getServer(), config.getPort());
			if(success)
			{
				login();
				for(String channel : channelsToRejoin)
				{
					cmdJOIN(channel, "");
				}
				return;
			}
			else
			{
				if(reconnects < config.getReconnectAttempts())
				{
					Logger.log("*** Connection failed. Trying again in " + config.getReconnectInterval() + " second(s)...");
					try
					{
						Thread.sleep(config.getReconnectInterval() * 1000);
					} 
					catch (InterruptedException e) 
					{
						Logger.error("IRC - Reconnect", "Thread interrupted while trying to reconnect");
					}
				}
			}
		}
		Logger.log("*** Connection failed. Giving up.");
	}
	
	public void startProcessing()
	{
		inputThread = new Thread(new Runnable()
		{
			public void run()
			{
				while(true)
				{
					 String line;
		             try
		             {
		            	 line = inputReader.readLine();
		             }
		             catch (InterruptedIOException iioe) 
		             {
		            	 cmdPING("" + System.currentTimeMillis() / 1000);
		            	 continue;
		             }
		             catch (Exception e)
		             {
		            	 line = null;
		            	 Logger.log("*** Connection to the server was lost. Trying to reconnect...");
		            	 disconnect(true);
		             }
	
		             if (line == null)
		                     break;

		             if(!line.equals(""))
		             {
		            	 inputParser.parseLine(line);
		             }
		             if (Thread.interrupted())
		                     return;
	
				}
			}
		});
		inputThread.start();
	}
	
	public void sendRaw(String line)
	{
		writeLock.lock();
		try
		{
			long currentNanos = System.nanoTime();
			while(lastSentLine + config.getMessageDelay() * 1000000 > currentNanos)
			{
				writeNowCondition.await(lastSentLine + config.getMessageDelay() * 1000000 - currentNanos, TimeUnit.NANOSECONDS);
				currentNanos = System.nanoTime();
			}
			lastSentLine = System.nanoTime();
			outputWriter.write(line + "\r\n");
			outputWriter.flush();
		}
		catch (IOException e)
		{
			Logger.error("IRC Output", "Error sending line");
		} 
		catch (InterruptedException e)
		{
			Logger.error("IRC Output", "Error while waiting to send line");
		}
		finally
		{
			writeLock.unlock();
		}
	}
	
	public void sendRawNow(String line)
	{
		writeLock.lock();
		try
		{
			lastSentLine = System.nanoTime();
			outputWriter.write(line + "\r\n");
			outputWriter.flush();
		}
		catch (IOException e)
		{
			Logger.error("IRC Output", "Error sending line");
		} 
		finally
		{
			writeLock.unlock();
		}
	}
	
	public void cmdNICK(String nick)
	{
		sendRaw("NICK " + nick);
	}
	
	public void cmdUSER(String user, String realname)
	{
		sendRawNow("USER " + user + " 8 * :" + realname);
	}
	
	public void cmdPING(String ping)
	{
		sendRawNow("PING "  + ping);
	}
	
	public void cmdPONG(String response)
	{
		sendRawNow("PONG "  + response);
	}
	
	public void cmdQUIT(String message)
	{
		sendRaw("QUIT: " + message);
	}
	
	public void cmdPRIVMSG(String target, String message)
	{
		sendRaw("PRIVMSG " + target + " :" + message);
	}
	
	public void cmdJOIN(String channel, String key)
	{
		sendRaw("JOIN " + channel + " " + key);
	}
	
	public void cmdPART(String channel, String message)
	{
		sendRaw("PART " + channel + " :" + message);
	}
	
	public void cmdPASS(String password)
	{
		sendRaw("PASS " + password);
	}
	
	public void cmdWHO(String target)
	{
		sendRaw("WHO " + target);
	}
	
	public void cmdMODE(String target, String mode)
	{
		sendRaw("MODE " + target + " " + mode);
	}
	
	public void cmdNOTICE(String target, String notice)
	{
		sendRaw("NOTICE " + target + " :" + notice);
	}
	
	public void cmdCAP(String capCommand, String arguments)
	{
		sendRawNow("CAP " + capCommand + arguments);
	}
	
	public void cmdACTION(String target, String action)
	{
		ctcpCommand(target, "ACTION " + action);
	}
	
	public void nickservIdentify(String password)
	{
		cmdPRIVMSG("NickServ", "IDENTIFY " + password);
	}
	
	public void ctcpCommand(String target, String command)
	{
		cmdPRIVMSG(target, "\u0001" + command + "\u0001");
	}
	
	public void ctcpReply(String target, String replyType, String reply)
	{
		cmdNOTICE(target, "\u0001" + replyType + " " + reply + "\u0001");
	}
	
	public String getAccessLevelOnUser(Channel channel, User user)
	{
		for(String accessLevel : serverInfo.getUserPrefixes().keySet())
		{
			if(channel.getModesOnUser(user).contains(accessLevel))
			{
				return accessLevel;
			}
		}
		return "";
	}
	
	public Channel getChannel(String channelName)
	{
		for(Channel channel : channels)
		{
			if(channel.getName().equalsIgnoreCase(channelName))
			{
				return channel;
			}
		}
		return null;
	}
	
	public User getUser(String nickname)
	{
		for(Channel channel : channels)
		{
			User user = channel.getUser(nickname);
			if(user != null)
			{
				return user;
			}
		}
		return null;
	}
	
	public Config getConfig()
	{
		return config;
	}
	
	public void setConfig(Config config)
	{
		this.config = config;
	}
	
	public ConnectionState getConnectionState() 
	{
		return connectionState;
	}

	public void setConnectionState(ConnectionState connectionState) 
	{
		this.connectionState = connectionState;
	}

	public String getNickname() 
	{
		return nickname;
	}
	
	public void setLoggedInNick(String nickname)
	{
		this.nickname = nickname;
	}
	
	public ArrayList<Channel> getChannels()
	{
		return channels;
	}
	
	public ServerInfo getServerInfo()
	{
		return serverInfo;
	}

	public List<String> getEnabledCapabilities() 
	{
		return enabledCapabilities;
	}

	public EventListenerManager getEventListenerManager() 
	{
		return eventListenerManager;
	}
}