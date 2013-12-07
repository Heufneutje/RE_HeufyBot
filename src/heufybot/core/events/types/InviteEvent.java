package heufybot.core.events.types;

import heufybot.core.User;

public class InviteEvent implements BotEvent
{
	private User inviter;
	private String invitee;
	private String channel;
	
	public InviteEvent(User inviter, String invitee, String channel)
	{
		this.inviter = inviter;
		this.invitee = invitee;
		this.channel = channel;
	}
	
	public User getInviter()
	{
		return inviter;
	}
	
	public String getInvitee()
	{
		return invitee;
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "InviteEvent";
	}
}
