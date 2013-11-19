package heufybot.startup;

import heufybot.core.Config;
import heufybot.core.HeufyBot;

public class HeufyBotInit
{
	public static void main(String[] args)
	{
		HeufyBot bot = new HeufyBot(new Config());
		bot.start();
	}
}
