package heufybot.core.events.types;

public class PingEvent implements BotEvent
{
	private String message;
	
	public PingEvent(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
	public String toString()
	{
		return "ErrorEvent";
	}
}
