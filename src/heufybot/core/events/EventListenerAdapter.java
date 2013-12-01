package heufybot.core.events;

import heufybot.core.events.types.*;

public abstract class EventListenerAdapter implements EventListener
{

	@Override
	public void onEvent(BotEvent event)
	{
		if(event instanceof MessageEvent)
		{
			onMessage((MessageEvent) event);
		}
		else if(event instanceof JoinEvent)
		{
			onJoin((JoinEvent) event);
		}
	}
	
	public void onMessage(MessageEvent event)
	{
		
	}
	
	public void onJoin(JoinEvent event)
	{
		
	}
}
