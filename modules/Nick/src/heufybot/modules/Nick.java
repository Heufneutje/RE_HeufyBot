package heufybot.modules;

import java.util.List;

public class Nick extends Module
{
	public Nick()
	{
		this.authType = Module.AuthType.OPs;
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

	public String getHelp()
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
