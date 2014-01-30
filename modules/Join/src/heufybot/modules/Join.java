package heufybot.modules;

import java.util.List;

public class Join extends Module
{
	public Join()
	{
		this.authType = Module.AuthType.Anyone;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(join)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getIRC().cmdPRIVMSG(source, "Join what?");
		}
		else
		{
			if(params.size() > 2)
			{
				bot.getIRC().cmdJOIN(params.get(1), params.get(2));
			}
			else
			{
				bot.getIRC().cmdJOIN(params.get(1), "");
			}
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "join <channel> (<password>) | Makes the bot join a channel.";
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
