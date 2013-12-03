package heufybot.core;

import heufybot.modules.ModuleInterface;
import heufybot.utils.FileUtils;
import heufybot.utils.enums.ModuleLoaderResponse;

public class HeufyBot
{
	public final static String VERSION = "0.0.1 ALPHA";
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

		for(int i = 0; i < config.getModulesToLoad().length; i++)
		{
			ModuleLoaderResponse result = moduleInterface.loadModule(config.getModulesToLoad()[i]);
			switch(result)
			{
			case Success: Logger.log("+++ Module " + config.getModulesToLoad()[i] + " was loaded");
				break;
			case AlreadyLoaded: Logger.error("Module Loader", "Module " + config.getModulesToLoad()[i] + " is already loaded");
				break;
			case DoesNotExist: Logger.error("Module Loader", "Module " + config.getModulesToLoad()[i] + " does not exist");
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