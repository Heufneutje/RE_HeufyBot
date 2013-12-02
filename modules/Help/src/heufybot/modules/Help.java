package heufybot.modules;

import java.util.ArrayList;
import java.util.Collections;

public class Help extends Module
{
	public Help()
	{
		this.name = "Help";
		this.authType = Module.AuthType.Anyone;
		
		this.triggers = new String[1];
		this.triggers[0] = bot.getConfig().getCommandPrefix() + "help";
	}
	
	public void processEvent(String source, String metadata, String triggerUser, String triggerCommand)
	{
		if ((metadata.equals("")) || (metadata.equals(" ")))
		{
			ArrayList<String> moduleNames = new ArrayList<String>();
			for(Module module : bot.getModuleInterface().getModuleList())
			{
				moduleNames.add(module.getName());
			}

			Collections.sort(moduleNames);
			String response = "Modules loaded: ";
			for (String module : moduleNames)
			{
				response += module;
				if(!moduleNames.get(moduleNames.size() - 1).equals(module))
				{
					response += ", ";
				}
			}
			bot.getIRC().cmdPRIVMSG(source, response);
		}
		else if (metadata.startsWith(" "))
		{
			String moduleName = Character.toUpperCase(metadata.substring(1).toLowerCase().charAt(0)) + metadata.substring(1).toLowerCase().substring(1);
			String help = bot.getModuleInterface().getModuleHelp(moduleName);
			if(help != null)
			{
				bot.getIRC().cmdPRIVMSG(source, help);
			}
			else
			{
				bot.getIRC().cmdPRIVMSG(source, "Module \"" + moduleName + "\" is not loaded or does not exist!");
			}
		}
	}

	@Override
	public String getHelp()
	{
		return "Commands: " + bot.getConfig().getCommandPrefix() + "help, " + bot.getConfig().getCommandPrefix() + "help <module> | Shows all modules that are currently loaded or shows help for a given module.";
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
