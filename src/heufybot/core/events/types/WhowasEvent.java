package heufybot.core.events.types;

import heufybot.utils.WhoisBuilder;

public class WhowasEvent implements BotEvent
{
	private String serverName;
	private WhoisBuilder builder;
	
	public WhowasEvent(String serverName, WhoisBuilder builder)
	{
		this.serverName = serverName;
		this.builder = builder;
	}
	
	public String getServerName()
	{
		return serverName;
	}
	
	public WhoisBuilder getWhoisBuilder()
	{
		return builder;
	}
	
	public String toString()
	{
		return "WhowasEvent";
	}
}
