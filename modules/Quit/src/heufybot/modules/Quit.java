package heufybot.modules;

import java.util.List;

public class Quit extends Module
{
	public Quit()
	{
		this.authType = Module.AuthType.OPs;
		this.trigger = "^" + commandPrefix + "(quit)$";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		bot.getIRC().cmdQUIT("Quit command issued by " + triggerUser);
	}

	public String getHelp()
	{
		return "Commands: " + commandPrefix + "part (<channel> <message>) | Makes the bot part the current channel or a given one.";
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