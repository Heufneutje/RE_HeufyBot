package heufybot.core.events;

import heufybot.core.events.types.ActionEvent;
import heufybot.core.events.types.BotEvent;
import heufybot.core.events.types.BotMessageEvent;
import heufybot.core.events.types.CTCPRequestEvent;
import heufybot.core.events.types.ChannelNoticeEvent;
import heufybot.core.events.types.ErrorEvent;
import heufybot.core.events.types.InviteEvent;
import heufybot.core.events.types.JoinEvent;
import heufybot.core.events.types.KickEvent;
import heufybot.core.events.types.MessageEvent;
import heufybot.core.events.types.ModeEvent;
import heufybot.core.events.types.NickChangeEvent;
import heufybot.core.events.types.NoticeEvent;
import heufybot.core.events.types.PMActionEvent;
import heufybot.core.events.types.PMMessageEvent;
import heufybot.core.events.types.PartEvent;
import heufybot.core.events.types.PingEvent;
import heufybot.core.events.types.QuitEvent;
import heufybot.core.events.types.ServerResponseChannelEvent;
import heufybot.core.events.types.ServerResponseEvent;
import heufybot.core.events.types.TopicEvent;
import heufybot.core.events.types.WhoisEvent;
import heufybot.core.events.types.WhowasEvent;

public abstract class EventListenerAdapter implements EventListener
{

    @Override
    public void onEvent(BotEvent event)
    {
        if (event instanceof ActionEvent)
        {
            this.onAction((ActionEvent) event);
        }
        else if (event instanceof BotMessageEvent)
        {
            this.onBotMessage((BotMessageEvent) event);
        }
        else if (event instanceof ChannelNoticeEvent)
        {
            this.onChannelNotice((ChannelNoticeEvent) event);
        }
        else if (event instanceof CTCPRequestEvent)
        {
            this.onCTCPRequest((CTCPRequestEvent) event);
        }
        else if (event instanceof ErrorEvent)
        {
            this.onError((ErrorEvent) event);
        }
        else if (event instanceof InviteEvent)
        {
            this.onInvite((InviteEvent) event);
        }
        else if (event instanceof JoinEvent)
        {
            this.onJoin((JoinEvent) event);
        }
        else if (event instanceof KickEvent)
        {
            this.onKick((KickEvent) event);
        }
        else if (event instanceof MessageEvent)
        {
            this.onMessage((MessageEvent) event);
        }
        else if (event instanceof ModeEvent)
        {
            this.onModeChange((ModeEvent) event);
        }
        else if (event instanceof NickChangeEvent)
        {
            this.onNickChange((NickChangeEvent) event);
        }
        else if (event instanceof NoticeEvent)
        {
            this.onNotice((NoticeEvent) event);
        }
        else if (event instanceof PartEvent)
        {
            this.onPart((PartEvent) event);
        }
        else if (event instanceof PingEvent)
        {
            this.onPing((PingEvent) event);
        }
        else if (event instanceof PMActionEvent)
        {
            this.onPMAction((PMActionEvent) event);
        }
        else if (event instanceof PMMessageEvent)
        {
            this.onPMMessage((PMMessageEvent) event);
        }
        else if (event instanceof QuitEvent)
        {
            this.onQuit((QuitEvent) event);
        }
        else if (event instanceof ServerResponseChannelEvent)
        {
            this.onServerChannelResponse((ServerResponseChannelEvent) event);
        }
        else if (event instanceof ServerResponseEvent)
        {
            this.onServerResponse((ServerResponseEvent) event);
        }
        else if (event instanceof TopicEvent)
        {
            this.onTopicChange((TopicEvent) event);
        }
        else if (event instanceof WhoisEvent)
        {
            this.onWhois((WhoisEvent) event);
        }
        else if (event instanceof WhowasEvent)
        {
            this.onWhowas((WhowasEvent) event);
        }
    }

    public void onAction(ActionEvent event)
    {

    }

    public void onBotMessage(BotMessageEvent event)
    {

    }

    public void onChannelNotice(ChannelNoticeEvent event)
    {

    }

    public void onCTCPRequest(CTCPRequestEvent event)
    {

    }

    public void onError(ErrorEvent event)
    {

    }

    public void onInvite(InviteEvent event)
    {

    }

    public void onJoin(JoinEvent event)
    {

    }

    public void onKick(KickEvent event)
    {

    }

    public void onMessage(MessageEvent event)
    {

    }

    public void onModeChange(ModeEvent event)
    {

    }

    public void onNickChange(NickChangeEvent event)
    {

    }

    public void onNotice(NoticeEvent event)
    {

    }

    public void onPart(PartEvent event)
    {

    }

    public void onPing(PingEvent event)
    {

    }

    public void onPMAction(PMActionEvent event)
    {

    }

    public void onPMMessage(PMMessageEvent event)
    {

    }

    public void onQuit(QuitEvent event)
    {

    }

    public void onServerChannelResponse(ServerResponseChannelEvent event)
    {

    }

    public void onServerResponse(ServerResponseEvent event)
    {

    }

    public void onTopicChange(TopicEvent event)
    {

    }

    public void onWhois(WhoisEvent event)
    {

    }

    public void onWhowas(WhowasEvent event)
    {

    }
}
