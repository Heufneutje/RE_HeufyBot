package heufybot.modules;

import java.util.List;

public class Source extends Module
{
	public Source(String server)
	{
		super(server);
		
		this.authType = AuthType.Anyone;
		this.apiVersion = 60;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(source)$";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		bot.getServer(server).cmdPRIVMSG(source, "https://github.com/Heufneutje/RE_HeufyBot/ | https://github.com/Heufneutje/RE_HeufyBot-AdditionalModules");
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "source | Provides a link to the bot's source code.";
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
