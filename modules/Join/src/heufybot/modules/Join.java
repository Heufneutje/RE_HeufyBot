package heufybot.modules;

import java.util.List;

public class Join extends Module
{
    public Join(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(join)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (params.size() == 1)
        {
            this.bot.getServer(this.server).cmdPRIVMSG(source, "Join what?");
        }
        else
        {
            if (params.size() > 2)
            {
                this.bot.getServer(this.server).cmdJOIN(params.get(1), params.get(2));
            }
            else
            {
                this.bot.getServer(this.server).cmdJOIN(params.get(1), "");
            }
        }
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: " + this.commandPrefix
                + "join <channel> (<password>) | Makes the bot join a channel.";
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
