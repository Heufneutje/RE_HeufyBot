package heufybot.core.events.types;

import heufybot.core.User;

public class WhoisEvent implements BotEvent
{
	private User user;
	
	public WhoisEvent(User user)
	{
		this.user = user;
	}
	
	public User getUser()
	{
		return user;
	}
}