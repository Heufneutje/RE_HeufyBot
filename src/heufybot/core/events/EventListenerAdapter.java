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
	}
	
	public void onMessage(MessageEvent event)
	{
		
	}
}
