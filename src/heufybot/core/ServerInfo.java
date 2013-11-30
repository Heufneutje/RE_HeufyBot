package heufybot.core;

public class ServerInfo 
{
	//TODO Add more server info
	
	private String motd;
	private static final ServerInfo instance = new ServerInfo();
	
	private ServerInfo()
	{
		
	}
	
	public static ServerInfo getInstance()
	{
		return instance;
	}
	
	public String getMotd()
	{
		return motd;
	}

	public void setMotd(String motd) 
	{
		this.motd = motd;
	}
	
	public void appendMotd(String motd)
	{
		this.motd += motd;
	}
}