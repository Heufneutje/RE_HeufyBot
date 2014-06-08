package heufybot.core.events.types;

import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;

public class ActionEvent implements BotEvent
{
    private IRCUser user;
    private IRCChannel channel;
    private String serverName, message;

    public ActionEvent(String serverName, IRCUser user, IRCChannel channel, String message)
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

    public String getMessage()
    {
        return this.message;
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
        return "ActionEvent";
    }
}
