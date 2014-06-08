package heufybot.core.events.types;

import heufybot.core.IRCUser;

public class InviteEvent implements BotEvent
{
    private IRCUser inviter;
    private String serverName, invitee, channel;

    public InviteEvent(String serverName, IRCUser inviter, String invitee, String channel)
    {
        this.serverName = serverName;
        this.inviter = inviter;
        this.invitee = invitee;
        this.channel = channel;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public IRCUser getInviter()
    {
        return this.inviter;
    }

    public String getInvitee()
    {
        return this.invitee;
    }

    public String getChannel()
    {
        return this.channel;
    }

    @Override
    public String toString()
    {
        return "InviteEvent";
    }
}
