package heufybot.modules;

import heufybot.utils.URLUtils;

import java.util.List;

public class Shorten extends Module
{
    public Shorten(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(shorten)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (params.size() == 1)
        {
            this.bot.getServer(this.server).cmdPRIVMSG(source, "Shorten what?");
        }
        else
        {
            String shortenedURL = URLUtils.shortenURL(params.get(1));
            if (shortenedURL == null)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "Error: URL could not be shortned");
            }
            else
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source, shortenedURL);
            }
        }
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: " + this.commandPrefix
                + "shorten <url> | Shortens the given URL using Googl.";
    }

    @Override
    public void onLoad()
    {
    }

    @Override
    public void onUnload()
    {
    }
}
