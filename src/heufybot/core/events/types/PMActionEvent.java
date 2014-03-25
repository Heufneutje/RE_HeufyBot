package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class PMActionEvent implements BotEvent
{
	private IRCUser user;
	private String message;
	
	public PMActionEvent(IRCUser user, String message)
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
		return "PMActionEvent";
	}
}
