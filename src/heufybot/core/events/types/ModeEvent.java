package heufybot.core.events.types;

import heufybot.core.Channel;

public class ModeEvent implements BotEvent
{
	private String setter;
	private Channel channel;
	private String mode;
	
	public ModeEvent(String setter, Channel channel, String mode)
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
	
	public Channel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "ModeEvent";
	}
}
