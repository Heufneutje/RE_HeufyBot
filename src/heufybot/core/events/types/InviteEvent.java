package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class InviteEvent implements BotEvent
{
	private IRCUser inviter;
	private String invitee;
	private String channel;
	
	public InviteEvent(IRCUser inviter, String invitee, String channel)
	{
		this.inviter = inviter;
		this.invitee = invitee;
		this.channel = channel;
	}
	
	public IRCUser getInviter()
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
