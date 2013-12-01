package heufybot.core.cap;

import java.util.List;

import heufybot.core.IRC;

public interface CapHandler
{
	public boolean handleLS(IRC irc, List<String> capabilities) throws CAPException;
	public boolean handleACK(IRC irc, List<String> capabilities) throws CAPException;
	public boolean handleNAK(IRC irc, List<String> capabilities) throws CAPException;
}
