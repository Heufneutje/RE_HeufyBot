package heufybot.core.cap;

import heufybot.core.IRC;
import heufybot.core.Logger;
import heufybot.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public class EnablingCapHandler implements CapHandler
{
	private String cap;
	
	public EnablingCapHandler(String cap)
	{
		this.cap = cap;
	}

	@Override
	public boolean handleLS(IRC irc, List<String> capabilities) 
	{
		if(capabilities.contains(cap))
		{
			irc.cmdCAP("REQ :", StringUtils.join(Arrays.asList(cap), ""));
		}
		else
		{
			Logger.error("CAP Enabling", cap + " is not supported by the server");
		}
		return false;
	}

	@Override
	public boolean handleACK(IRC irc, List<String> capabilities) 
	{
		return capabilities.contains(cap);
	}

	@Override
	public boolean handleNAK(IRC irc, List<String> capabilities) 
	{
		if(capabilities.contains(cap))
		{
			capabilities.remove(cap);
			return true;
		}
		return false;
	}
}
