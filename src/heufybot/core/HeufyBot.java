package heufybot.core;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;

import config.GlobalConfig;
import config.GlobalConfig.PasswordType;
import config.ServerConfig;
import heufybot.core.cap.SASLCapHandler;
import heufybot.core.events.LoggingInterface;
import heufybot.modules.Module;
import heufybot.modules.ModuleInterface.ModuleLoaderResponse;
import heufybot.utils.FileUtils;

public class HeufyBot
{
	public final static String VERSION = "0.5.1";
	public final static String MODULE_API_VERSION = "0.5.0";
	
	private GlobalConfig config;
	private HashMap<String, IRCServer> servers;
	private LoggingInterface loggingInterface;
	
	private static final HeufyBot instance = new HeufyBot();
	
	private HeufyBot()
	{
		FileUtils.touchDir("config");
		FileUtils.touchDir("data");
		FileUtils.touchDir("modules");
		
		this.servers = new HashMap<String, IRCServer>();
		
		this.loadConfigs();
		this.start();
	}
	
	public void loadConfigs()
	{
		this.config = new GlobalConfig();
		if(config.loadGlobalConfig("config/globalconfig.yml"))
		{
			FileUtils.touchDir(config.getSettingWithDefault("logPath", "logs"));
			//Loaded global config file successfully
			File[] folder = new File("config").listFiles();
			for(int i = 0; i < folder.length; i++)
			{
				File file = folder[i];
				if(!file.getName().equals("globalconfig.yml") && file.getName().endsWith(".yml"))
				{
					//We found a config file. Assume it's a server config
					ServerConfig serverConfig = new ServerConfig();
					if(serverConfig.loadServerConfig(file.getPath(), serverConfig))
					{
						int serverID = 1;
						while(servers.containsKey(serverConfig.getSettingWithDefault("server" + serverID, "irc.foo.bar")))
						{
							serverID++;
						}
						
						String serverName = serverConfig.getSettingWithDefault("server" + serverID, "irc.foo.bar");
						IRCServer server = new IRCServer(serverName, serverConfig);
						servers.put(serverName, server);
						
						FileUtils.touchDir(serverConfig.getSettingWithDefault("logPath", "logs"));
					}
				}
			}
		}
	}
	
	public void start()
	{
		//moduleInterface = new ModuleInterface(this);
		loggingInterface = new LoggingInterface(this);
		
		//this.loadModules();
		
		for(IRCServer server : servers.values())
		{
			//server.getEventListenerManager().addListener(moduleInterface);
			server.getEventListenerManager().addListener(loggingInterface);
			
			ServerConfig sConfig = server.getConfig();
			
			if(sConfig.getSettingWithDefault("passwordType", PasswordType.None) == PasswordType.SASL)
			{
				SASLCapHandler handler = new SASLCapHandler(sConfig.getSettingWithDefault("username", "RE_HeufyBot"), sConfig.getSettingWithDefault("password", ""));
				server.getConfig().getCapHandlers().add(handler);
			}
			
			if(server.connect(sConfig.getSettingWithDefault("server", "irc.foo.bar"), sConfig.getSettingWithDefault("port", 6667)))
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
		//this.unloadModules();
		
		Logger.log("*** Stopping...");
	}
	
	public void restart()
	{
		//Disconnect from the server
		this.stop("Restarting...");
		
		//Reload modules
		//this.loadModules();
		
		//Reload config and reconnect
		this.loadConfigs();
		this.start();
	}
	
	public void loadModules(IRCServer server)
	{
		Logger.log("*** Loading modules...");
		
		for(String module : server.getConfig().getSettingWithDefault("modules", new ArrayList<String>()))
		{
			SimpleEntry<ModuleLoaderResponse, String> result = server.getModuleInterface().loadModule(module);
			
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
	
	public void unloadModules(IRCServer server)
	{
		Logger.log("*** Unloading modules...");
		
		Module[] loadedModules = new Module[server.getModuleInterface().getModuleList().size()];
		loadedModules = server.getModuleInterface().getModuleList().toArray(loadedModules);
		
		for(int i = 0; i < loadedModules.length; i++)
		{
			Module module = loadedModules[i];
			SimpleEntry<ModuleLoaderResponse, String> result = server.getModuleInterface().unloadModule(module.toString());

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
	
	public IRCServer getServer(String name)
	{
		return servers.get(name);
	}
	
	public static HeufyBot getInstance()
	{
		return instance;
	}
	
	public GlobalConfig getGlobalConfig()
	{
		return config;
	}
}
