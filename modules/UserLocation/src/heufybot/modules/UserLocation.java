package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.util.HashMap;
import java.util.List;

public class UserLocation extends Module
{
    private HashMap<String, String> userLocations;
    private String locationsPath;

    public UserLocation(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(registerloc)($| .*)";

        this.userLocations = new HashMap<String, String>();
        this.locationsPath = "data/" + server + "/userlocations.txt";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (params.size() == 1)
        {
            this.bot.getServer(this.server).cmdPRIVMSG(source,
                    "You didn't give a location to register.");
            return;
        }
        else
        {
            params.remove(0);
            String location = StringUtils.join(params, " ").replaceAll("=", "");
            boolean alreadyRegistered = false;

            if (this.userLocations.containsKey(triggerUser.toLowerCase()))
            {
                alreadyRegistered = true;
            }

            this.userLocations.put(triggerUser.toLowerCase(), location);
            this.writeLocations();

            if (alreadyRegistered)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "Your location has been updated.");
            }
            else
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "Your location is now registered.");
            }
            return;
        }
    }

    private void writeLocations()
    {
        String result = "";
        for (String user : this.userLocations.keySet())
        {
            result += user + "=" + this.userLocations.get(user) + "\n";
        }
        FileUtils.writeFile(this.locationsPath, result);
    }

    private void readLocations()
    {
        String[] locationArray = FileUtils.readFile(this.locationsPath).split("\n");
        if (locationArray[0].length() > 0)
        {
            for (String element : locationArray)
            {
                String[] location = element.split("=");
                this.userLocations.put(location[0], location[1]);
            }
        }
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: " + this.commandPrefix
                + "registerloc <location> | Registers your current nickname at the given location.";
    }

    @Override
    public void onLoad()
    {
        FileUtils.touchFile("data/worldweatheronlineapikey.txt");
        FileUtils.touchFile(this.locationsPath);

        this.readLocations();
    }

    @Override
    public void onUnload()
    {
        this.writeLocations();
    }
}
