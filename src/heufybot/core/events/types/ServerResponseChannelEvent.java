package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class ServerResponseChannelEvent implements BotEvent
{
	private String serverName, line;
	private IRCChannel channel;
	
	public ServerResponseChannelEvent(String serverName, IRCChannel channel, String line)
	{
		this.serverName = serverName;
		this.line = line;
		this.channel = channel;
	}
	
	public String getServerName()
	{
		return serverName;
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
