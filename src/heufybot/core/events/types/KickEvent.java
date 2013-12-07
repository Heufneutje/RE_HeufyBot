package heufybot.core.events.types;

import heufybot.core.Channel;
import heufybot.core.User;

public class KickEvent implements BotEvent
{
	private User kicker;
	private User recipient;
	private Channel channel;
	private String message;
	
	public KickEvent(User kicker, User recipient, Channel channel, String message)
	{
		this.kicker = kicker;
		this.recipient = recipient;
		this.channel = channel;
		this.message = message;
	}
	
	public User getKicker()
	{
		return kicker;
	}
	
	public User getRecipient()
	{
		return recipient;
	}
	
	public Channel getChannel()
	{
		return channel;
	}

	public String getMessage()
	{
		return message;
	}
	
	public String toString()
	{
		return "KickEvent";
	}
}
