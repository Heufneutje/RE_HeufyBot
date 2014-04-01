package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class PMActionEvent implements BotEvent
{
	private IRCUser user;
	private String serverName, message;
	
	public PMActionEvent(String serverName, IRCUser user, String message)
	{
		this.serverName = serverName;
		this.user = user;
		this.message = message;
	}
	
	public String getServerName()
	{
		return serverName;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public IRCUser getUser()
	{
		return user;
	}
	
	public String toString()
	{
		return "PMActionEvent";
	}
}
