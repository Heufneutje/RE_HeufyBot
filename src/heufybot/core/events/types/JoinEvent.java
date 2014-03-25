package heufybot.core.events.types;

import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;

public class JoinEvent implements BotEvent
{
	private IRCUser user;
	private IRCChannel channel;
	
	public JoinEvent(IRCUser user, IRCChannel channel)
	{
		this.user = user;
		this.channel = channel;
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
