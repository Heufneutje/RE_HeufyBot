package heufybot.startup;

import heufybot.core.HeufyBot;

public class HeufyBotInit
{
	public static void main(String[] args)
	{
		HeufyBot bot = HeufyBot.getInstance();
		
		if(bot.getConfig().loadConfigFromFile("settings.yml"))
		{
			bot.start();
		}
	}
}
