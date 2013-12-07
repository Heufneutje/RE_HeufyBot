package heufybot.core.events.types;

public class NoticeEvent implements BotEvent
{
	private String source;
	private String message;
	
	public NoticeEvent(String source, String message)
	{
		this.source = source;
		this.message = message;
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
