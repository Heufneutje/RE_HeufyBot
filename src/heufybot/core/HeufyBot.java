package heufybot.core;

import heufybot.core.events.EventListenerAdapter;
import heufybot.core.events.types.*;

public class HeufyBot extends EventListenerAdapter
{
	public final static String VERSION = "0.0.1 ALPHA";
	private Config config;
	private IRC irc;
	
	public HeufyBot(Config config)
	{
		this.config = config;
		this.irc = IRC.getInstance();
		irc.setConfig(config);
		irc.getEventListenerManager().addListener(this);
	}
	
	public void start()
	{
		if(irc.connect(config.getServer(), config.getPort()))
		{
			irc.login();
		}
	}
	
	public void stop()
	{
		irc.cmdQUIT("RE_HeufyBot " + VERSION);
		irc.disconnect(false);
	}
	
	public void onMessage(MessageEvent event)
	{
		if(event.getMessage().contains(irc.getNickname()))
		{
			irc.cmdPRIVMSG(event.getChannel().getName(), "Are you talking about me?");
		}
	}
	
	public void onJoin(JoinEvent event)
	{
		if(!event.getUser().getNickname().equals(irc.getNickname()))
		{
			irc.cmdPRIVMSG(event.getChannel().getName(), "Welcome to the channel!");
		}
	}
}