package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class BotMessageEvent implements BotEvent
{
	private IRCUser user;
	private String target;
	private String message;
	
	public BotMessageEvent(IRCUser user, String target, String message)
	{
		this.user = user;
		this.target = target;
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public IRCUser getUser()
	{
		return user;
	}
	
	public String getTarget()
	{
		return target;
	}
	
	public String toString()
	{
		return "BotMessageEvent";
	}
}
