package heufybot.core.events.types;

import heufybot.core.Channel;

public class TopicEvent implements BotEvent
{
	private String source;
	private Channel channel;
	private String topic;
	
	public TopicEvent(String source, Channel channel, String topic)
	{
		this.source = source;
		this.channel = channel;
		this.topic = topic;
	}
	
	public String getMessage()
	{
		return topic;
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
		return "TopicEvent";
	}
}
