package heufybot.core;

import java.util.AbstractMap.SimpleEntry;

import heufybot.core.events.LoggingInterface;
import heufybot.modules.ModuleInterface;
import heufybot.utils.FileUtils;
import heufybot.utils.enums.ModuleLoaderResponse;

public class HeufyBot
{
	public final static String VERSION = "0.2.0";
	private Config config;
	private IRC irc;
	private ModuleInterface moduleInterface;
	private LoggingInterface loggingInterface;
	
	private static final HeufyBot instance = new HeufyBot();
	
	private HeufyBot()
	{
		FileUtils.touchDir("data");
		FileUtils.touchDir("logs");
		FileUtils.touchDir("modules");
		
		this.config = Config.getInstance();
		this.irc = IRC.getInstance();
		irc.setConfig(config);
	}
	
	public void start()
	{
		moduleInterface = new ModuleInterface(this);
		loggingInterface = new LoggingInterface(this);
		irc.getEventListenerManager().addListener(moduleInterface);
		irc.getEventListenerManager().addListener(loggingInterface);
		
		Logger.log("*** Loading modules...");
		
		for(String module : config.getModulesToLoad())
		{
			SimpleEntry<ModuleLoaderResponse, String> result = moduleInterface.loadModule(module);
			
			switch(result.getKey())
			{
			case Success: Logger.log(" -  Module " + result.getValue() + " was loaded");
				break;
			case AlreadyLoaded: Logger.error("Module Loader", "Module " + module + " is already loaded");
				break;
			case DoesNotExist: Logger.error("Module Loader", "Module " + module + " does not exist");
				break;
			default:
				break;
			}
		}

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