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
	public boolean handleLS(IRC irc, List<String> capabilities) throws CAPException
	{
		if(capabilities.contains(cap))
		{
			irc.cmdCAP("REQ :", StringUtils.join(Arrays.asList(cap), ""));
			irc.getEnabledCapabilities().add(cap);
			Logger.log("*** Capability \"" + cap + "\" is enabled");
		}
		else
		{
			Logger.error("CAP Enabling", cap + " is not supported by the server");
			throw new CAPException(CAPException.Reason.UnsupportedCapability, cap);
		}
		return false;
	}

	@Override
	public boolean handleACK(IRC irc, List<String> capabilities) throws CAPException
	{
		return capabilities.contains(cap);
	}

	@Override
	public boolean handleNAK(IRC irc, List<String> capabilities) throws CAPException
	{
		if(capabilities.contains(cap))
		{
			capabilities.remove(cap);
			return true;
		}
		return false;
	}
}
