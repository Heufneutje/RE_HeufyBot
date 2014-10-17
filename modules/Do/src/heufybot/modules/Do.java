package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class Do extends Module
{
    public Do(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(do)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (params.size() == 1)
        {
            this.bot.getServer(this.server).cmdPRIVMSG(source, "Do what?");
        }
        else
        {
            params.remove(0);
            this.bot.getServer(this.server).cmdACTION(source, StringUtils.join(params, " "));
        }
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: " + this.commandPrefix
                + "do <message> | Makes the bot perform the given line in an action.";
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
