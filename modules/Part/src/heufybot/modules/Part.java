package heufybot.modules;

import java.util.List;

public class Part extends Module
{
	public Part()
	{
		this.authType = Module.AuthType.Anyone;
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
			if(params.size() > 2)
			{
				bot.getIRC().cmdPART(params.get(1), params.get(2));
			}
			else
			{
				bot.getIRC().cmdPART(params.get(1), "");
			}
		}
	}

	public String getHelp()
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
