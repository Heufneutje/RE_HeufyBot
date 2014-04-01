package heufybot.modules;

import java.util.List;

public class Nick extends Module
{
	public Nick(String server)
	{
		super(server);
		
		this.authType = AuthType.BotAdmins;
		this.apiVersion = 60;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(nick)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getServer(server).cmdPRIVMSG(source, "Change my nick to what?");
		}
		else
		{
			bot.getServer(server).cmdNICK(params.get(1));
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "nick <nickname> | Changes the bot's nickname.";
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
