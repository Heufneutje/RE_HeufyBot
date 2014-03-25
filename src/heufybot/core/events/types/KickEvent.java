package heufybot.core.events.types;

import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;

public class KickEvent implements BotEvent
{
	private IRCUser kicker;
	private IRCUser recipient;
	private IRCChannel channel;
	private String message;
	
	public KickEvent(IRCUser recipient, IRCUser kicker, IRCChannel channel, String message)
	{
		this.kicker = kicker;
		this.recipient = recipient;
		this.channel = channel;
		this.message = message;
	}
	
	public IRCUser getKicker()
	{
		return kicker;
	}
	
	public IRCUser getRecipient()
	{
		return recipient;
	}
	
	public IRCChannel getChannel()
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
