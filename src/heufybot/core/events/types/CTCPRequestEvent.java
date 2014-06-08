package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class CTCPRequestEvent implements BotEvent
{
    private String serverName;
    private IRCUser user;
    private String type;

    public CTCPRequestEvent(String serverName, IRCUser user, String type)
    {
        this.serverName = serverName;
        this.user = user;
        this.type = type;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public String getType()
    {
        return this.type;
    }

    public IRCUser getUser()
    {
        return this.user;
    }

    @Override
    public String toString()
    {
        return "CTCPRequestEvent";
    }
}
