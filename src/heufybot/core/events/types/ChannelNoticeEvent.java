package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class ChannelNoticeEvent implements BotEvent
{
	private String serverName;
	private String source;
	private IRCChannel channel;
	private String message;
	
	public ChannelNoticeEvent(String serverName, String source, IRCChannel channel, String message)
	{
		this.serverName = serverName;
		this.source = source;
		this.channel = channel;
		this.message = message;
	}
	
	public String getServerName()
	{
		return serverName;
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
