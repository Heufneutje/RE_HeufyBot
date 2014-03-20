package heufybot.modules;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import heufybot.modules.Module;
import heufybot.utils.FileUtils;
import heufybot.utils.URLUtils;

public class NowPlaying extends Module
{
	private HashMap<String, String> userLinks;
	private final String linksPath = "data/nplinks.txt";
	
	public NowPlaying()
	{
		this.authType = Module.AuthType.Anyone;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(np|nowplaying|nplink)($| .*)";
		
		this.userLinks = new HashMap<String, String>();
	}
	
	@Override
	public String getHelp(String message)
	{
		if(message.matches("np"))
		{
			return bot.getConfig().getCommandPrefix() + "np (<user>) | Returns your currently playing music (from LastFM). You can also supply a specific username to check.";
		}
		else if(message.matches("nplink"))
		{
			return bot.getConfig().getCommandPrefix() + "nplink <LastFM name> - Links the specified LastFM account name to your IRC name.";
		}
		
		return bot.getConfig().getCommandPrefix() + "np (<user>), " + bot.getConfig().getCommandPrefix() + "nplink <LastFM name> | Returns your or someone else's currently playing music (from LastFM) or link your nickname to a LastFM name.";
	}

	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if(message.toLowerCase().matches("^" + bot.getConfig().getCommandPrefix() + "nplink.*"))
		{
			if(params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "You must provide a LastFM name to link to your nickname.");
			}
			else
			{
				String link = params.get(1).replaceAll("=", "");
				userLinks.put(triggerUser.toLowerCase(), link);
				writeLinks();
				
				bot.getIRC().cmdPRIVMSG(source, "The nickname \"" + triggerUser + "\" is now linked to LastFM name \"" + link + "\".");
			}
		}
		else if(message.toLowerCase().matches("^" + bot.getConfig().getCommandPrefix() + "(np|nowplaying).*"))
		{
			String name = "";
			if(params.size() == 1)
			{
				name = triggerUser.toLowerCase();
			}
			else
			{
				name = params.get(1).toLowerCase();
			}
			
			if(userLinks.containsKey(name))
			{
				name = userLinks.get(name);
			}
			
			String url = "http://ws.audioscrobbler.com/1.0/user/" + name + "/recenttracks.rss";
			LinkedHashMap<String, String> results = URLUtils.grabRSSFeed(url);
			
			if(results == null)
			{
				bot.getIRC().cmdPRIVMSG(source, "No user with the name \"" + name + "\" could be found on LastFM.");
			}
			else if(results.size() == 0)
			{
				bot.getIRC().cmdPRIVMSG(source, "No recently played tracks for user \"" + name + "\" could be found on LastFM.");
			}
			else
			{
				String[] titles = new String[results.keySet().size()];
				titles = results.keySet().toArray(titles);
				String lastEntryTitle = titles[0];
				
				String lastEntryLink = results.get(lastEntryTitle);
				String[] splittedTitle = lastEntryTitle.split("–");
				
				String artist = splittedTitle[0].trim();
				String song = splittedTitle[1].trim();
				String link = URLUtils.shortenURL(lastEntryLink);
				
				bot.getIRC().cmdPRIVMSG(source, "\"" + song + "\" by " + artist + " | " + link);
			}
		}
	}

	private void writeLinks()
	{
		String result = "";
		for(String user : userLinks.keySet())
		{
			result += user + "=" + userLinks.get(user) + "\n";
		}
		FileUtils.writeFile(linksPath, result);
	}
	
	private void readLinks()
	{
		String[] locationArray = FileUtils.readFile(linksPath).split("\n");
		if(locationArray[0].length() > 0)
		{
			for(int i = 0; i < locationArray.length; i++)
			{
				String[] location = locationArray[i].split("=");
				userLinks.put(location[0], location[1]);
			}
		}
	}
	
	@Override
	public void onLoad() 
	{
		FileUtils.touchFile(linksPath);
		readLinks();
	}

	@Override
	public void onUnload() 
	{
		writeLinks();
	}
}