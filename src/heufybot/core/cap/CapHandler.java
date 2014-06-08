package heufybot.core.cap;

import heufybot.core.IRCServer;

import java.util.List;

public interface CapHandler
{
    public boolean handleLS(IRCServer irc, List<String> capabilities) throws CAPException;

    public boolean handleACK(IRCServer irc, List<String> capabilities) throws CAPException;

    public boolean handleNAK(IRCServer irc, List<String> capabilities) throws CAPException;

    public boolean handleUnknown(IRCServer irc, String line) throws CAPException;
}
