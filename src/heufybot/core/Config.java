package heufybot.core;

import java.util.ArrayList;
import java.util.List;

import heufybot.core.cap.CapHandler;
import heufybot.core.cap.EnablingCapHandler;
import heufybot.utils.enums.PasswordType;

public class Config 
{
	private String nickname, username, realname, server, password, commandPrefix;
	private int port, reconnectAttempts, reconnectInterval;
	private PasswordType passwordType;
	private boolean autoJoinEnabled, autoNickChange, autoReconnect;
	private String[] autoJoinChannels, modulesToLoad;
	private long messageDelay;
	private List<CapHandler> capHandlers;
	
	private static final Config instance = new Config();
	
	private Config()
	{
		this.nickname = "RE_HeufyBot";
		this.username = "HeufyButt";
		this.realname = "RE_HeufyBot Dev Build";
		this.server = "localhost";
		this.password = "";
		this.passwordType = PasswordType.None;
		this.port = 6667;
		this.autoJoinEnabled = true;
		this.autoJoinChannels = new String[] { "#heufneutje" };
		this.autoNickChange = true;
		this.autoReconnect = true;
		this.reconnectAttempts = 99;
		this.reconnectInterval = 10;
		this.messageDelay = 500;
		this.commandPrefix = "~";
		this.modulesToLoad = new String[] { "Moduleloader", "Say", "Help" } ;
		
		this.capHandlers = new ArrayList<CapHandler>();
		this.capHandlers.add(new EnablingCapHandler("multi-prefix"));
	}
	
	public static Config getInstance()
	{
		return instance;
	}
	
	public String getNickname()
	{
		return nickname;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getRealname()
	{
		return realname;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public PasswordType getPasswordType()
	{
		return passwordType;
	}
	
	public boolean getAutoJoinEnabled()
	{
		return autoJoinEnabled;
	}
	
	public boolean getAutoNickChange()
	{
		return autoNickChange;
	}
	
	public String[] getAutoJoinChannels()
	{
		return autoJoinChannels;
	}

	public int getReconnectAttempts() 
	{
		return reconnectAttempts;
	}

	public void setReconnectAttempts(int reconnectAttempts)
	{
		this.reconnectAttempts = reconnectAttempts;
	}

	public boolean autoReconnect() 
	{
		return autoReconnect;
	}

	public void setAutoReconnect(boolean autoReconnect) 
	{
		this.autoReconnect = autoReconnect;
	}

	public int getReconnectInterval()
	{
		return reconnectInterval;
	}

	public void setReconnectInterval(int reconnectInterval)
	{
		this.reconnectInterval = reconnectInterval;
	}

	public long getMessageDelay()
	{
		return messageDelay;
	}

	public void setMessageDelay(long messageDelay)
	{
		this.messageDelay = messageDelay;
	}
	
	public List<CapHandler> getCapHandlers()
	{
		return capHandlers;
	}
	
	public String getCommandPrefix()
	{
		return commandPrefix;
	}

	public String[] getFeaturesToLoad()
	{
		return modulesToLoad;
	}

	public void setFeaturesToLoad(String[] featuresToLoad)
	{
		this.modulesToLoad = featuresToLoad;
	}
}