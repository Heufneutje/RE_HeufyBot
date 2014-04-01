package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.util.Iterator;
import java.util.List;

public class Ignore extends Module
{
	private List<String> ignoreList;
	private String ignoreListPath;
	
	public Ignore(String server)
	{
		super(server);
		
		this.authType = AuthType.BotAdmins;
		this.apiVersion = 60;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(ignore|unignore)($| .*)";
		
		this.ignoreListPath = "data/" + bot.getServer(server).getServerInfo().getNetwork() + "/ignorelist.txt";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if(message.toLowerCase().matches("^" + commandPrefix + "ignore.*"))
		{
			if(params.size() == 1)
			{
				bot.getServer(server).cmdPRIVMSG(source, "Users currently ignored: " + StringUtils.join(ignoreList, ", "));
			}
			else
			{
				boolean match = false;
				String nick = params.get(1);
				
	  	  		for(String ignore : ignoreList)
	  	  		{
	  	  			if(ignore.equalsIgnoreCase(nick))
	  	  			{
	  	  				match = true;
	  	  			}
	  	  		}
	  	  		if(match)
	  	  		{
	  	  			bot.getServer(server).cmdPRIVMSG(source, nick + " is already on the ignore list!");
	  	  		}
	  	  		else
	  	  		{
	  	  			ignoreList.add(nick);
	  	  			FileUtils.writeFileAppend(ignoreListPath, nick + "\n");
	  	  			bot.getServer(server).cmdPRIVMSG(source, nick + " was added to the ignore list.");
	  	  		}
			}
		}
		else if(message.toLowerCase().matches("^" + commandPrefix + "unignore.*"))
		{
			if(params.size() == 1)
			{
				bot.getServer(server).cmdPRIVMSG(source, "Who do you want me to unignore?");
			}
			else
			{
				String nick = params.get(1);
	  	  		boolean match = false;
	  	  		
	  	  		for(Iterator<String> iter = ignoreList.iterator(); iter.hasNext();)
	  	  		{
	  	  			String ignore = iter.next();
	  	  			if(ignore.equalsIgnoreCase(nick))
	  	  			{
	  	  				iter.remove();
	  	  				match = true;
	  	  			}
	  	  		}
	  	  		if(match)
	  	  		{
	  	  			FileUtils.deleteFile(ignoreListPath);
	  	  			FileUtils.touchFile(ignoreListPath);
		  	  		for(String ignore : ignoreList)
		  	  		{
		  	  			FileUtils.writeFileAppend(ignoreListPath, ignore + "\n");
		  	  		}
		  	  		//bot.setIgnoreList(ignoreList);
		  	  		bot.getServer(server).cmdPRIVMSG(source, nick + " was removed from the ignore list.");
	  	  		}
	  	  		else
	  	  		{
	  	  			bot.getServer(server).cmdPRIVMSG(source, nick + " is not on the ignore list!");
	  	  		}
			}
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "ignore <nickname>, " + commandPrefix + "unignore <nickname> | Add or remove a nickname from the ignore list.";
	}

	@Override
	public void onLoad() 
	{
		FileUtils.touchFile(ignoreListPath);
		ignoreList = StringUtils.parseStringtoList(FileUtils.readFile(ignoreListPath), "\n");
		bot.getServer(server).getModuleInterface().setIgnores(ignoreList);
	}

	@Override
	public void onUnload()
	{
		ignoreList.clear();
	}
}
