package heufybot.core.events.types;

public class ErrorEvent implements BotEvent
{
	private String message;
	
	public ErrorEvent(String message)
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
