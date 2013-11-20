package heufybot.core;

public class Config 
{
	private String nickname, username, realname, server, password;
	private int port;
	
	private static final Config config = new Config();
	
	private Config()
	{
		this.nickname = "RE_HeufyBot";
		this.username = "RE_HeufyBot";
		this.realname = "HeufyBot Dev Build";
		this.server = "irc.applejack.me";
		this.password = "";
		this.port = 6667;
	}
	
	public static Config getInstance()
	{
		return config;
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
}