package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class Quit extends Module
{
    public Quit(String server)
    {
        super(server);

        this.authType = AuthType.BotAdmins;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(quit)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (params.size() == 1)
        {
            this.bot.stop("Quit command issued by " + triggerUser);
        }
        else
        {
            params.remove(0);
            this.bot.stop(StringUtils.join(params, " "));
        }
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: " + this.commandPrefix
                + "quit (<message>) | Makes the bot quit the server.";
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
