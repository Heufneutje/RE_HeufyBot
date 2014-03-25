package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class NickChangeEvent implements BotEvent
{
	private IRCUser user;
	private String newNick;
	private String oldNick;
	
	public NickChangeEvent(IRCUser user, String newNick, String oldNick)
	{
		this.user = user;
		this.newNick = newNick;
		this.oldNick = oldNick;
	}
	
	public String getNewNick()
	{
		return newNick;
	}
	
	public IRCUser getUser()
	{
		return user;
	}
	
	public String getOldNick()
	{
		return oldNick;
	}
	
	public String toString()
	{
		return "NickChangeEvent";
	}
}
