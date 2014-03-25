package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class CTCPRequestEvent implements BotEvent
{
	private IRCUser user;
	private String type;
	
	public CTCPRequestEvent(IRCUser user, String type)
	{
		this.user = user;
		this.type = type;
	}
	
	public String getType()
	{
		return type;
	}
	
	public IRCUser getUser()
	{
		return user;
	}
	
	public String toString()
	{
		return "CTCPRequestEvent";
	}
}
