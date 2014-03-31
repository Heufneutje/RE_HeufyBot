package heufybot.startup;

import heufybot.core.HeufyBot;

public class HeufyBotInit
{
	public static void main(String[] args)
	{
		HeufyBot bot = HeufyBot.getInstance();
		bot.loadConfigs();
		bot.start();
	}
}
