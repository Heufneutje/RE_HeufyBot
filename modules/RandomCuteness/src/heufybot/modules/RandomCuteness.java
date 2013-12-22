package heufybot.modules;

import heufybot.utils.URLUtils;

import java.util.HashMap;
import java.util.List;

public class RandomCuteness extends Module
{
	public RandomCuteness()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(aww)($)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		try
		{
			HashMap<String, String> feedElements = URLUtils.grabRSSFeed("http://imgur.com/r/aww/rss");
			String[] titles = new String[feedElements.keySet().size()];
			titles = feedElements.keySet().toArray(titles);
			
			int id = (int) (Math.random() * titles.length);
			bot.getIRC().cmdPRIVMSG(source, titles[id] + " | " + feedElements.get(titles[id]));
		}
		catch(Exception e)
		{
			bot.getIRC().cmdPRIVMSG(source, "Something went wrong while trying to read the RSS feed.");
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "aww | Returns random cuteness from the imgur /r/aww RSS feed.";
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
