package heufybot.core.events;

import heufybot.core.events.types.*;

public abstract class EventListenerAdapter implements EventListener
{

	@Override
	public void onEvent(BotEvent event)
	{
		if(event instanceof ActionEvent)
		{
			onAction((ActionEvent) event);
		}
		else if(event instanceof ChannelNoticeEvent)
		{
			onChannelNotice((ChannelNoticeEvent) event);
		}
		else if(event instanceof CTCPRequestEvent)
		{
			onCTCPRequest((CTCPRequestEvent) event);
		}
		else if(event instanceof ErrorEvent)
		{
			onError((ErrorEvent) event);
		}
		else if(event instanceof InviteEvent)
		{
			onInvite((InviteEvent) event);
		}
		else if(event instanceof JoinEvent)
		{
			onJoin((JoinEvent) event);
		}
		else if(event instanceof KickEvent)
		{
			onKick((KickEvent) event);
		}
		else if(event instanceof MessageEvent)
		{
			onMessage((MessageEvent) event);
		}
		else if(event instanceof ModeEvent)
		{
			onModeChange((ModeEvent) event);
		}
		else if(event instanceof NickChangeEvent)
		{
			onNickChange((NickChangeEvent) event);
		}
		else if(event instanceof NoticeEvent)
		{
			onNotice((NoticeEvent) event);
		}
		else if(event instanceof PartEvent)
		{
			onPart((PartEvent) event);
		}
		else if(event instanceof PingEvent)
		{
			onPing((PingEvent) event);
		}
		else if(event instanceof PMActionEvent)
		{
			onPMAction((PMActionEvent) event);
		}
		else if(event instanceof PMMessageEvent)
		{
			onPMMessage((PMMessageEvent) event);
		}
		else if(event instanceof QuitEvent)
		{
			onQuit((QuitEvent) event);
		}
		else if(event instanceof ServerResponseChannelEvent)
		{
			onServerChannelResponse((ServerResponseChannelEvent) event);
		}
		else if(event instanceof ServerResponseEvent)
		{
			onServerResponse((ServerResponseEvent) event);
		}
		else if(event instanceof TopicEvent)
		{
			onTopicChange((TopicEvent) event);
		}
	}
	
	public void onAction(ActionEvent event)
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
}
