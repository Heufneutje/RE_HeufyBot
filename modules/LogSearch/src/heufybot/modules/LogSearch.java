package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class LogSearch extends Module
{
	public LogSearch(String server)
	{
		super(server);
		
		this.authType = AuthType.Anyone;
		this.apiVersion = 60;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(firstseen|lastseen|lastsaw|firstsaid|lastsaid)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if(params.size() == 1)
		{
			bot.getServer(server).cmdPRIVMSG(source, "What do you want me to search for?");
		}
		else
		{
			params.remove(0);
			String searchTerms = StringUtils.join(params, " ");
			
			String rootLogPath = bot.getServer(server).getConfig().getSettingWithDefault("logPath", "logs");
			String network = bot.getServer(server).getServerInfo().getNetwork();
			Searcher searcher = new Searcher(rootLogPath + "/" + network + "/" + source + "/");
			
			if(message.matches(commandPrefix + "firstseen.*"))
			{
				bot.getServer(server).cmdPRIVMSG(source, searcher.firstSeen(source, searchTerms));
			}
			else if(message.matches(commandPrefix + "lastseen.*"))
			{
				bot.getServer(server).cmdPRIVMSG(source, searcher.lastSeen(source, searchTerms, true));
			}
			else if(message.matches(commandPrefix + "lastsaw.*"))
			{
				bot.getServer(server).cmdPRIVMSG(source, searcher.lastSeen(source, searchTerms, false));
			}
			else if(message.matches(commandPrefix + "firstsaid.*"))
			{
				bot.getServer(server).cmdPRIVMSG(source, searcher.firstSaid(source, searchTerms));
			}
			else if(message.matches(commandPrefix + "lastsaid.*"))
			{
				bot.getServer(server).cmdPRIVMSG(source, searcher.lastSaid(source, searchTerms));
			}
		}
	}

	public String getHelp(String message)
	{
		if(message.matches("firstseen"))
		{
			return "Commands: " + commandPrefix + "firstseen <nickname> | Finds the first message of the given user.";
		}
		else if(message.matches("lastseen"))
		{
			return "Commands: " + commandPrefix + "lastseen <nickname> | Finds the last message of the given user. This includes today.";
		}
		else if(message.matches("lastsaw"))
		{
			return "Commands: " + commandPrefix + "lastsaw <nickname> | Finds the last message of the given user. This does not include today.";
		}
		else if(message.matches("firstsaid"))
		{
			return "Commands: " + commandPrefix + "firstsaid <text> | Checks the log for the first time someone mentioned a given word or phrase.";
		}
		else if(message.matches("lastsaid"))
		{
			return "Commands: " + commandPrefix + "lastsaid <text> | Checks the log for the last time someone mentioned a given word or phrase.";
		}
		else
		{
			return "Commands: " + commandPrefix + "firstseen <nickname>, "
					+ commandPrefix + "lastseen <nickname>, "
					+ commandPrefix + "lastsaw <nickname>, "
					+ commandPrefix + "firstsaid <text>, "
					+ commandPrefix + "lastsaid <text> | Searches the logs for a certain name or phrase.";
		}
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
