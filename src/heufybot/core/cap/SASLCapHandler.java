package heufybot.core.cap;

import heufybot.core.IRC;
import heufybot.core.Logger;
import heufybot.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public class SASLCapHandler implements CapHandler
{
	private String username;
	private String password;
	
	public SASLCapHandler(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	@Override
	public boolean handleLS(IRC irc, List<String> capabilities) throws CAPException 
	{
		if(capabilities.contains("sasl"))
		{
			irc.cmdCAP("REQ :", "sasl");
			Logger.log(" -  Capability \"sasl\" is enabled");
		}
		else
		{
			Logger.error("CAP Enabling", "sasl is not supported by the server");
			throw new CAPException(CAPException.Reason.UnsupportedCapability, "sasl");
		}
		return false;
	}

	@Override
	public boolean handleACK(IRC irc, List<String> capabilities) throws CAPException 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleNAK(IRC irc, List<String> capabilities)throws CAPException 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleUnknown(IRC irc, String line) throws CAPException 
	{
		// TODO Auto-generated method stub
		return false;
	}

}
