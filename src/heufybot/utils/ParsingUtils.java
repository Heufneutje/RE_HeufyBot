package heufybot.utils;

public class ParsingUtils 
{
	public static int tryParseInt(String intString)
	{
		try
		{
			return Integer.parseInt(intString);
		}
		catch(NumberFormatException e)
		{
			return -1;
		}
	}
	
	public static long tryParseLong(String longString)
	{
		try
		{
			return Long.parseLong(longString);
		}
		catch(NumberFormatException e)
		{
			return -1;
		}
	}
}
