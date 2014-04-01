package heufybot.core.cap;

import java.util.List;

import heufybot.core.IRCServer;

public interface CapHandler
{
	public boolean handleLS(IRCServer irc, List<String> capabilities) throws CAPException;
	public boolean handleACK(IRCServer irc, List<String> capabilities) throws CAPException;
	public boolean handleNAK(IRCServer irc, List<String> capabilities) throws CAPException;
	public boolean handleUnknown(IRCServer irc, String line) throws CAPException;
}
