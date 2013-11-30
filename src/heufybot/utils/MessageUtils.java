package heufybot.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MessageUtils
{
	public static List<String> tokenizeLine(String input)
	{
		List<String> retn = new ArrayList<String>();

		if (input == null || input.length() == 0)
			return retn;

		String temp = input;

		while (true)
		{
			if (temp.startsWith(":") && retn.size() > 0) 
			{
				retn.add(temp.substring(1));

				return retn;
			}

			String[] split = temp.split(" ", 2);
			retn.add(split[0]);

			if (split.length > 1)
				temp = split[1];
			else
				break;
		}

		return retn;
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
	
	public static LinkedHashMap<String, String> getUserPrefixes(String prefixString)
	{
		LinkedHashMap<String, String> prefixes = new LinkedHashMap<String, String>();
		char[] channelModes = prefixString.substring(1, prefixString.indexOf(")")).toCharArray();
		char[] userLevels = prefixString.substring(prefixString.indexOf(")") + 1).toCharArray();
		
		for(int i = 0; i < channelModes.length; i++)
		{
			prefixes.put("" + channelModes[i], "" + userLevels[i]);
		}
		return prefixes;
	}
	
	public static LinkedHashMap<String, String> getReverseUserPrefixes(String prefixString)
	{
		LinkedHashMap<String, String> prefixes = new LinkedHashMap<String, String>();
		char[] channelModes = prefixString.substring(1, prefixString.indexOf(")")).toCharArray();
		char[] userLevels = prefixString.substring(prefixString.indexOf(")") + 1).toCharArray();
		
		for(int i = 0; i < channelModes.length; i++)
		{
			prefixes.put("" + userLevels[i], "" + channelModes[i]);
		}
		return prefixes;
	}
}