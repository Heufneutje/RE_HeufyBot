package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class PMMessageEvent implements BotEvent
{
	private IRCUser user;
	private String message;
	
	public PMMessageEvent(IRCUser user, String message)
	{
		this.user = user;
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
	
	public String toString()
	{
		return "PMMessageEvent";
	}
}
