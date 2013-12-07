package heufybot.utils;

public class RegexUtils
{
	public static String escapeRegex(String regex)
	{
		return regex.replaceAll("\\*", "\\*").
				replaceAll("\\+", "\\\\+").
				replaceAll("\\|", "\\\\|").
				replaceAll("\\{", "\\\\{").
				replaceAll("\\[", "\\\\[").
				replaceAll("\\(", "\\\\(").
				replaceAll("\\)", "\\\\)").
				replaceAll("\\^", "\\\\^").
				replaceAll("\\$", "\\\\$").
				replaceAll("\\.", "\\\\.").
				replaceAll("\\#", "\\\\#").
				replaceAll("\\~", "").
				replaceAll(" ", "");
	}
}