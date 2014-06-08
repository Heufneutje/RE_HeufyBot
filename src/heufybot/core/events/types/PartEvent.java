package heufybot.core.events.types;

import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;

public class PartEvent implements BotEvent
{
    private IRCUser user;
    private IRCChannel channel;
    private String serverName, message;

    public PartEvent(String serverName, IRCUser user, IRCChannel channel, String message)
    {
        this.serverName = serverName;
        this.user = user;
        this.channel = channel;
        this.message = message;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public IRCUser getUser()
    {
        return this.user;
    }

    public IRCChannel getChannel()
    {
        return this.channel;
    }

    public String getMessage()
    {
        return this.message;
    }

    @Override
    public String toString()
    {
        return "PartEvent";
    }
}
