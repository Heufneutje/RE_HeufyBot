package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.util.Iterator;
import java.util.List;

public class Ignore extends Module
{
    private List<String> ignoreList;
    private String ignoreListPath;

    public Ignore(String server)
    {
        super(server);

        this.authType = AuthType.BotAdmins;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(ignore|unignore)($| .*)";

        this.ignoreListPath = "data/" + server + "/ignorelist.txt";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (message.toLowerCase().matches("^" + this.commandPrefix + "ignore.*"))
        {
            if (params.size() == 1)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "Users currently ignored: " + StringUtils.join(this.ignoreList, ", "));
            }
            else
            {
                boolean match = false;
                String nick = params.get(1);

                for (String ignore : this.ignoreList)
                {
                    if (ignore.equalsIgnoreCase(nick))
                    {
                        match = true;
                    }
                }
                if (match)
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                            nick + " is already on the ignore list!");
                }
                else
                {
                    this.ignoreList.add(nick);
                    FileUtils.writeFileAppend(this.ignoreListPath, nick + "\n");
                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                            nick + " was added to the ignore list.");
                }
            }
        }
        else if (message.toLowerCase().matches("^" + this.commandPrefix + "unignore.*"))
        {
            if (params.size() == 1)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "Who do you want me to unignore?");
            }
            else
            {
                String nick = params.get(1);
                boolean match = false;

                for (Iterator<String> iter = this.ignoreList.iterator(); iter.hasNext();)
                {
                    String ignore = iter.next();
                    if (ignore.equalsIgnoreCase(nick))
                    {
                        iter.remove();
                        match = true;
                    }
                }
                if (match)
                {
                    FileUtils.deleteFile(this.ignoreListPath);
                    FileUtils.touchFile(this.ignoreListPath);
                    for (String ignore : this.ignoreList)
                    {
                        FileUtils.writeFileAppend(this.ignoreListPath, ignore + "\n");
                    }
                    // bot.setIgnoreList(ignoreList);
                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                            nick + " was removed from the ignore list.");
                }
                else
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                            nick + " is not on the ignore list!");
                }
            }
        }
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: " + this.commandPrefix + "ignore <nickname>, " + this.commandPrefix
                + "unignore <nickname> | Add or remove a nickname from the ignore list.";
    }

    @Override
    public void onLoad()
    {
        FileUtils.touchFile(this.ignoreListPath);
        this.ignoreList = StringUtils.parseStringtoList(FileUtils.readFile(this.ignoreListPath),
                "\n");
        this.bot.getServer(this.server).getModuleInterface().setIgnores(this.ignoreList);
    }

    @Override
    public void onUnload()
    {
        this.ignoreList.clear();
    }
}
