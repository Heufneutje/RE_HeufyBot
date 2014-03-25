package heufybot.core;

import java.util.AbstractMap.SimpleEntry;

import heufybot.core.events.LoggingInterface;
import heufybot.modules.Module;
import heufybot.modules.ModuleInterface;
import heufybot.modules.ModuleInterface.ModuleLoaderResponse;
import heufybot.utils.FileUtils;

public class HeufyBot
{
	public final static String VERSION = "0.5.1";
	public final static String MODULE_API_VERSION = "0.5.0";
	
	private Config config;
	private IRC irc;
	private ModuleInterface moduleInterface;
	private LoggingInterface loggingInterface;
	
	private static final HeufyBot instance = new HeufyBot();
	
	private HeufyBot()
	{
		FileUtils.touchDir("data");
		FileUtils.touchDir("modules");
		
		this.config = Config.getInstance();
		this.irc = new IRC();
		irc.setConfig(config);
	}
	
	public void start()
	{
		moduleInterface = new ModuleInterface(this);
		loggingInterface = new LoggingInterface(this);
		irc.getEventListenerManager().addListener(moduleInterface);
		irc.getEventListenerManager().addListener(loggingInterface);
		
		this.loadModules();

		if(irc.connect(config.getServer(), config.getPort()))
		{
			irc.login();
		}
	}
	
	public void stop(String message)
	{
		irc.cmdQUIT(message);
		irc.disconnect(false);
		
		//Unload modules
		this.unloadModules();
		
		Logger.log("*** Stopping...");
	}
	
	public void restart()
	{
		//Disconnect from the server
		this.stop("Restarting...");
		
		//Reload modules
		this.loadModules();

		//Reload config and reconnect
		if(config.loadConfigFromFile("settings.yml"))
		{
			if(irc.connect(config.getServer(), config.getPort()))
			{
				irc.login();
			}
		}
	}
	
	public void loadModules()
	{
		Logger.log("*** Loading modules...");
		
		for(String module : config.getModulesToLoad())
		{
			SimpleEntry<ModuleLoaderResponse, String> result = moduleInterface.loadModule(module);
			
			switch(result.getKey())
			{
			case Success: Logger.log(" -  Module \"" + result.getValue() + "\" was loaded");
				break;
			case AlreadyLoaded: Logger.error("Module Loader", "Module \"" + module + "\" is already loaded");
				break;
			case DoesNotExist: Logger.error("Module Loader", "Module \"" + module + "\" does not exist");
				break;
			case APIVersionDoesNotMatch: 
				String moduleVersion = result.getValue().split(" ")[0];
				String apiVersion = result.getValue().split(" ")[1];
				Logger.error("Module Loader", "Module \"" + module + "\" could not be loaded. Its module API version (" + moduleVersion + ") does not match the bot's API version (" + apiVersion + ")");
				break;
			default:
				break;
			}
		}
	}
	
	public void unloadModules()
	{
		Logger.log("*** Unloading modules...");
		
		Module[] loadedModules = new Module[moduleInterface.getModuleList().size()];
		loadedModules = moduleInterface.getModuleList().toArray(loadedModules);
		
		for(int i = 0; i < loadedModules.length; i++)
		{
			Module module = loadedModules[i];
			SimpleEntry<ModuleLoaderResponse, String> result = moduleInterface.unloadModule(module.toString());

			switch (result.getKey()) 
			{
			case Success:
				Logger.log(" -  Module " + result.getValue() + " was unloaded");
				break;
			case DoesNotExist:
				//If for whatever reason a loaded module doesn't exist
				Logger.error("Module Loader", "Module " + module + " is already unloaded or does not exist");
				break;
			default:
				break;
			}
		}
	}
	
	public IRC getIRC()
	{
		return irc;
	}
	
	public ModuleInterface getModuleInterface()
	{
		return moduleInterface;
	}
	
	public static HeufyBot getInstance()
	{
		return instance;
	}
	
	public Config getConfig()
	{
		return config;
	}
}
