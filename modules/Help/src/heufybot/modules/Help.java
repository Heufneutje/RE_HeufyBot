package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Help extends Module
{
    public Help(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(help)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (params.size() == 1)
        {
            ArrayList<String> moduleNames = new ArrayList<String>();
            for (Module module : this.bot.getServer(this.server).getModuleInterface()
                    .getModuleList())
            {
                moduleNames.add(module.toString());
            }

            Collections.sort(moduleNames);
            String response = "Modules loaded: " + StringUtils.join(moduleNames, ", ");
            this.bot.getServer(this.server).cmdPRIVMSG(source, response);
        }
        else
        {
            params.remove(0);
            String helpParams = StringUtils.join(params, " ");
            String help = this.bot.getServer(this.server).getModuleInterface()
                    .getModuleHelp(helpParams);
            if (help != null)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source, help);
            }
            else
            {
                this.bot.getServer(this.server).cmdPRIVMSG(
                        source,
                        "Module or command matching \"" + helpParams
                                + "\" is not loaded or does not exist!");
            }
        }
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: "
                + this.commandPrefix
                + "help (<module>) | Shows all modules that are currently loaded or shows help for a given module. Command syntax will be as such: command <parameter>. Parameters in brackets are optional.";
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
