package heufybot.modules;

import java.util.List;

public class Nick extends Module
{
	public Nick()
	{
		this.authType = AuthType.BotAdmins;
		this.apiVersion = "0.5.0";
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(nick)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getIRC().cmdPRIVMSG(source, "Change my nick to what?");
		}
		else
		{
			bot.getIRC().cmdNICK(params.get(1));
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "nick <nickname> | Changes the bot's nickname.";
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
