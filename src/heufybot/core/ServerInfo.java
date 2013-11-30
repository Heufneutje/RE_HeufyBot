package heufybot.core;

import java.util.LinkedHashMap;

public class ServerInfo 
{
	//TODO Add more server info
	
	private String motd;
	private LinkedHashMap<String, String> userPrefixes;
	private LinkedHashMap<String, String> reverseUserPrefixes;	
	
	private static final ServerInfo instance = new ServerInfo();
	
	private ServerInfo()
	{
		this.userPrefixes = new LinkedHashMap<String, String>();
		this.reverseUserPrefixes = new LinkedHashMap<String, String>();
		
		//Initialize user prefixes with default values (@ for op (o) and + for voice (v))
		userPrefixes.put("o", "@");
		userPrefixes.put("v", "+");
		
		reverseUserPrefixes.put("@", "o");
		reverseUserPrefixes.put("+", "v");	
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

	public LinkedHashMap<String, String> getUserPrefixes()
	{
		return userPrefixes;
	}

	public void setUserPrefixes(LinkedHashMap<String, String> userPrefixes) 
	{
		this.userPrefixes = userPrefixes;
	}

	public LinkedHashMap<String, String> getReverseUserPrefixes() 
	{
		return reverseUserPrefixes;
	}

	public void setReverseUserPrefixes(LinkedHashMap<String, String> reverseUserPrefixes)
	{
		this.reverseUserPrefixes = reverseUserPrefixes;
	}
}