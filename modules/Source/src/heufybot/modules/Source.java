package heufybot.modules;

import java.util.List;

public class Source extends Module
{
	public Source()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(source)$";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		bot.getIRC().cmdPRIVMSG(source, "https://github.com/Heufneutje/RE_HeufyBot/");
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
