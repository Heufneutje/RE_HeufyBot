package heufybot.core;

import heufybot.core.events.EventListenerManager;
import heufybot.core.events.types.BotMessageEvent;
import heufybot.utils.SSLSocketUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.SocketFactory;

public class IRCServer 
{
	public enum ConnectionState 
	{
		Initializing, Connected, Disconnected
	}
	
	private Config config;
	
	private Socket socket;
	private BufferedReader inputReader;
	private OutputStreamWriter outputWriter;
	private Thread inputThread;
	private InputParser inputParser;
	private ConnectionState connectionState;
	private ArrayList<IRCChannel> channels;
	private ArrayList<IRCUser> users;
	private ServerInfo serverInfo;
	private List<String> userModes;
	private List<String> enabledCapabilities;
	private EventListenerManager eventListenerManager;
	
	//Locking stuff for the output to server
	private final ReentrantLock writeLock = new ReentrantLock(true);
	private final Condition writeNowCondition = writeLock.newCondition();
	private long lastSentLine = 0;

	private String nickname;
	
	public IRCServer(Config config)
	{
		this.socket = new Socket();
		this.inputParser = new InputParser(this);
		this.connectionState = ConnectionState.Initializing;
		
		this.channels = new ArrayList<IRCChannel>();
		this.users = new ArrayList<IRCUser>();
		
		this.serverInfo = ServerInfo.getInstance();
		this.enabledCapabilities = new ArrayList<String>();
		this.eventListenerManager = new EventListenerManager();
		this.userModes = new ArrayList<String>();
		
		this.config = config;
		this.nickname = "";
	}
	
	public boolean connect(String server, int port)
	{
		if(connectionState == ConnectionState.Connected)
		{
			Logger.error("IRC Connect", "Already connected to a server. Connection failed.");
			return false;
		}
		
		SocketFactory sf;
		if(config.isSSLEnabled())
		{
			//Trust all certificates, since making Java recognize a valid certificate is annoying.
			sf = new SSLSocketUtils().trustAllCertificates();
		}
		else
		{
			sf = SocketFactory.getDefault();
		}
		Logger.log("*** Trying to connect to " + server + "...");
		
		try 
		{
			InetAddress[] foundIPs = InetAddress.getAllByName(server);
			Logger.log("*** " + foundIPs.length + " IP(s) found for host " + server + ".");
			for (InetAddress curAddress : foundIPs)
			{
				try
				{
					Logger.log("*** Trying IP address " + curAddress.getHostAddress() + ":" + port + "...");
					this.socket = sf.createSocket(curAddress, port, null, 0);
					this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
					this.outputWriter = new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8"));
					Logger.log("*** Connected to the server.");
					return true;
				}
				catch (Exception e)
				{
					Logger.error("IRC Connect", "Could not connect to " + server + " on IP address " + curAddress);
				}
			}
			Logger.error("IRC Connect", "Could not connect to " + server);
			return false;
		}
		catch (UnknownHostException e)
		{
			Logger.error("IRC Connect", "Host could not be resolved. Connection failed.");
			return false;
		}
	}
	
	public void login()
	{
		if(config.getPasswordType() == Config.PasswordType.ServerPass)
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
		serverInfo.clear();
		enabledCapabilities.clear();
		userModes.clear();
		
		try 
		{
			this.inputThread.interrupt();
			this.inputReader.close();
			this.outputWriter.flush();
			this.outputWriter.close();
			this.socket.close();
			
			channels.clear();
			users.clear();
		} 
		catch (IOException e) 
		{
			Logger.error("IRC Disconnect", "Error closing connection");
		}
		
		if(reconnect && config.autoReconnect())
		{
			reconnect();
		}
	}
	
	public void reconnect()
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
			e.printStackTrace();
			Logger.error("IRC Output", "Error sending line");
		} 
		finally
		{
			writeLock.unlock();
		}
	}
	
	public void sendRawSplit(String prefix, String message, String suffix)
	{
		String fullMessage = prefix + message + suffix;
		if(fullMessage.length() < config.getMaxLineLength() - 2)
		{
			sendRaw(fullMessage);
			return;
		}
		
		int maxLength = config.getMaxLineLength() - 2 - (prefix + suffix).length();
		int iterations = (int) Math.ceil(message.length() / (double) maxLength);
		for(int i = 0; i < iterations; i++)
		{
			int endPoint = (i != iterations - 1) ? ((i + 1) * maxLength) : message.length();
			String currentPart = prefix + message.substring(i * maxLength, endPoint) + suffix;
			sendRaw(currentPart);
		}
	}
	
	public void sendRawSplit(String prefix, String message)
	{
		sendRawSplit(prefix, message, "");
	}
	
	public void cmdNICK(String nick)
	{
		sendRawNow("NICK " + nick);
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
		sendRaw("QUIT :" + message);
	}
	
	public void cmdPRIVMSG(String target, String message)
	{
		sendRawSplit("PRIVMSG " + target + " :", message);
		eventListenerManager.dispatchEvent(new BotMessageEvent(getUser(nickname), target, message));
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
	
	public void cmdWHOIS(String target)
	{
		sendRaw("WHOIS " + target);
	}
	
	public void cmdWHOWAS(String target)
	{
		sendRaw("WHOWAS " + target);
	}
	
	public void cmdMODE(String target, String mode)
	{
		sendRaw("MODE " + target + " " + mode);
	}
	
	public void cmdNOTICE(String target, String notice)
	{
		sendRawSplit("NOTICE " + target + " :", notice);
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
		sendRawSplit("PRIVMSG " + target + " :\u0001", command, "\u0001");
	}
	
	public void ctcpReply(String target, String replyType, String reply)
	{
		cmdNOTICE(target, "\u0001" + replyType + " " + reply + "\u0001");
	}
	
	public String getAccessLevelOnUser(IRCChannel channel, IRCUser user)
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
	
	public IRCChannel getChannel(String channelName)
	{
		for(IRCChannel channel : channels)
		{
			if(channel.getName().equalsIgnoreCase(channelName))
			{
				return channel;
			}
		}
		return null;
	}
	
	public IRCUser getUser(String nickname)
	{
		for(IRCUser user : users)
		{
			if(user.getNickname().equalsIgnoreCase(nickname))
			{
				return user;
			}
		}

		IRCUser user = new IRCUser(nickname);
		users.add(user);
		return user;
	}
	
	public Config getConfig()
	{
		return config;
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
	
	public ArrayList<IRCChannel> getChannels()
	{
		return channels;
	}
	
	public ArrayList<IRCUser> getUsers()
	{
		return users;
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

	public List<String> getUserModes() 
	{
		return userModes;
	}

	public void parseUserModesChange(String modeChange)
	{
		boolean adding = true;
		for (char curChar : modeChange.toCharArray())
		{
			if (curChar == '-')
			{
				adding = false;
			}
			else if (curChar == '+')
			{
				adding = true;
			}
			else if (adding)
			{
				String current = Character.toString(curChar);
				if(!userModes.contains(current))
				{
					userModes.add(current);
				}
			}
			else
			{
				String current = Character.toString(curChar);
				if(userModes.contains(current))
				{
					userModes.remove(current);
				}
			}
		}
	}
}
