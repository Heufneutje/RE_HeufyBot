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
        return this.serverName;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getSource()
    {
        return this.source;
    }

    @Override
    public String toString()
    {
        return "NoticeEvent";
    }
}
