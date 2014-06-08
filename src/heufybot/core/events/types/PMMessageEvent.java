package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class PMMessageEvent implements BotEvent
{
    private IRCUser user;
    private String serverName, message;

    public PMMessageEvent(String serverName, IRCUser user, String message)
    {
        this.serverName = serverName;
        this.user = user;
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

    public IRCUser getUser()
    {
        return this.user;
    }

    @Override
    public String toString()
    {
        return "PMMessageEvent";
    }
}
