package heufybot.core.events.types;

import heufybot.core.User;

public class NickChangeEvent implements BotEvent
{
	private User user;
	private String newNick;
	private String oldNick;
	
	public NickChangeEvent(User user, String newNick, String oldNick)
	{
		this.user = user;
		this.newNick = newNick;
		this.oldNick = oldNick;
	}
	
	public String getNewNick()
	{
		return newNick;
	}
	
	public User getUser()
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
