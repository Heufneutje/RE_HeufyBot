package heufybot.core.events.types;

import heufybot.utils.WhoisBuilder;

public class WhowasEvent implements BotEvent
{
	private WhoisBuilder builder;
	
	public WhowasEvent(WhoisBuilder builder)
	{
		this.builder = builder;
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
