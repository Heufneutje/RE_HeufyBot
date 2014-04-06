package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class LogSearch extends Module
{
	private Searcher searcher;
	
	public LogSearch(String server)
	{
		super(server);
		
		this.authType = AuthType.Anyone;
		this.apiVersion = 60;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(lastseen)($| .*)";
		
		String rootLogPath = bot.getServer(server).getConfig().getSettingWithDefault("logPath", "logs");
		String network = bot.getServer(server).getServerInfo().getNetwork();
		
		this.searcher = new Searcher(rootLogPath + "/" + network + "/");
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
			
			bot.getServer(server).cmdPRIVMSG(source, searcher.firstSeen(source, searchTerms, true));
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
