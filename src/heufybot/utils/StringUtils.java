package heufybot.utils;

import java.util.Collection;
import java.util.Iterator;

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
}