package heufybot.core.events.types;

import heufybot.core.Channel;

public class ServerResponseChannelEvent implements BotEvent
{
	private String line;
	private Channel channel;
	
	public ServerResponseChannelEvent(Channel channel, String line)
	{
		this.line = line;
		this.channel = channel;
	}
	
	public String getLine()
	{
		return line;
	}
	
	public Channel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "ServerResponseChannelEvent";
	}
}
