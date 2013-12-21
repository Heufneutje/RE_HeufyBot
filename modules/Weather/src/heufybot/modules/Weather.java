package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.util.HashMap;
import java.util.List;

import org.json.simple.parser.ParseException;

public class Weather extends Module 
{
	private HashMap<String, String> userLocations;
	private final String locationsPath = "data/userlocations.txt";
	
	public Weather()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(weather|forecast|registerloc)($| .*)";
		
		this.userLocations = new HashMap<String, String>();
	}

	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params)
	{
		if(FileUtils.readFile("data/worldweatheronlineapikey.txt").equals(""))
		{
			bot.getIRC().cmdPRIVMSG(source, "No WorldWeatherOnline API key found");
			return;
		}
		
		if(message.matches("^" + commandPrefix + "registerloc.*"))
		{
			if(params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "You didn't give a location to register.");
				return;
			}
			else
			{
				params.remove(0);
				String location = StringUtils.join(params, " ").replaceAll("=", "");
				boolean alreadyRegistered = false;
				
				if(userLocations.containsKey(triggerUser))
				{
					alreadyRegistered = true;
				}
				
				userLocations.put(triggerUser, location);
				writeLocations();
				
				if(alreadyRegistered)
				{
					bot.getIRC().cmdPRIVMSG(source, "Your location has been updated.");
				}
				else
				{
					bot.getIRC().cmdPRIVMSG(source, "Your location is now registered.");
				}
				return;
			}
		}

		if (params.size() == 1)
		{
			if(!userLocations.containsKey(triggerUser))
			{
				bot.getIRC().cmdPRIVMSG(source, "You are not registered. Use \"" + commandPrefix + "registerloc <location>\" to register your location.");
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
				String prefix = location.success ? "Location: " + location.locality : "City: " + latitude + "," + longitude;
				
				if(message.matches("^" + commandPrefix + "weather.*"))
				{
					String weather = getWeatherFromGeolocation(location);
					bot.getIRC().cmdPRIVMSG(source, String.format("%s | %s", prefix, weather));
				}
				else if(message.matches("^" + commandPrefix + "forecast.*"))
				{
					String forecast = getForecastFromGeolocation(location);
					bot.getIRC().cmdPRIVMSG(source, String.format("%s | %s", prefix, forecast));
				}
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
			Geolocation location = geo.getGeolocationForPlace(userLocations.get(params.get(0)));
			if (location != null)
			{
				String loc = location.locality;
				if(loc == null)
				{
					loc = "Unknown";
				}
				
				if(message.matches("^" + commandPrefix + "weather.*"))
				{
					String weather = getWeatherFromGeolocation(location);
					if(weather == null)
					{
						weather = "Weather for this location could not be retrieved.";
					}
					bot.getIRC().cmdPRIVMSG(source, String.format("Location: %s | %s", loc, weather));
				}
				else if(message.matches("^" + commandPrefix + "forecast.*"))
				{
					String forecast = getForecastFromGeolocation(location);
					if(forecast == null)
					{
						bot.getIRC().cmdPRIVMSG(source, String.format("Location: %s | %s", location.locality, "Forecast for this location could not be retrieved."));
						return;
					}
					bot.getIRC().cmdPRIVMSG(source, String.format("Location: %s", loc + " | " + forecast));
				}
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
			
			if(message.matches("^" + commandPrefix + "weather.*"))
			{
				String weather = getWeatherFromGeolocation(location);
				if(weather == null)
				{
					weather = "Weather for this location could not be retrieved.";
				}
				bot.getIRC().cmdPRIVMSG(source, String.format("Location: %s | %s", location.locality, weather));
			}
			else if(message.matches("^" + commandPrefix + "forecast.*"))
			{
				String forecast = getForecastFromGeolocation(location);
				if(forecast == null)
				{
					bot.getIRC().cmdPRIVMSG(source, String.format("Location: %s | %s", location.locality, "Forecast for this location could not be retrieved."));
					return;
				}
				
				bot.getIRC().cmdPRIVMSG(source, String.format("Location: %s", location.locality + " | " + forecast));
			}
		} 
		catch (ParseException e)
		{
			bot.getIRC().cmdPRIVMSG(source, "I don't think that's even a location in this multiverse...");
			return;
		}
	}

	private String getWeatherFromGeolocation(Geolocation location) throws ParseException
	{
		WeatherInterface weatherInterface = new WeatherInterface();
		String weather = weatherInterface.getWeather(location.latitude, location.longitude);
		return weather;
	}
	
	private String getForecastFromGeolocation(Geolocation location) throws ParseException
	{
		WeatherInterface weatherInterface = new WeatherInterface();
		String forecast = weatherInterface.getForecast(location.latitude, location.longitude);
		return forecast;
	}
	
	private void writeLocations()
	{
		String result = "";
		for(String user : userLocations.keySet())
		{
			result += user + "=" + userLocations.get(user);
		}
		FileUtils.writeFile(locationsPath, result);
	}
	
	private void readLocations()
	{
		String[] locationArray = FileUtils.readFile(locationsPath).split("\n");
		if(locationArray[0].length() > 0)
		{
			for(int i = 0; i < locationArray.length; i++)
			{
				String[] location = locationArray[i].split("=");
				userLocations.put(location[0], location[1]);
			}
		}
	}

	@Override
	public String getHelp(String message) 
	{
		if(message.matches("weather"))
		{
			return "Commands: " + commandPrefix + "weather (<place>/<latitude longitude>/<ircuser>) | Makes the bot get the current weather for a location or IRC user. Without a parameter it will look up the weather at your location, as long as it's registered. Type \"" + commandPrefix + "help registerloc\" for more information.";
		}
		else if(message.matches("forecast"))
		{
			return "Commands: " + commandPrefix + "forecast (<place>/<latitude longitude>/<ircuser>) | Makes the bot get the forecast for a location or IRC user. Without a parameter it will look up the forecast at your location, as long as it's registered. Type \"" + commandPrefix + "help registerloc\" for more information.";
		}
		else if(message.matches("registerloc"))
		{
			return "Commands: " + commandPrefix + "registerloc <location> | Registers your current nickname at the given location.";
		}
		return "Commands: " + commandPrefix + "weather (<place>/<latitude longitude>/<ircuser>), " + commandPrefix + "forecast (<place>/<latitude longitude>/<ircuser>), " + commandPrefix + "registerloc <location> | Makes the bot get the current weather conditions at the location specified or at the location of the ircuser.";
	}

	@Override
	public void onLoad() 
	{
		FileUtils.touchFile("data/worldweatheronlineapikey.txt");
		FileUtils.touchFile(locationsPath);
		
		readLocations();
	}

	@Override
	public void onUnload() 
	{
		writeLocations();
	}
}
