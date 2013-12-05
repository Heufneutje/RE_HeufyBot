package heufybot.core;

import java.util.AbstractMap.SimpleEntry;

import heufybot.modules.ModuleInterface;
import heufybot.utils.FileUtils;
import heufybot.utils.enums.ModuleLoaderResponse;

public class HeufyBot
{
	public final static String VERSION = "0.0.1";
	private Config config;
	private IRC irc;
	private ModuleInterface moduleInterface;
	
	private static final HeufyBot instance = new HeufyBot();
	
	private HeufyBot()
	{
		this.config = Config.getInstance();
		this.irc = IRC.getInstance();
		irc.setConfig(config);
	}
	
	public void start()
	{
		FileUtils.touchDir("data");
		FileUtils.touchDir("logs");
		FileUtils.touchDir("modules");
		
		moduleInterface = new ModuleInterface();
		irc.getEventListenerManager().addListener(moduleInterface);
		
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