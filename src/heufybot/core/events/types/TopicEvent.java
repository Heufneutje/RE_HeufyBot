package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class TopicEvent implements BotEvent
{
	private String source;
	private IRCChannel channel;
	private String topic;
	
	public TopicEvent(String source, IRCChannel channel, String topic)
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
	
	public IRCChannel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "TopicEvent";
	}
}
