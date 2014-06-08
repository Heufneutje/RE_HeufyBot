package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class ServerResponseChannelEvent implements BotEvent
{
    private String serverName, line;
    private IRCChannel channel;

    public ServerResponseChannelEvent(String serverName, IRCChannel channel, String line)
    {
        this.serverName = serverName;
        this.line = line;
        this.channel = channel;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public String getLine()
    {
        return this.line;
    }

    public IRCChannel getChannel()
    {
        return this.channel;
    }

    @Override
    public String toString()
    {
        return "ServerResponseChannelEvent";
    }
}
