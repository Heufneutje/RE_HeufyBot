package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class ModeEvent implements BotEvent
{
	private String setter;
	private IRCChannel channel;
	private String mode;
	
	public ModeEvent(String setter, IRCChannel channel, String mode)
	{
		this.setter = setter;
		this.channel = channel;
		this.mode = mode;
	}
	
	public String getMode()
	{
		return mode;
	}
	
	public String getSetter()
	{
		return setter;
	}
	
	public IRCChannel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "ModeEvent";
	}
}
