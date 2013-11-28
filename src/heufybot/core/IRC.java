package heufybot.core;

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
import java.util.ArrayList;

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
	
	private String nickname;
	
	private IRC()
	{
		this.socket = new Socket();
		this.inputParser = new InputParser(this);
		this.connectionState = ConnectionState.Initializing;
	}
	
	
	//Singleton stuff
	public static IRC getInstance()
	{
		return instance;
	}
	
	public boolean connect(String server, int port)
	{
		if(socket.isConnected())
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
		
		cmdNICK(config.getNickname());
		cmdUSER(config.getUsername(), config.getRealname());
		
		startProcessing();
	}
	
	public void disconnect()
	{
		try 
		{
			this.inputReader.close();
			this.outputWriter.flush();
			this.outputWriter.close();
			this.socket.close();
		} 
		catch (IOException e) 
		{
			Logger.error("IRC Disconnect", "Error closing connection");
		}
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
		             }
	
		             if (line == null)
		                     break;
		             
		             inputParser.parseLine(line);
		             
		             if (Thread.interrupted())
		                     return;
	
				}
			}
		});
		inputThread.start();
	}
	
	public void sendRaw(String line)
	{
		try
		{
			outputWriter.write(line + "\r\n");
			outputWriter.flush();
		}
		catch (IOException e)
		{
			Logger.error("IRC Output", "Error sending line");
		}
	}
	
	public void cmdNICK(String nick)
	{
		sendRaw("NICK " + nick);
	}
	
	public void cmdUSER(String user, String realname)
	{
		sendRaw("USER " + user + " 8 * :" + realname);
	}
	
	public void cmdPING(String ping)
	{
		sendRaw("PING "  + ping);
	}
	
	public void cmdPONG(String response)
	{
		sendRaw("PONG "  + response);
	}
	
	public void cmdQUIT(String message)
	{
		sendRaw("QUIT: " + message);
	}
	
	public void cmdPRIVMSG(String target, String message)
	{
		sendRaw("PRIVMSG " + target + ": " + message);
	}
	
	public void cmdJOIN(String channel, String key)
	{
		sendRaw("JOIN " + channel + " " + key);
	}
	
	public void cmdPART(String channel, String message)
	{
		sendRaw("PART " + channel + ": " + message);
	}
	
	public void cmdPASS(String password)
	{
		sendRaw("PASS " + password);
	}
	
	public void nickservIdentify(String password)
	{
		cmdPRIVMSG("NickServ", "IDENTIFY " + password);
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
}