package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.URLUtils;

import java.util.List;

import org.json.simple.parser.ParseException;

public class TimeDB extends Module 
{
	public TimeDB()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(time)($| .*)";
	}

	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params)
	{
		if(FileUtils.readFile("data/worldweatheronlineapikey.txt").equals(""))
		{
			bot.getIRC().cmdPRIVMSG(source, "No WorldWeatherOnline API key found");
			return;
		}	
		
		if (params.size() == 1)
		{
			if(URLUtils.grab("http://tsukiakariusagi.net/chatmaplookup.php?nick=" + triggerUser).equals(", "))
			{
				bot.getIRC().cmdPRIVMSG(source, "You are not registered on the chatmap.");
				return;
			}			
			params.add(triggerUser);
		}

		params.remove(0);
		GeocodingInterface geo = new GeocodingInterface();

		// First try latitude and longitude. If these are not in fact lat/lon this will fail before any network stuff is done
		try 
		{
			float latitude = Float.parseFloat(params.get(0));
			float longitude = Float.parseFloat(params.get(1));
			try
			{
				Geolocation location = geo.getGeolocationForLatLng(latitude, longitude);
				String time = getTimeFromGeolocation(location);
				String prefix = location.success ? "Location: " + location.locality : "City: " + latitude + "," + longitude;

				bot.getIRC().cmdPRIVMSG(source, String.format("%s | %s", prefix, time));
				return;
			} 
			catch (ParseException e)
			{
				bot.getIRC().cmdPRIVMSG(source, "I don't think that's even a location in this multiverse...");
				return;
			}
		} 
		catch (NumberFormatException e)
		{
			// Nothing to see here, just not latitude/longitude, continuing.
		}
		catch (IndexOutOfBoundsException e)
		{
			// Either this is fuzzing or invalid input. Either way we don't care, and should check the next two cases.
		}

		try
		{
			Geolocation location = geo.getGeolocationForIRCUser(params.get(0));
			if (location != null)
			{
				String weather = getTimeFromGeolocation(location);

				bot.getIRC().cmdPRIVMSG(source, String.format("Location: %s | %s", location.locality, weather));
				return;
			}
		} 
		catch (ParseException e) 
		{
			bot.getIRC().cmdPRIVMSG(source, "I don't think that's even a user in this multiverse...");
			return;
		}

		try
		{
			Geolocation location = geo.getGeolocationForPlace(message.substring(message.indexOf(' ') + 1));
			if (!location.success)
			{
				bot.getIRC().cmdPRIVMSG(source, "I don't think that's even a location in this multiverse...");
				return;
			}
			String weather = getTimeFromGeolocation(location);
			bot.getIRC().cmdPRIVMSG(source, String.format("Location: %s | %s", location.locality, weather));
			return;
		} 
		catch (ParseException e)
		{
			bot.getIRC().cmdPRIVMSG(source, "I don't think that's even a location in this multiverse...");
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
		return "Commands: " + commandPrefix + "time <place>/<latitude longitude>/<ircuser> | Makes the bot get the current time at the location specified or at the location of the ircuser.";
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
