package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Help extends Module
{
	public Help()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(help)($| .*)";
	}
	
	public void processEvent(String source, String message, String triggerUser, List<String> params)
	{
		if (params.size() == 1)
		{
			ArrayList<String> moduleNames = new ArrayList<String>();
			for(Module module : bot.getModuleInterface().getModuleList())
			{
				moduleNames.add(module.getClass().getSimpleName());
			}

			Collections.sort(moduleNames);
			String response = "Modules loaded: " + StringUtils.join(moduleNames, ", ");
			bot.getIRC().cmdPRIVMSG(source, response);
		}
		else
		{
			String help = bot.getModuleInterface().getModuleHelp(params.get(1));
			if(help != null)
			{
				bot.getIRC().cmdPRIVMSG(source, help);
			}
			else
			{
				bot.getIRC().cmdPRIVMSG(source, "Module \"" + params.get(1) + "\" is not loaded or does not exist!");
			}
		}
	}

	@Override
	public String getHelp()
	{
		return "Commands: " + commandPrefix + "help, " + commandPrefix + "help <module> | Shows all modules that are currently loaded or shows help for a given module. Module syntax will be as such: command <parameter>. Parameters in brackets are optional.";
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
