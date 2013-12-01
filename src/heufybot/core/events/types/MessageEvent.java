package heufybot.core.events.types;

import heufybot.core.Channel;
import heufybot.core.User;

public class MessageEvent implements BotEvent
{
	private User user;
	private Channel channel;
	private String message;
	
	@Override
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
		return "MessageEvent";
	}
}
