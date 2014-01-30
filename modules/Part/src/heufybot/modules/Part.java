package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class Part extends Module
{
	public Part()
	{
		this.authType = Module.AuthType.Anyone;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(part)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getIRC().cmdPART(source, "");
		}
		else
		{
			params.remove(0);
			if(params.size() > 1)
			{
				String channel = params.remove(0);
				bot.getIRC().cmdPART(channel, StringUtils.join(params, " "));
			}
			else
			{
				bot.getIRC().cmdPART(params.get(0), "");
			}
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "part (<channel> <message>) | Makes the bot part the current channel or a given one.";
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
