package heufybot.core;

public class HeufyBot
{
	public final static String VERSION = "0.0.1 ALPHA";
	private Config config;
	private IRC irc;
	
	public HeufyBot(Config config)
	{
		this.config = config;
		this.irc = IRC.getInstance();
		irc.setConfig(config);
	}
	
	public void start()
	{
		if(irc.connect(config.getServer(), config.getPort()))
		{
			irc.login();
		}
	}
	
	public void stop()
	{
		irc.cmdQUIT("RE_HeufyBot " + VERSION);
		irc.disconnect(false);
	}
}