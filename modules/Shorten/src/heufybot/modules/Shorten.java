package heufybot.modules;

import heufybot.utils.URLUtils;

import java.util.List;

public class Shorten extends Module
{
	public Shorten(String server)
	{
		super(server);
		
		this.authType = AuthType.Anyone;
		this.apiVersion = 60;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(shorten)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getServer(server).cmdPRIVMSG(source, "Shorten what?");
		}
		else
		{
			String shortenedURL = URLUtils.shortenURL(params.get(1));
			if(shortenedURL == null)
			{
				bot.getServer(server).cmdPRIVMSG(source, "Error: URL could not be shortned");
			}
			else
			{
				bot.getServer(server).cmdPRIVMSG(source, shortenedURL);
			}
		}
	}

	public String getHelp(String message)
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
