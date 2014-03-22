package heufybot.modules;

import java.util.List;

public class Restart extends Module
{
	public Restart()
	{
		this.authType = AuthType.BotAdmins;
		this.apiVersion = "0.5.0";
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(restart)$";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		bot.restart();
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "restart | Makes the reload its config and modules and reconnect to the server.";
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