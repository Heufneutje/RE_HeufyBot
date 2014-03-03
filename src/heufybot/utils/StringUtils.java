package heufybot.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StringUtils 
{
	public static String join(Collection<String> strings, String sep) 
	{
		StringBuilder builder = new StringBuilder();
		Iterator<String> itr = strings.iterator();
		while (itr.hasNext()) 
		{
			builder.append(itr.next());
			if (itr.hasNext())
			{
				builder.append(sep);
			}
		}
		return builder.toString();
	}
	
	public static List<String> parseStringtoList(String input, String split)
	{
		List<String> list = new ArrayList<String>();
		String[] splitted = input.split(split);
		for(int i = 0; i < splitted.length; i++)
		{
			list.add(splitted[i]);
		}
		return list;
	}
	
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
	
	public static String removeColors(String line)
	{
		int length = line.length();
		StringBuilder buffer = new StringBuilder();
		int i = 0;
		while (i < length) 
		{
			char ch = line.charAt(i);
			if (ch == '\u0003')
			{
				i++;
				if (i < length)
				{
					ch = line.charAt(i);
					if (Character.isDigit(ch))
					{
						i++;
						if (i < length) 
						{
							ch = line.charAt(i);
							if (Character.isDigit(ch))
							{
								i++;
							}
						}
						if (i < length)
						{
							ch = line.charAt(i);
							if (ch == ',') 
							{
								i++;
								if (i < length)
								{
									ch = line.charAt(i);
									if (Character.isDigit(ch))
									{
										i++;
										if (i < length)
										{
											ch = line.charAt(i);
											if (Character.isDigit(ch))
											{
												i++;
											}
										}
									} 
									else
									{
										i--;
									}
								} 
								else
								{
									i--;
								}
							}
						}
					}
				}
			} 
			else if (ch == '\u000f')
			{
				i++;
			}
			else 
			{
				buffer.append(ch);
				i++;
			}
		}
		return buffer.toString();
	}
	
	public static String removeFormatting(String line)
	{
		int length = line.length();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < length; i++)
		{
			char ch = line.charAt(i);
			if (ch == '\u000f' || ch == '\u0002' || ch == '\u001f' || ch == '\u0016')
			{
				// Don't add this character.
			} 
			else
			{
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}
	
	public static String removeFormattingAndColors(String line)
	{
		return removeFormatting(removeColors(line));
	}
	
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