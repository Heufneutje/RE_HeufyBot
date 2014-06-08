package heufybot.modules;

import java.util.List;

public class Restart extends Module
{
    public Restart(String server)
    {
        super(server);

        this.authType = AuthType.BotAdmins;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(restart)$";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        this.bot.restart();
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: " + this.commandPrefix
                + "restart | Makes the reload its config and modules and reconnect to the server.";
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
