package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class Part extends Module
{
    public Part(String server)
    {
        super(server);

        this.authType = AuthType.BotAdmins;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(part)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (params.size() == 1)
        {
            this.bot.getServer(this.server).cmdPART(source, "");
        }
        else
        {
            params.remove(0);
            if (params.size() > 1)
            {
                String channel = params.remove(0);
                this.bot.getServer(this.server).cmdPART(channel, StringUtils.join(params, " "));
            }
            else
            {
                this.bot.getServer(this.server).cmdPART(params.get(0), "");
            }
        }
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: "
                + this.commandPrefix
                + "part (<channel> <message>) | Makes the bot part the current channel or a given one.";
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
