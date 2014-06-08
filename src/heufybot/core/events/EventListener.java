package heufybot.core.events;

import heufybot.core.events.types.BotEvent;

public interface EventListener
{
    public void onEvent(BotEvent event);
}
