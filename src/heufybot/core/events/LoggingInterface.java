package heufybot.core.events;

import heufybot.core.HeufyBot;
import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;
import heufybot.core.Logger;
import heufybot.core.events.types.*;
import heufybot.utils.StringUtils;
import heufybot.utils.WhoisBuilder;

import java.util.Date;

public class LoggingInterface extends EventListenerAdapter
{
    private HeufyBot bot;

    public LoggingInterface(HeufyBot bot)
    {
        this.bot = bot;
    }

    @Override
    public void onAction(ActionEvent event)
    {
        Logger.log("* " + event.getUser().getNickname() + " " + event.getMessage(), event
                .getChannel().getName(), this.bot.getServer(event.getServerName()).getServerInfo()
                .getNetwork());
    }

    @Override
    public void onBotMessage(BotMessageEvent event)
    {
        IRCUser user = event.getUser();
        IRCChannel channel = this.bot.getServer(event.getServerName())
                .getChannel(event.getTarget());

        if (channel == null)
        {
            if (event.getTarget().equalsIgnoreCase("nickserv")
                    || event.getTarget().equalsIgnoreCase("chanserv"))
            {
                return;
            }

            String sourceNick = user.getNickname();
            Logger.log("<" + sourceNick + "> " + event.getMessage(), event.getTarget(), this.bot
                    .getServer(event.getServerName()).getServerInfo().getNetwork());
            return;
        }

        String modes = channel.getModesOnUser(user);
        if (!modes.equals(""))
        {
            Logger.log(
                    "<"
                            + this.bot
                            .getServer(event.getServerName())
                            .getServerInfo()
                            .getUserPrefixes()
                            .get(this.bot.getServer(event.getServerName())
                                    .getAccessLevelOnUser(channel, user))
                            + user.getNickname() + "> " + event.getMessage(), channel.getName(),
                    this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
        }
        else
        {
            Logger.log("<" + user.getNickname() + "> " + event.getMessage(), channel.getName(),
                    this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
        }
    }

    @Override
    public void onChannelNotice(ChannelNoticeEvent event)
    {
        Logger.log("[Notice] --" + event.getSource() + "-- [" + event.getChannel().getName() + "] "
                        + event.getMessage(), event.getChannel().getName(),
                this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
    }

    @Override
    public void onCTCPRequest(CTCPRequestEvent event)
    {
        Logger.log("[" + event.getUser().getNickname() + " " + event.getType() + "]");
    }

    @Override
    public void onError(ErrorEvent event)
    {
        Logger.log(event.getMessage());
    }

    @Override
    public void onInvite(InviteEvent event)
    {
        IRCUser inviter = event.getInviter();
        Logger.log("-- " + inviter.getNickname() + " (" + inviter.getLogin() + "@"
                + inviter.getHostname() + ") invites " + event.getInvitee() + " to join "
                + event.getChannel());
    }

    @Override
    public void onJoin(JoinEvent event)
    {
        IRCUser user = event.getUser();
        Logger.log(">> " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostname()
                        + ") has joined " + event.getChannel().getName(), event.getChannel().getName(),
                this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
    }

    @Override
    public void onKick(KickEvent event)
    {
        Logger.log("-- " + event.getRecipient().getNickname() + " was kicked from "
                        + event.getChannel().getName() + " by " + event.getKicker().getNickname() + " ("
                        + event.getMessage() + ")", event.getChannel().getName(),
                this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
    }

    @Override
    public void onMessage(MessageEvent event)
    {
        IRCUser user = event.getUser();
        IRCChannel channel = event.getChannel();

        String modes = channel.getModesOnUser(user);
        if (!modes.equals(""))
        {
            Logger.log(
                    "<"
                            + this.bot
                            .getServer(event.getServerName())
                            .getServerInfo()
                            .getUserPrefixes()
                            .get(this.bot.getServer(event.getServerName())
                                    .getAccessLevelOnUser(channel, user))
                            + user.getNickname() + "> " + event.getMessage(), channel.getName(),
                    this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
        }
        else
        {
            Logger.log("<" + user.getNickname() + "> " + event.getMessage(), channel.getName(),
                    this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
        }
    }

    @Override
    public void onModeChange(ModeEvent event)
    {
        if (event.getChannel() == null)
        {
            Logger.log("-- " + event.getSetter() + " sets mode: " + event.getMode());
        }
        else
        {
            Logger.log("-- " + event.getSetter() + " sets mode: " + event.getMode(), event
                    .getChannel().getName(), this.bot.getServer(event.getServerName())
                    .getServerInfo().getNetwork());
        }
    }

    @Override
    public void onNickChange(NickChangeEvent event)
    {
        for (IRCChannel channel : this.bot.getServer(event.getServerName()).getChannels())
        {
            IRCUser user = channel.getUser(event.getUser().getNickname());
            if (user != null)
            {
                Logger.log("-- " + event.getOldNick() + " is now known as " + event.getNewNick(),
                        channel.getName(), this.bot.getServer(event.getServerName())
                                .getServerInfo().getNetwork());
            }
        }
    }

    @Override
    public void onNotice(NoticeEvent event)
    {
        Logger.log("[Notice] --" + event.getSource() + "-- " + event.getMessage());
    }

    @Override
    public void onPart(PartEvent event)
    {
        IRCUser user = event.getUser();
        String channel = event.getChannel().getName();

        if (event.getMessage().equals(""))
        {
            Logger.log(
                    "<< " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostname()
                            + ") has left " + channel, channel,
                    this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
        }
        else
        {
            Logger.log(
                    "<< " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostname()
                            + ") has left " + channel + " (" + event.getMessage() + ")", channel,
                    this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
        }
    }

    @Override
    public void onPMAction(PMActionEvent event)
    {
        if (this.bot.getGlobalConfig().getSettingWithDefault("logPMs", false))
        {
            ;
        }
        {
            Logger.log("* " + event.getUser().getNickname() + " " + event.getMessage(), event
                    .getUser().getNickname(), this.bot.getServer(event.getServerName())
                    .getServerInfo().getNetwork());
        }
    }

    @Override
    public void onPMMessage(PMMessageEvent event)
    {
        if (this.bot.getGlobalConfig().getSettingWithDefault("logPMs", false))
        {
            String sourceNick = event.getUser().getNickname();
            Logger.log("<" + sourceNick + "> " + event.getMessage(), sourceNick, this.bot
                    .getServer(event.getServerName()).getServerInfo().getNetwork());
        }
    }

    @Override
    public void onQuit(QuitEvent event)
    {
        for (IRCChannel channel : this.bot.getServer(event.getServerName()).getChannels())
        {
            IRCUser user = channel.getUser(event.getUser().getNickname());
            if (user != null)
            {
                Logger.log(
                        "<< " + user.getNickname() + " (" + user.getLogin() + "@"
                                + user.getHostname() + ") has quit IRC (" + event.getMessage()
                                + ")", channel.getName(), this.bot.getServer(event.getServerName())
                                .getServerInfo().getNetwork());
            }
        }
    }

    @Override
    public void onServerChannelResponse(ServerResponseChannelEvent event)
    {
        Logger.log(event.getLine(), event.getChannel().getName(),
                this.bot.getServer(event.getServerName()).getServerInfo().getNetwork());
    }

    @Override
    public void onServerResponse(ServerResponseEvent event)
    {
        Logger.log(event.getLine());
    }

    @Override
    public void onTopicChange(TopicEvent event)
    {
        Logger.log("-- " + event.getSource() + " changes topic to \'" + event.getMessage() + "\'",
                event.getChannel().getName(), this.bot.getServer(event.getServerName())
                        .getServerInfo().getNetwork());
    }

    @Override
    public void onWhois(WhoisEvent event)
    {
        WhoisBuilder builder = event.getWhoisBuilder();

        Logger.log("-- Start of /WHOIS");
        Logger.log(builder.getNickname() + " is " + builder.getNickname() + "!"
                + builder.getLogin() + "@" + builder.getHostname());
        Logger.log(builder.getNickname() + "'s real name is " + builder.getRealname());
        Logger.log(builder.getNickname() + "'s channels: "
                + StringUtils.join(builder.getChannels(), ", "));
        Logger.log(builder.getNickname() + "'s server is " + builder.getServer() + " - "
                + builder.getServerInfo());

        if (builder.getOperPrivs() != null)
        {
            Logger.log(builder.getNickname() + " " + builder.getOperPrivs());
        }

        Logger.log(builder.getNickname() + "'s idle time is " + builder.getIdleSeconds()
                + " seconds");

        if (builder.getSignOnTime() != -1)
        {
            Logger.log(builder.getNickname() + "'s sign on time is "
                    + new Date(builder.getSignOnTime() * 1000));
        }

        Logger.log("-- End of /WHOIS");
    }
}
