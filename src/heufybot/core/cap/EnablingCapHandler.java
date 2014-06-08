package heufybot.core.cap;

import heufybot.core.IRCServer;
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
    public boolean handleLS(IRCServer irc, List<String> capabilities) throws CAPException
    {
        if (capabilities.contains(this.cap))
        {
            irc.cmdCAP("REQ :", StringUtils.join(Arrays.asList(this.cap), ""));
            irc.getEnabledCapabilities().add(this.cap);
            Logger.log(" -  Capability \"" + this.cap + "\" is enabled");
        }
        else
        {
            throw new CAPException(CAPException.Reason.UnsupportedCapability, this.cap);
        }
        return false;
    }

    @Override
    public boolean handleACK(IRCServer irc, List<String> capabilities) throws CAPException
    {
        return capabilities.contains(this.cap);
    }

    @Override
    public boolean handleNAK(IRCServer irc, List<String> capabilities) throws CAPException
    {
        if (capabilities.contains(this.cap))
        {
            irc.getEnabledCapabilities().remove(this.cap);
            throw new CAPException(CAPException.Reason.UnsupportedCapability, this.cap);
        }
        return false;
    }

    @Override
    public boolean handleUnknown(IRCServer irc, String line) throws CAPException
    {
        return false;
    }
}
