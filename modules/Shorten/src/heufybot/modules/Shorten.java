package heufybot.modules;

import heufybot.utils.URLUtils;

import java.util.List;

public class Shorten extends Module
{
	public Shorten()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(shorten)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getIRC().cmdPRIVMSG(source, "Shorten what?");
		}
		else
		{
			String shortenedURL = URLUtils.shortenURL(params.get(1));
			if(shortenedURL == null)
			{
				bot.getIRC().cmdPRIVMSG(source, "[Shorten] Error: URL could not be shortned");
			}
			else
			{
				bot.getIRC().cmdPRIVMSG(source, "[Shorten] " + shortenedURL);
			}
		}
	}

	public String getHelp()
	{
		return "Commands: " + commandPrefix + "shorten <url> | Shortens the given URL using Googl.";
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
