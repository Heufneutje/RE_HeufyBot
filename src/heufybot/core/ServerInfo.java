package heufybot.core;

import heufybot.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;

public class ServerInfo
{
    private String server, serverVersion, motd, network, chantypes;
    private LinkedHashMap<String, String> userPrefixes;
    private LinkedHashMap<String, String> reverseUserPrefixes;
    private List<String> channelListModes, channelSetArgsModes, channelSetUnsetArgsModes,
            channelNoArgsModes, userModes;

    public ServerInfo()
    {
        this.clear();
    }

    public void clear()
    {
        this.userPrefixes = new LinkedHashMap<String, String>();
        this.reverseUserPrefixes = new LinkedHashMap<String, String>();

        // Initialize user prefixes with default values (@ for op (o) and + for
        // voice (v)), as documented in RFC1459
        this.userPrefixes.put("o", "@");
        this.userPrefixes.put("v", "+");

        this.reverseUserPrefixes.put("@", "o");
        this.reverseUserPrefixes.put("+", "v");

        // Initialize channel modes with the default set documented in RFC1459
        this.channelListModes = StringUtils.parseStringtoList("b", ",");
        this.channelSetArgsModes = StringUtils.parseStringtoList("l", ",");
        this.channelSetUnsetArgsModes = StringUtils.parseStringtoList("k", ",");
        this.channelNoArgsModes = StringUtils.parseStringtoList("p,s,i,t,n,m", ",");

        // Initialize user modes with the default set documented in RFC1459
        this.userModes = StringUtils.parseStringtoList("i,s,w,o", ",");

        this.chantypes = "#";
        this.network = "Unknown Network";
    }

    public String getMotd()
    {
        return this.motd;
    }

    public void setMotd(String motd)
    {
        this.motd = motd;
    }

    public void appendMotd(String motd)
    {
        this.motd += motd;
    }

    public LinkedHashMap<String, String> getUserPrefixes()
    {
        return this.userPrefixes;
    }

    public void setUserPrefixes(LinkedHashMap<String, String> userPrefixes)
    {
        this.userPrefixes = userPrefixes;
    }

    public LinkedHashMap<String, String> getReverseUserPrefixes()
    {
        return this.reverseUserPrefixes;
    }

    public void setReverseUserPrefixes(LinkedHashMap<String, String> reverseUserPrefixes)
    {
        this.reverseUserPrefixes = reverseUserPrefixes;
    }

    public String getNetwork()
    {
        return this.network;
    }

    public void setNetwork(String network)
    {
        this.network = network;
    }

    public List<String> getChannelListModes()
    {
        return this.channelListModes;
    }

    public List<String> getChannelSetArgsModes()
    {
        return this.channelSetArgsModes;
    }

    public List<String> getChannelSetUnsetArgsModes()
    {
        return this.channelSetUnsetArgsModes;
    }

    public List<String> getChannelNoArgsModes()
    {
        return this.channelNoArgsModes;
    }

    public List<String> getUserModes()
    {
        return this.userModes;
    }

    public String getChantypes()
    {
        return this.chantypes;
    }

    public void setChantypes(String chantypes)
    {
        this.chantypes = chantypes;
    }

    public String getServer()
    {
        return this.server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getServerVersion()
    {
        return this.serverVersion;
    }

    public void setServerVersion(String serverVersion)
    {
        this.serverVersion = serverVersion;
    }
}
