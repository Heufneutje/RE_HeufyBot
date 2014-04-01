package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class NickChangeEvent implements BotEvent
{
	private IRCUser user;
	private String serverName, newNick, oldNick;
	
	public NickChangeEvent(String serverName, IRCUser user, String newNick, String oldNick)
	{
		this.serverName = serverName;
		this.user = user;
		this.newNick = newNick;
		this.oldNick = oldNick;
	}
	
	public String getServerName()
	{
		return serverName;
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
