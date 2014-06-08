package heufybot.core.events.types;

import heufybot.core.IRCChannel;

public class TopicEvent implements BotEvent
{
    private String serverName, source, topic;
    private IRCChannel channel;

    public TopicEvent(String serverName, String source, IRCChannel channel, String topic)
    {
        this.serverName = serverName;
        this.source = source;
        this.channel = channel;
        this.topic = topic;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public String getMessage()
    {
        return this.topic;
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
        return "TopicEvent";
    }
}
