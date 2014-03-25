package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class ServerResponseChannelEvent implements BotEvent
{
	private String line;
	private IRCChannel channel;
	
	public ServerResponseChannelEvent(IRCChannel channel, String line)
	{
		this.line = line;
		this.channel = channel;
	}
	
	public String getLine()
	{
		return line;
	}
	
	public IRCChannel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "ServerResponseChannelEvent";
	}
}
