package heufybot.core;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

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
	private HashMap<String, IRCServer> servers;
	private ModuleInterface moduleInterface;
	private LoggingInterface loggingInterface;
	
	private static final HeufyBot instance = new HeufyBot();
	
	private HeufyBot()
	{
		FileUtils.touchDir("data");
		FileUtils.touchDir("modules");
		
		this.config = Config.getInstance();
		this.servers = new HashMap<String, IRCServer>();
		this.servers.put("Test", new IRCServer(config));
	}
	
	public void start()
	{
		moduleInterface = new ModuleInterface(this);
		loggingInterface = new LoggingInterface(this);
		
		this.loadModules();
		
		for(IRCServer server : servers.values())
		{
			server.getEventListenerManager().addListener(moduleInterface);
			server.getEventListenerManager().addListener(loggingInterface);
		
			if(server.connect(config.getServer(), config.getPort()))
			{
				server.login();
			}
		}
	}
	
	public void stop(String message)
	{
		for(IRCServer server : servers.values())
		{
			server.cmdQUIT(message);
			server.disconnect(false);
		}
		
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
			for(IRCServer server : servers.values())
			{
				if(server.connect(config.getServer(), config.getPort()))
				{
					server.login();
				}
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
	
	public IRCServer getIRC()
	{
		return servers.get("Test");
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
