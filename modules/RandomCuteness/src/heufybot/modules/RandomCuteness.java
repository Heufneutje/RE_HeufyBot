package heufybot.modules;

import heufybot.utils.URLUtils;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RandomCuteness extends Module
{
	public RandomCuteness()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(aww)($)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		try 
		{
			int pageNumber = (int) (Math.random() * 100 + 1);
			JSONArray dataArray = (JSONArray) (getJSON("https://api.imgur.com/3/gallery/r/aww/time/all/" + pageNumber)).get("data");
			JSONObject object = (JSONObject) dataArray.get((int) (Math.random() * dataArray.size()));
			
			String title = object.get("title").toString();
			String url = object.get("link").toString();
			
			if(url.equals(""))
			{
				bot.getIRC().cmdPRIVMSG(source, "Something went wrong while trying to get an image. Most likely the Imgur API is down.");
				return;
			}
			
			bot.getIRC().cmdPRIVMSG(source, title + " | " + url);
		} 
		catch (ParseException e) 
		{
			bot.getIRC().cmdPRIVMSG(source, "Something went wrong while trying to read the data.");
		}
	}
	
	private JSONObject getJSON(String urlString) throws ParseException 
	{
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString));
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "aww | Returns random cuteness from the /r/aww subreddit.";
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
