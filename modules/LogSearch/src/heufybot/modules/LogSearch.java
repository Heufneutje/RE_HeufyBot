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
		}
	}

	public String getHelp(String message)
	{
		return "";
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
