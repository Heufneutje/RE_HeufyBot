package heufybot.core.events.types;

import heufybot.core.Channel;
import heufybot.core.User;

public class PartEvent implements BotEvent
{
	private User user;
	private Channel channel;
	
	public PartEvent(User user, Channel channel)
	{
		this.user = user;
		this.channel = channel;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public Channel getChannel()
	{
		return channel;
	}

	@Override
	public String getMessage()
	{
		return channel.getName();
	}
	
	public String toString()
	{
		return "PartEvent";
	}
}
