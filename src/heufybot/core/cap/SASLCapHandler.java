package heufybot.core.cap;

import heufybot.core.IRCServer;
import heufybot.core.Logger;
import heufybot.utils.Base64;

import java.io.UnsupportedEncodingException;
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
    public boolean handleLS(IRCServer irc, List<String> capabilities) throws CAPException
    {
        if (capabilities.contains("sasl"))
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
    public boolean handleACK(IRCServer irc, List<String> capabilities) throws CAPException
    {
        if (capabilities.contains("sasl"))
        {
            irc.sendRawNow("AUTHENTICATE PLAIN");
        }
        return false;
    }

    @Override
    public boolean handleNAK(IRCServer irc, List<String> capabilities) throws CAPException
    {
        if (capabilities.contains("sasl"))
        {
            irc.getConfig().getCapHandlers().remove("sasl");
            throw new CAPException(CAPException.Reason.UnsupportedCapability, "sasl");
        }
        return false;
    }

    @Override
    public boolean handleUnknown(IRCServer irc, String line) throws CAPException
    {
        if (line.equals("AUTHENTICATE +"))
        {
            try
            {
                String encodedAuth = Base64.encodeToString((this.username + '\u0000'
                        + this.username + '\u0000' + this.password).getBytes("UTF-8"), false);
                irc.sendRawNow("AUTHENTICATE " + encodedAuth);
                Logger.log("*** Attempting SASL authentication...");
            }
            catch (UnsupportedEncodingException e)
            {
                Logger.error("SASL Authentication", "SASL authentication failed");
                return true;
            }
        }

        String[] parsedLine = line.split(" ", 4);
        if (parsedLine.length > 0)
        {
            String code = parsedLine[1];
            if (code.equals("904") || code.equals("905"))
            {
                irc.getEnabledCapabilities().remove("sasl");
                Logger.error("SASL Authentication", "SASL authentication failed with message: "
                        + parsedLine[3].substring(1));
                return true;
            }
            else if (code.equals("900"))
            {
                Logger.log(parsedLine[3]);
            }
            else if (code.equals("903"))
            {
                Logger.log("*** " + parsedLine[3].substring(1));
                return true;
            }
        }
        return false;
    }

}
