package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class Do extends Module
{
	public Do()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(do)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getIRC().cmdPRIVMSG(source, "Do what?");
		}
		else
		{
			params.remove(0);
			bot.getIRC().cmdACTION(source, StringUtils.join(params, " "));
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "do <message> | Makes the bot perform the given line in an action.";
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
