package heufybot.core.events.types;

import heufybot.core.Channel;
import heufybot.core.User;

public class ModeEvent implements BotEvent
{
	private User user;
	private Channel channel;
	private String message;
	
	public ModeEvent(User user, Channel channel, String message)
	{
		this.user = user;
		this.channel = channel;
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
	
	public Channel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "ModeEvent";
	}
}
