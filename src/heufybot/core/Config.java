package heufybot.core;

import heufybot.utils.enums.PasswordType;

public class Config 
{
	private String nickname, username, realname, server, password;
	private int port, reconnectAttempts, reconnectInterval;
	private PasswordType passwordType;
	private boolean autoJoinEnabled, autoNickChange, autoReconnect;
	private String[] autoJoinChannels;
	private static final Config instance = new Config();
	
	private Config()
	{
		this.nickname = "RE_HeufyBot";
		this.username = "RE_HeufyBot";
		this.realname = "HeufyBot Dev Build";
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

	public int getReconnectInterval() {
		return reconnectInterval;
	}

	public void setReconnectInterval(int reconnectInterval) {
		this.reconnectInterval = reconnectInterval;
	}
}