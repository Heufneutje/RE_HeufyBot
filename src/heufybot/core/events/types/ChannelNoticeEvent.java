package heufybot.core.events.types;

import heufybot.core.Channel;

public class ChannelNoticeEvent implements BotEvent
{
	private String source;
	private Channel channel;
	private String message;
	
	public ChannelNoticeEvent(String source, Channel channel, String message)
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
	
	public Channel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "ChannelNoticeEvent";
	}
}
