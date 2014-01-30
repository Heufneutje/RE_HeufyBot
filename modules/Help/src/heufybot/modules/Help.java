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
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(help)($| .*)";
	}
	
	public void processEvent(String source, String message, String triggerUser, List<String> params)
	{
		if (params.size() == 1)
		{
			ArrayList<String> moduleNames = new ArrayList<String>();
			for(Module module : bot.getModuleInterface().getModuleList())
			{
				moduleNames.add(module.toString());
			}

			Collections.sort(moduleNames);
			String response = "Modules loaded: " + StringUtils.join(moduleNames, ", ");
			bot.getIRC().cmdPRIVMSG(source, response);
		}
		else
		{
			params.remove(0);
			String helpParams = StringUtils.join(params, " ");
			String help = bot.getModuleInterface().getModuleHelp(helpParams);
			if(help != null)
			{
				bot.getIRC().cmdPRIVMSG(source, help);
			}
			else
			{
				bot.getIRC().cmdPRIVMSG(source, "Module or command matching \"" + helpParams + "\" is not loaded or does not exist!");
			}
		}
	}

	@Override
	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "help (<module>) | Shows all modules that are currently loaded or shows help for a given module. Command syntax will be as such: command <parameter>. Parameters in brackets are optional.";
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
