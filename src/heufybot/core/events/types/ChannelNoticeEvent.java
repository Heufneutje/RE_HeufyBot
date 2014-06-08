package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class ChannelNoticeEvent implements BotEvent
{
    private String serverName;
    private String source;
    private IRCChannel channel;
    private String message;

    public ChannelNoticeEvent(String serverName, String source, IRCChannel channel, String message)
    {
        this.serverName = serverName;
        this.source = source;
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

    public String getSource()
    {
        return this.source;
    }

    public IRCChannel getChannel()
    {
        return this.channel;
    }

    @Override
    public String toString()
    {
        return "ChannelNoticeEvent";
    }
}
