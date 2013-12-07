package heufybot.core.events.types;

import heufybot.core.User;

public class PMActionEvent implements BotEvent
{
	private User user;
	private String message;
	
	public PMActionEvent(User user, String message)
	{
		this.user = user;
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public String toString()
	{
		return "PMActionEvent";
	}
}
