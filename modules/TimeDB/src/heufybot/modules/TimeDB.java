package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.URLUtils;
import org.json.simple.parser.ParseException;

import java.util.List;

public class TimeDB extends Module
{
    public TimeDB(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(time)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (FileUtils.readFile("data/worldweatheronlineapikey.txt").equals(""))
        {
            this.bot.getServer(this.server).cmdPRIVMSG(source,
                    "No WorldWeatherOnline API key found");
            return;
        }

        if (params.size() == 1)
        {
            String chatmapResult = URLUtils
                    .grab("http://tsukiakariusagi.net/chatmaplookup.php?nick=" + triggerUser);

            if (chatmapResult == null)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "Chatmap seems to be down right now. Try again later.");
                return;
            }
            else if (chatmapResult.equals(", "))
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "You are not registered on the chatmap.");
                return;
            }
            params.add(triggerUser);
        }

        params.remove(0);
        GeocodingInterface geo = new GeocodingInterface();

        // First try latitude and longitude. If these are not in fact lat/lon
        // this will fail before any network stuff is done
        try
        {
            float latitude = Float.parseFloat(params.get(0));
            float longitude = Float.parseFloat(params.get(1));
            try
            {
                Geolocation location = geo.getGeolocationForLatLng(latitude, longitude);
                String time = this.getTimeFromGeolocation(location);
                String prefix = location.success ? "Location: " + location.locality : "City: "
                        + latitude + "," + longitude;

                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        String.format("%s | %s", prefix, time));
                return;
            }
            catch (ParseException e)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "I don't think that's even a location in this multiverse...");
                return;
            }
        }
        catch (NumberFormatException e)
        {
            // Nothing to see here, just not latitude/longitude, continuing.
        }
        catch (IndexOutOfBoundsException e)
        {
            // Either this is fuzzing or invalid input. Either way we don't
            // care, and should check the next two cases.
        }

        try
        {
            Geolocation location = geo.getGeolocationForIRCUser(params.get(0));
            if (location != null)
            {
                String weather = this.getTimeFromGeolocation(location);

                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        String.format("Location: %s | %s", location.locality, weather));
                return;
            }
        }
        catch (ParseException e)
        {
            this.bot.getServer(this.server).cmdPRIVMSG(source,
                    "I don't think that's even a user in this multiverse...");
            return;
        }

        try
        {
            Geolocation location = geo.getGeolocationForPlace(message.substring(message
                    .indexOf(' ') + 1));
            if (!location.success)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "I don't think that's even a location in this multiverse...");
                return;
            }
            String weather = this.getTimeFromGeolocation(location);
            this.bot.getServer(this.server).cmdPRIVMSG(source,
                    String.format("Location: %s | %s", location.locality, weather));
            return;
        }
        catch (ParseException e)
        {
            this.bot.getServer(this.server).cmdPRIVMSG(source,
                    "I don't think that's even a location in this multiverse...");
            return;
        }
    }

    private String getTimeFromGeolocation(Geolocation location) throws ParseException
    {
        TimeInterface weatherInterface = new TimeInterface();
        String weather = weatherInterface.getTime(location.latitude, location.longitude);
        return weather;
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: "
                + this.commandPrefix
                + "time (<place>/<latitude longitude>/<ircuser>) | Makes the bot get the current time at the location" +
                " specified or at the location of the ircuser.";
    }

    @Override
    public void onLoad()
    {
        FileUtils.touchFile("data/worldweatheronlineapikey.txt");
    }

    @Override
    public void onUnload()
    {
    }
}
