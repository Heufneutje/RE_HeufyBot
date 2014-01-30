package heufybot.modules;

import java.util.List;

import heufybot.core.HeufyBot;

/**
 * Abstract Module class.
 * All modules must extend this class in order to be loadable.
 * @author Heufneutje
 * @version 1.0
 */
public abstract class Module 
{
	
	public enum AuthType
	{
		Anyone, OPs
	}
	
	public enum TriggerType
	{
		Message, Action;
	}
	
	/**
	 * This variable is to be a regex. An example of a trigger set in the constructor of a module would be:<br/>
	 * <i><pre>    this.trigger = "^" + commandPrefix + "(say)($| .*)";</pre></i>
	 * The trigger is not case sensitive. The above trigger ignores any suffix after the command.
	 * It is possible to trigger on more than one command, as shown here:
	 * <i><pre>    this.trigger = "^" + commandPrefix + "(load|unload|reload)($| .*)";</pre></i>
	 * 
	 */
	protected String trigger;
	/**
	 * This is set by the abstract constructor, and must be used in the trigger.
	 * It is what users will input before a command in order for the bot to recognize it as a command.
	 */
	protected String commandPrefix;
	/**
	 * This determines whether or not the module should be triggered on every message.
	 * This is useful for automatic commands, like checking when a certain person talks.
	 */
	protected boolean triggerOnEveryMessage;
	/**
	 * A reference to the static {@link HeufyBot} instance.
	 */	
	protected HeufyBot bot;
	/**
	 * Who can invoke the module.
	 * If Anyone is used, any IRC user is allowed to invoke the module.
	 * If OPs is used, only channel operators and bot admins are allowed to invoke the module.
	 */
	protected AuthType authType;
	
	/**
	 * What types of events trigger the module.
	 * Possible triggers are: Message, PM, Action and PMAction
	 */
	protected TriggerType[] triggerTypes;
	
	public Module()
	{
		bot = HeufyBot.getInstance();
		commandPrefix = bot.getConfig().getCommandPrefix();
	}
	
	/**
	 * This method gets called when the module command triggers.
	 * @param source The name of the channel from which the command originated.
	 * @param message The message received, with any command, without username or channel information.
	 * @param triggerUser The IRC username that entered the command
	 * @param params The same as message, split at " ". The first index can be checked for the trigger.
	 */
	public abstract void processEvent(String source, String message, String triggerUser, List<String> params);
	
	public String getTrigger()
	{
		return this.trigger;
	}
	
	public AuthType getAuthType()
	{
		return authType;
	}
	
	public TriggerType[] getTriggerTypes()
	{
		return triggerTypes;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Every module can be checked for syntax by the help module. This is the method that gets called to facilitate that.
	 * @param message The message received. It is sent to provide help for possible subcommands
	 * @return The string to be printed by the help module when a user calls (commandPrefix)help yourmodule
	 */
	public abstract String getHelp(String message);
	
	/**
	 * Gets called when the module is loaded.
	 * Use this to make sure any necessary resources exist, such as folders or files.
	 */
	public abstract void onLoad();
	
	/**
	 * Use this to destroy any resources no longer needed after the module is unloaded.
	 * Do not use this to destroy resources that would be recreated by onLoad().
	 */
	public abstract void onUnload();
	
	public boolean getTriggerOnEveryMessage()
	{
		return triggerOnEveryMessage;
	}
}
