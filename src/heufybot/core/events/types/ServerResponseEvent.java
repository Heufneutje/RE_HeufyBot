package heufybot.core.events.types;

public class ServerResponseEvent implements BotEvent
{
	private String line;
	
	public ServerResponseEvent(String line)
	{
		this.line = line;
	}
	
	public String getLine()
	{
		return line;
	}
	public String toString()
	{
		return "ServerResponseEvent";
	}
}
