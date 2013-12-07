package heufybot.core.events.types;

import heufybot.core.Channel;
import heufybot.core.User;

public class QuitEvent implements BotEvent
{
	private User user;
	
	public QuitEvent(User user, Channel channel)
	{
		this.user = user;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public String toString()
	{
		return "QuitEvent";
	}
}
