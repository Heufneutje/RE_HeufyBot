package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class Quit extends Module
{
	public Quit()
	{
		this.authType = AuthType.BotAdmins;
		this.apiVersion = "0.5.0";
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(quit)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if(params.size() == 1)
		{
			bot.stop("Quit command issued by " + triggerUser);
		}
		else
		{
			params.remove(0);
			bot.stop(StringUtils.join(params, " "));
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "quit (<message>) | Makes the bot quit the server.";
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