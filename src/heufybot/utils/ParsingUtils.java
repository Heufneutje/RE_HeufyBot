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
}
