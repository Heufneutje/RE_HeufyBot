package heufybot.core.events.types;

public class NoticeEvent implements BotEvent
{
	private String serverName, source, message;
	
	public NoticeEvent(String serverName, String source, String message)
	{
		this.serverName = serverName;
		this.source = source;
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
	
	public String getSource()
	{
		return source;
	}
	
	public String toString()
	{
		return "NoticeEvent";
	}
}
