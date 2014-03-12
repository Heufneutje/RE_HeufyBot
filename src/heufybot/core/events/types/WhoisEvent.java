package heufybot.core.events.types;

import heufybot.utils.WhoisBuilder;

public class WhoisEvent implements BotEvent
{
	private WhoisBuilder builder;
	
	public WhoisEvent(WhoisBuilder builder)
	{
		this.builder = builder;
	}
	
	public WhoisBuilder getWhoisBuilder()
	{
		return builder;
	}
	
	public String toString()
	{
		return "WhoisEvent";
	}
}