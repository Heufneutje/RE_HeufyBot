package heufybot.core.events.types;

import heufybot.core.Channel;
import heufybot.core.User;

public class InviteEvent implements BotEvent
{
	private User inviter;
	private String invitee;
	private Channel channel;
	private String message;
	
	public InviteEvent(User inviter, String invitee, Channel channel, String message)
	{
		this.inviter = inviter;
		this.invitee = invitee;
		this.channel = channel;
		this.message = message;
	}
	
	public User getInviter()
	{
		return inviter;
	}
	
	public String getInvitee()
	{
		return invitee;
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
		return "InviteEvent";
	}
}
