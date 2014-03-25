package heufybot.core.events.types;

import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;

public class PartEvent implements BotEvent
{
	private IRCUser user;
	private IRCChannel channel;
	private String message;
	
	public PartEvent(IRCUser user, IRCChannel channel, String message)
	{
		this.user = user;
		this.channel = channel;
		this.message = message;
	}
	
	public IRCUser getUser()
	{
		return user;
	}
	
	public IRCChannel getChannel()
	{
		return channel;
	}

	public String getMessage()
	{
		return message;
	}
	
	public String toString()
	{
		return "PartEvent";
	}
}
