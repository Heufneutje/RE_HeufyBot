package heufybot.modules;

public class Say extends Module
{
	public Say()
	{
		this.name = "Say";
		this.authType = Module.AuthType.Anyone;
		
		this.triggers = new String[1];
		this.triggers[0] = bot.getConfig().getCommandPrefix() + "say";
	}
	
	@Override
	public void processEvent(String source, String metadata, String triggerUsed, String triggerCommand) 
	{
		if ((metadata.equals("")) || (metadata.equals(" ")))
		{
			bot.getIRC().cmdPRIVMSG(source, "[Say] Say what?");
		}
		else if (metadata.startsWith(" "))
		{
			bot.getIRC().cmdPRIVMSG(source, metadata.substring(1));
		}
	}

	public String getHelp()
	{
		return "Commands: " + bot.getConfig().getCommandPrefix() + "say <message> | Makes the bot say the given line.";
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