package heufybot.modules;

import java.util.List;

import heufybot.core.HeufyBot;

/**
 * This abstract class provides an interface for any module used by the bot.
 * All modules must extend this class in order to be loadable.
 * @author Stefan "Heufneutje" Frijters
 */
public abstract class Module 
{
	
	public enum AuthType
	{
		Anyone, BotAdmins
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
	 * If OPs is used, only bot admins are allowed to invoke the module.
	 */
	protected AuthType authType;
	
	protected String commandPrefix;
	
	/**
	 * What types of events trigger the module.
	 * Possible triggers are: Message and Action
	 */
	protected TriggerType[] triggerTypes;
	
	/**
	 * The module API version this module is using.
	 * This must match up with the bot's module API version for the module to be loadable.
	 */
	protected String apiVersion = "0.0.0";
	
	public Module()
	{
		this.bot = HeufyBot.getInstance();
		this.commandPrefix = bot.getGlobalConfig().getSettingWithDefault("commandPrefix", "~");
	}
	
	/**
	 * This method gets called when the module command triggers.
	 * @param source The name of the channel from which the command originated.
	 * @param message The message received, with any command, without username or channel information.
	 * @param triggerUser The IRC username that entered the command
	 * @param params The same as message, split at " ". The first index can be checked for the trigger.
	 */
	public abstract void processEvent(String source, String message, String triggerUser, List<String> params);
	
	/**
	 * @return The module API version this module is using
	 */
	public String getAPIVersion()
	{
		return apiVersion;
	}
	
	/**
	 * @return The trigger for this module
	 */
	public String getTrigger()
	{
		return this.trigger;
	}
	
	/**
	 * @return The module's authentication type (Anyone or OPs)
	 */
	public AuthType getAuthType()
	{
		return authType;
	}
	
	/**
	 * @return An array with all trigger types this module supports
	 */
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
	 * Automatic method that gets called by the ModuleInterface when the module is being loaded.
	 * Use this to make sure any necessary resources exist, such as folders or files.
	 */
	public abstract void onLoad();
	
	/**
	 * Automatic method that gets called by the ModuleInterface when the module is being loaded.
	 * It is typically used to save resources used by the module to a file.
	 */
	public abstract void onUnload();
	
	/**
	 * @return Whether or not this module will be triggered on any message
	 */
	public boolean getTriggerOnEveryMessage()
	{
		return triggerOnEveryMessage;
	}
}
