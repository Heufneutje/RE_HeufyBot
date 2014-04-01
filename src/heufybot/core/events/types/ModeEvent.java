package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class ModeEvent implements BotEvent
{
	private String serverName, setter, mode;
	private IRCChannel channel;
	
	public ModeEvent(String serverName, String setter, IRCChannel channel, String mode)
	{
		this.serverName = serverName;
		this.setter = setter;
		this.channel = channel;
		this.mode = mode;
	}
	
	public String getServerName()
	{
		return serverName;
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
