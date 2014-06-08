package heufybot.core.events.types;

public class ServerResponseEvent implements BotEvent
{
    private String serverName, line;

    public ServerResponseEvent(String serverName, String line)
    {
        this.serverName = serverName;
        this.line = line;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public String getLine()
    {
        return this.line;
    }

    @Override
    public String toString()
    {
        return "ServerResponseEvent";
    }
}
