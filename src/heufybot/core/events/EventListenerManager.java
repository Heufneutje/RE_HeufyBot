package heufybot.core.events;

import heufybot.core.Logger;
import heufybot.core.events.types.BotEvent;

import java.util.HashSet;
import java.util.Set;

public class EventListenerManager
{
    protected Set<EventListener> listeners = new HashSet<EventListener>();

    public void addListener(EventListener listener)
    {
        this.listeners.add(listener);
    }

    public void removeListener(EventListener listener)
    {
        this.listeners.remove(listener);
    }

    public void dispatchEvent(BotEvent event)
    {
        try
        {
            for (EventListener curListener : this.listeners)
            {
                curListener.onEvent(event);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.error("Event Manager", "Could not dispatch event " + event);
        }
    }
}
