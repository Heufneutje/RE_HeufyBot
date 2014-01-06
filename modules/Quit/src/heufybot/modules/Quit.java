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

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "quit | Makes the quit the server.";
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