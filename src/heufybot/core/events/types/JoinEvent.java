package heufybot.core.events.types;

import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;

public class JoinEvent implements BotEvent
{
    private String serverName;
    private IRCUser user;
    private IRCChannel channel;

    public JoinEvent(String serverName, IRCUser user, IRCChannel channel)
    {
        this.serverName = serverName;
        this.user = user;
        this.channel = channel;
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

    @Override
    public String toString()
    {
        return "JoinEvent";
    }
}
