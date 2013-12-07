package heufybot.core.events.types;

public class ServerResponseEvent implements BotEvent
{
	private String message;
	
	public ServerResponseEvent(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
	public String toString()
	{
		return "ServerResponseEvent";
	}
}
