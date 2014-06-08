package heufybot.core.events.types;

import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;

public class KickEvent implements BotEvent
{
    private IRCUser kicker, recipient;
    private IRCChannel channel;
    private String serverName, message;

    public KickEvent(String serverName, IRCUser recipient, IRCUser kicker, IRCChannel channel,
            String message)
    {
        this.serverName = serverName;
        this.kicker = kicker;
        this.recipient = recipient;
        this.channel = channel;
        this.message = message;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public IRCUser getKicker()
    {
        return this.kicker;
    }

    public IRCUser getRecipient()
    {
        return this.recipient;
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
        return "KickEvent";
    }
}
