package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class QuitEvent implements BotEvent
{
	private IRCUser user;
	private String message;
	
	public QuitEvent(IRCUser user, String message)
	{
		this.user = user;
		this.message = message;
	}
	
	public IRCUser getUser()
	{
		return user;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String toString()
	{
		return "QuitEvent";
	}
}
