package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class Say extends Module
{
    public Say(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(say)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (params.size() == 1)
        {
            this.bot.getServer(this.server).cmdPRIVMSG(source, "Say what?");
        }
        else
        {
            params.remove(0);
            this.bot.getServer(this.server).cmdPRIVMSG(source, StringUtils.join(params, " "));
        }
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: " + this.commandPrefix
                + "say <message> | Makes the bot say the given line.";
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
