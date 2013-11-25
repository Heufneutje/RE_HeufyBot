package heufybot.utils;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils
{
	public static List<String> tokenizeLine(String input) {
		List<String> retn = new ArrayList<String>();

		if (input == null || input.length() == 0)
			return retn;

		String temp = input;

		while (true) {
			if (temp.startsWith(":") && retn.size() > 0) {
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
}