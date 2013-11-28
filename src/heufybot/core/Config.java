package heufybot.core;

import heufybot.utils.enums.PasswordType;

public class Config 
{
	private String nickname, username, realname, server, password;
	private int port;
	private PasswordType passwordType;
	private boolean autoJoinEnabled, autoNickChange;
	private String[] autoJoinChannels;
	private String channelPrefixes;
	
	private static final Config instance = new Config();
	
	private Config()
	{
		this.nickname = "RE_HeufyBot";
		this.username = "RE_HeufyBot";
		this.realname = "HeufyBot Dev Build";
		this.server = "192.168.2.7";
		this.password = "";
		this.passwordType = PasswordType.None;
		this.port = 6667;
		this.autoJoinEnabled = true;
		this.autoJoinChannels = new String[] { "#heufneutje" };
		this.autoNickChange = true;
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
}