package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;

public class LogSearch extends Module
{
    public LogSearch(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix
                + "(firstseen|lastseen|lastsaw|firstsaid|lastsaid)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (params.size() == 1)
        {
            this.bot.getServer(this.server)
                    .cmdPRIVMSG(source, "What do you want me to search for?");
        }
        else
        {
            params.remove(0);
            String searchTerms = StringUtils.join(params, " ");

            String rootLogPath = this.bot.getServer(this.server).getConfig()
                    .getSettingWithDefault("logPath", "logs");
            String network = this.bot.getServer(this.server).getServerInfo().getNetwork();
            Searcher searcher = new Searcher(rootLogPath + "/" + network + "/" + source + "/");

            if (message.matches(this.commandPrefix + "firstseen.*"))
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        searcher.firstSeen(source, searchTerms));
            }
            else if (message.matches(this.commandPrefix + "lastseen.*"))
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        searcher.lastSeen(source, searchTerms, true));
            }
            else if (message.matches(this.commandPrefix + "lastsaw.*"))
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        searcher.lastSeen(source, searchTerms, false));
            }
            else if (message.matches(this.commandPrefix + "firstsaid.*"))
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        searcher.firstSaid(source, searchTerms));
            }
            else if (message.matches(this.commandPrefix + "lastsaid.*"))
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        searcher.lastSaid(source, searchTerms));
            }
        }
    }

    @Override
    public String getHelp(String message)
    {
        if (message.matches("firstseen"))
        {
            return "Commands: " + this.commandPrefix
                    + "firstseen <nickname> | Finds the first message of the given user.";
        }
        else if (message.matches("lastseen"))
        {
            return "Commands: "
                    + this.commandPrefix
                    + "lastseen <nickname> | Finds the last message of the given user. This includes today.";
        }
        else if (message.matches("lastsaw"))
        {
            return "Commands: "
                    + this.commandPrefix
                    + "lastsaw <nickname> | Finds the last message of the given user. This does not include today.";
        }
        else if (message.matches("firstsaid"))
        {
            return "Commands: "
                    + this.commandPrefix
                    + "firstsaid <text> | Checks the log for the first time someone mentioned a given word or phrase.";
        }
        else if (message.matches("lastsaid"))
        {
            return "Commands: "
                    + this.commandPrefix
                    + "lastsaid <text> | Checks the log for the last time someone mentioned a given word or phrase.";
        }
        else
        {
            return "Commands: " + this.commandPrefix + "firstseen <nickname>, "
                    + this.commandPrefix + "lastseen <nickname>, " + this.commandPrefix
                    + "lastsaw <nickname>, " + this.commandPrefix + "firstsaid <text>, "
                    + this.commandPrefix
                    + "lastsaid <text> | Searches the logs for a certain name or phrase.";
        }
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
