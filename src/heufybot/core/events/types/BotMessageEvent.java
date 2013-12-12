package heufybot.core.events.types;

import heufybot.core.User;

public class BotMessageEvent implements BotEvent
{
	private User user;
	private String target;
	private String message;
	
	public BotMessageEvent(User user, String target, String message)
	{
		this.user = user;
		this.target = target;
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
	
	public String getTarget()
	{
		return target;
	}
	
	public String toString()
	{
		return "BotMessageEvent";
	}
}
