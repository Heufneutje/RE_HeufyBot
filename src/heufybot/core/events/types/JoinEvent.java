package heufybot.core.events.types;

import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;

public class JoinEvent implements BotEvent
{
	private String serverName;
	private IRCUser user;
	private IRCChannel channel;
	
	public JoinEvent(String serverName, IRCUser user, IRCChannel channel)
	{
		this.serverName = serverName;
		this.user = user;
		this.channel = channel;
	}
	
	public String getServerName()
	{
		return serverName;
	}
	
	public IRCUser getUser()
	{
		return user;
	}
	
	public IRCChannel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "JoinEvent";
	}
}
