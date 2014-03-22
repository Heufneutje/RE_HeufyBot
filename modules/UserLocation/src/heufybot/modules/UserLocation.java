package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.util.HashMap;
import java.util.List;

public class UserLocation extends Module
{
	private HashMap<String, String> userLocations;
	private final String locationsPath = "data/userlocations.txt";
	
	public UserLocation()
	{
		this.authType = AuthType.Anyone;
		this.apiVersion = "0.5.0";
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(registerloc)($| .*)";
		
		this.userLocations = new HashMap<String, String>();
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
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
			
			if(userLocations.containsKey(triggerUser.toLowerCase()))
			{
				alreadyRegistered = true;
			}
			
			userLocations.put(triggerUser.toLowerCase(), location);
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
	
	private void writeLocations()
	{
		String result = "";
		for(String user : userLocations.keySet())
		{
			result += user + "=" + userLocations.get(user) + "\n";
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

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "registerloc <location> | Registers your current nickname at the given location.";
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
