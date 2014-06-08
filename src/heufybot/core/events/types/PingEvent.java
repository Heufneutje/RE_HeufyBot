package heufybot.core.events.types;

public class PingEvent implements BotEvent
{
    private String serverName, message;

    public PingEvent(String serverName, String message)
    {
        this.serverName = serverName;
        this.message = message;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public String getMessage()
    {
        return this.message;
    }

    @Override
    public String toString()
    {
        return "ErrorEvent";
    }
}
