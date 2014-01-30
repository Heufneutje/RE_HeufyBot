package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class Say extends Module
{
	public Say()
	{
		this.authType = Module.AuthType.Anyone;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(say)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getIRC().cmdPRIVMSG(source, "Say what?");
		}
		else
		{
			params.remove(0);
			bot.getIRC().cmdPRIVMSG(source, StringUtils.join(params, " "));
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "say <message> | Makes the bot say the given line.";
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