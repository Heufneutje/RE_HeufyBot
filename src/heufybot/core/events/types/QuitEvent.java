package heufybot.core.events.types;

import heufybot.core.User;

public class QuitEvent implements BotEvent
{
	private User user;
	private String message;
	
	public QuitEvent(User user, String message)
	{
		this.user = user;
		this.message = message;
	}
	
	public User getUser()
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
