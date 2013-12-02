package heufybot.core;

import heufybot.modules.ModuleInterface;
import heufybot.utils.FileUtils;

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
		
		moduleInterface = new ModuleInterface();
		irc.getEventListenerManager().addListener(moduleInterface);
		moduleInterface.loadModule("Say");
		moduleInterface.loadModule("Help");
		
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