package heufybot.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ServerInfo 
{
	//TODO Add more server info
	
	private String motd, network, chantypes;
	private LinkedHashMap<String, String> userPrefixes;
	private LinkedHashMap<String, String> reverseUserPrefixes;
	private List<String> channelListModes, channelSetArgsModes, channelSetUnsetArgsModes, channelNoArgsModes;
	
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
		
		this.channelListModes = new ArrayList<String>();
		this.channelSetArgsModes = new ArrayList<String>();
		this.channelSetUnsetArgsModes = new ArrayList<String>();
		this.channelNoArgsModes = new ArrayList<String>();
		
		this.chantypes = "#";
		this.network = "Unknown Network";
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

	public String getNetwork()
	{
		return network;
	}

	public void setNetwork(String network)
	{
		this.network = network;
	}
	
	public List<String> getChannelListModes()
	{
		return channelListModes;
	}
	
	public List<String> getChannelSetArgsModes()
	{
		return channelSetArgsModes;
	}
	
	public List<String> getChannelSetUnsetArgsModes()
	{
		return channelSetUnsetArgsModes;
	}
	
	public List<String> getChannelNoArgsModes()
	{
		return channelNoArgsModes;
	}

	public String getChantypes() 
	{
		return chantypes;
	}

	public void setChantypes(String chantypes) 
	{
		this.chantypes = chantypes;
	}
}