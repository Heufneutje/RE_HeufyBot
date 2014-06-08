package heufybot.core;

public class IRCUser
{
    private String nickname;
    private String login = "";
    private String hostname = "";
    private String realname = "";
    private String server = "";
    private int hops;
    private boolean isOper;
    private boolean isAway;

    public IRCUser(String nickname, String login, String hostname)
    {
        this.nickname = nickname;
        this.login = login;
        this.hostname = hostname;
    }

    public IRCUser(String nickname)
    {
        this.nickname = nickname;
    }

    public String getNickname()
    {
        return this.nickname;
    }

    public String getLogin()
    {
        return this.login;
    }

    public String getHostname()
    {
        return this.hostname;
    }

    public String getFullHost()
    {
        return this.nickname + "!" + this.login + "@" + this.hostname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public String getRealname()
    {
        return this.realname;
    }

    public void setRealname(String realname)
    {
        this.realname = realname;
    }

    public boolean isOper()
    {
        return this.isOper;
    }

    public boolean isAway()
    {
        return this.isAway;
    }

    public void setOper(boolean isOper)
    {
        this.isOper = isOper;
    }

    public void setAway(boolean isAway)
    {
        this.isAway = isAway;
    }

    public int getHops()
    {
        return this.hops;
    }

    public void setHops(int hops)
    {
        this.hops = hops;
    }

    public String getServer()
    {
        return this.server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }
}
