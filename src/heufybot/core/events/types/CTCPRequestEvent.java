package heufybot.core.events.types;

import heufybot.core.User;

public class CTCPRequestEvent implements BotEvent
{
	private User user;
	private String type;
	
	public CTCPRequestEvent(User user, String type)
	{
		this.user = user;
		this.type = type;
	}
	
	public String getType()
	{
		return type;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public String toString()
	{
		return "CTCPRequestEvent";
	}
}
