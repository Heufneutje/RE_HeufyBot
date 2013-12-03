package heufybot.modules;

import java.util.List;

import heufybot.core.HeufyBot;

public abstract class Module 
{
	public enum AuthType
	{
		Anyone, OPs
	}
	
	protected String trigger;
	protected String commandPrefix;
	protected HeufyBot bot;
	protected AuthType authType;
	
	public Module()
	{
		bot = HeufyBot.getInstance();
		commandPrefix = bot.getConfig().getCommandPrefix();
	}
	
	public abstract void processEvent(String source, String message, String triggerUser, List<String> params);
	
	public String getTrigger()
	{
		return this.trigger;
	}
	
	public AuthType getAuthType()
	{
		return authType;
	}
	
	public abstract String getHelp();
	
	public abstract void onLoad();
	
	public abstract void onUnload();
}
