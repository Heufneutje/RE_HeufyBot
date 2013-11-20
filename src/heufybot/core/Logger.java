package heufybot.core;

public class Logger 
{
	public static void log(String line)
	{
		System.out.println(line);
	}
	
	public static void error(String errorSource, String line)
	{
		System.err.println(errorSource + " - ERROR: " + line);
	}
}