package heufybot.core.events.types;

import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;

public class ActionEvent implements BotEvent
{
	private IRCUser user;
	private IRCChannel channel;
	private String serverName, message;
	
	public ActionEvent(String serverName, IRCUser user, IRCChannel channel, String message)
	{
		this.serverName = serverName;
		this.user = user;
		this.channel = channel;
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
	
	public IRCChannel getChannel()
	{
		return channel;
	}
	
	public String toString()
	{
		return "ActionEvent";
	}
}
