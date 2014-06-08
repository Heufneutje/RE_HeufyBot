package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class BotMessageEvent implements BotEvent
{
    private IRCUser user;
    private String target;
    private String serverName, message;

    public BotMessageEvent(String serverName, IRCUser user, String target, String message)
    {
        this.serverName = serverName;
        this.user = user;
        this.target = target;
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

    public String getTarget()
    {
        return this.target;
    }

    @Override
    public String toString()
    {
        return "BotMessageEvent";
    }
}
