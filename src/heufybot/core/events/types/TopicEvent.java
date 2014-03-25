package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class TopicEvent implements BotEvent
{
	private String serverName, source, topic;
	private IRCChannel channel;
	
	public TopicEvent(String serverName, String source, IRCChannel channel, String topic)
	{
		this.serverName = serverName;
		this.source = source;
		this.channel = channel;
		this.topic = topic;
	}
	
	public String getServerName()
	{
		return serverName;
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
