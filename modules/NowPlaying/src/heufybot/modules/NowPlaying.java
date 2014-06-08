package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.URLUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class NowPlaying extends Module
{
    private HashMap<String, String> userLinks;
    private String linksPath;

    public NowPlaying(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(np|nowplaying|nplink)($| .*)";

        this.userLinks = new HashMap<String, String>();
        this.linksPath = "data/" + server + "/nplinks.txt";
    }

    @Override
    public String getHelp(String message)
    {
        if (message.matches("np"))
        {
            return this.commandPrefix
                    + "np (<user>) | Returns your currently playing music (from LastFM). You can also supply a specific username to check.";
        }
        else if (message.matches("nplink"))
        {
            return this.commandPrefix
                    + "nplink <LastFM name> - Links the specified LastFM account name to your IRC name.";
        }

        return this.commandPrefix
                + "np (<user>), "
                + this.commandPrefix
                + "nplink <LastFM name> | Returns your or someone else's currently playing music (from LastFM) or link your nickname to a LastFM name.";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (message.toLowerCase().matches("^" + this.commandPrefix + "nplink.*"))
        {
            if (params.size() == 1)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "You must provide a LastFM name to link to your nickname.");
            }
            else
            {
                String link = params.get(1).replaceAll("=", "");
                this.userLinks.put(triggerUser.toLowerCase(), link);
                this.writeLinks();

                this.bot.getServer(this.server).cmdPRIVMSG(
                        source,
                        "The nickname \"" + triggerUser + "\" is now linked to LastFM name \""
                                + link + "\".");
            }
        }
        else if (message.toLowerCase().matches("^" + this.commandPrefix + "(np|nowplaying).*"))
        {
            String name = "";
            if (params.size() == 1)
            {
                name = triggerUser.toLowerCase();
            }
            else
            {
                name = params.get(1).toLowerCase();
            }

            if (this.userLinks.containsKey(name))
            {
                name = this.userLinks.get(name);
            }

            String url = "http://ws.audioscrobbler.com/1.0/user/" + name + "/recenttracks.rss";
            LinkedHashMap<String, String> results = URLUtils.grabRSSFeed(url);

            if (results == null)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "No user with the name \"" + name + "\" could be found on LastFM.");
            }
            else if (results.size() == 0)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(
                        source,
                        "No recently played tracks for user \"" + name
                                + "\" could be found on LastFM.");
            }
            else
            {
                String[] titles = new String[results.keySet().size()];
                titles = results.keySet().toArray(titles);
                String lastEntryTitle = titles[0];

                String lastEntryLink = results.get(lastEntryTitle);
                String[] splittedTitle = lastEntryTitle.split("–");

                String artist = splittedTitle[0].trim();
                String song = splittedTitle[1].trim();
                String link = URLUtils.shortenURL(lastEntryLink);

                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "\"" + song + "\" by " + artist + " | " + link);
            }
        }
    }

    private void writeLinks()
    {
        String result = "";
        for (String user : this.userLinks.keySet())
        {
            result += user + "=" + this.userLinks.get(user) + "\n";
        }
        FileUtils.writeFile(this.linksPath, result);
    }

    private void readLinks()
    {
        String[] locationArray = FileUtils.readFile(this.linksPath).split("\n");
        if (locationArray[0].length() > 0)
        {
            for (String element : locationArray)
            {
                String[] location = element.split("=");
                this.userLinks.put(location[0], location[1]);
            }
        }
    }

    @Override
    public void onLoad()
    {
        FileUtils.touchFile(this.linksPath);
        this.readLinks();
    }

    @Override
    public void onUnload()
    {
        this.writeLinks();
    }
}
