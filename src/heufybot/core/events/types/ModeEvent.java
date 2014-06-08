package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class ModeEvent implements BotEvent
{
    private String serverName, setter, mode;
    private IRCChannel channel;

    public ModeEvent(String serverName, String setter, IRCChannel channel, String mode)
    {
        this.serverName = serverName;
        this.setter = setter;
        this.channel = channel;
        this.mode = mode;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public String getMode()
    {
        return this.mode;
    }

    public String getSetter()
    {
        return this.setter;
    }

    public IRCChannel getChannel()
    {
        return this.channel;
    }

    @Override
    public String toString()
    {
        return "ModeEvent";
    }
}
