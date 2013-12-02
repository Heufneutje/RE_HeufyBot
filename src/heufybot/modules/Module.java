package heufybot.modules;

import heufybot.core.HeufyBot;

public abstract class Module 
{
	protected String name;
	protected String[] triggers;
	protected HeufyBot bot;
	
	public Module()
	{
		bot = HeufyBot.getInstance();
	}
	
	public abstract void processEvent(String source, String metadata, String triggerUser, String triggerCommand);
	
	public String getName()
	{
		return name;
	}
	
	public String[] getTriggers()
	{
		return this.triggers;
	}
	
	public abstract String getHelp();
}
