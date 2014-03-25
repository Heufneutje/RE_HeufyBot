package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class ChannelNoticeEvent implements BotEvent
{
	private String source;
	private IRCChannel channel;
	private String message;
	
	public ChannelNoticeEvent(String source, IRCChannel channel, String message)
	{
		this.source = source;
		this.channel = channel;
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String getSource()
	{
		return source;
	}
	
	public IRCChannel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "ChannelNoticeEvent";
	}
}
