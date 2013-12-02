package heufybot.modules;

import heufybot.core.HeufyBot;

public abstract class Module 
{
	public enum AuthType
	{
		Anyone, OPs
	}
	
	protected String name;
	protected String[] triggers;
	protected HeufyBot bot;
	protected AuthType authType;
	
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
	
	public AuthType getAuthType()
	{
		return authType;
	}
	
	public abstract String getHelp();
	
	public abstract void onLoad();
	
	public abstract void onUnload();
}
