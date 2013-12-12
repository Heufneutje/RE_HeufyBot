package heufybot.modules;

import heufybot.utils.enums.ModuleLoaderResponse;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class ModuleLoader extends Module
{
	public ModuleLoader()
	{
		this.authType = Module.AuthType.OPs;
		this.trigger = "^" + commandPrefix + "(load|unload|reload)($| .*)";
	}

	public void processEvent(String source, String message, String triggerUser, List<String> params)
	{
		if(message.matches("^" + commandPrefix + "load.*"))
		{
			if (params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "Load what?");
			}
			else
			{
				for(int i = 1; i < params.size(); i++)
				{
					SimpleEntry<ModuleLoaderResponse, String> result = bot.getModuleInterface().loadModule(params.get(i));
					
					switch (result.getKey()) 
					{
					case Success:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + result.getValue() + "\" was successfully loaded!");
						break;
					case AlreadyLoaded:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + params.get(i) + "\" is already loaded!");
						break;
					case DoesNotExist:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + params.get(i) + "\" does not exist!");
					default:
						break;
					}
				}
			}
		}
		else if(message.matches("^" + commandPrefix + "unload.*"))
		{
			if (params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "Unload what?");
			}
			else
			{
				for(int i = 1; i < params.size(); i++)
				{
					SimpleEntry<ModuleLoaderResponse, String> result = bot.getModuleInterface().unloadModule(params.get(i));
	
					switch (result.getKey()) 
					{
					case Success:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + result.getValue() + "\" was successfully unloaded!");
						break;
					case DoesNotExist:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + params.get(i) + "\" is not loaded or does not exist!");
						break;
					default:
						break;
					}
				}
			}
		}
		else
		{
			if (params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "Reload what?");
			}
			else
			{
				for(int i = 1; i < params.size(); i++)
				{
					SimpleEntry<ModuleLoaderResponse, String> result = bot.getModuleInterface().unloadModule(params.get(i));
					
					switch (result.getKey()) 
					{
					case Success:
						bot.getModuleInterface().loadModule(params.get(i));
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + result.getValue() + "\" was successfully reloaded!");
						break;
					case DoesNotExist:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + params.get(i) + "\" is not loaded or does not exist!");
						break;
					default:
						break;
					}
				}
			}
		}
	}

	@Override
	public String getHelp(String message)
	{
		if(message.matches("load.*"))
		{
			return "Commands: " + commandPrefix + "load <module> | Load one or more modules. Seperate module names by spaces if more.";
		}
		else if(message.matches("unload.*"))
		{
			return "Commands: " + commandPrefix + "unload <module> | Unload one or more modules. Seperate module names by spaces if more.";
		}
		else if(message.matches("reload.*"))
		{
			return "Commands: " + commandPrefix + "reload <module> | Reload one or more modules. Seperate module names by spaces if more.";
		}
		
		return "Commands: " + commandPrefix + "load <module>, " + commandPrefix + "unload <module>, " + commandPrefix + "reload <module> | Load, unload or reload one or more modules. Seperate module names by spaces if more.";
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onUnload()
	{	
	}
}
