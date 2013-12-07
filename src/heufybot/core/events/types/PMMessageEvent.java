package heufybot.core.events.types;

import heufybot.core.User;

public class PMMessageEvent implements BotEvent
{
	private User user;
	private String message;
	
	public PMMessageEvent(User user, String message)
	{
		this.user = user;
		this.message = message;
	}
	
	@Override
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
		return "PMMessageEvent";
	}
}
