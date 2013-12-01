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
}