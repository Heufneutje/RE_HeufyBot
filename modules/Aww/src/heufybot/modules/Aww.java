package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.URLUtils;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Aww extends Module
{
	private final String clientIDPath = "data/imgurclientid.txt";
	
	public Aww()
	{
		this.authType = AuthType.Anyone;
		this.apiVersion = "0.5.0";
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(aww)($)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		try 
		{
			if(FileUtils.readFile(clientIDPath).equals(""))
			{
				bot.getIRC().cmdPRIVMSG(source, "No Imgur client ID found.");
				return;
			}
			
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
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Client-ID " + FileUtils.readFile(clientIDPath).replaceAll("\n", ""));
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString, headers));
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "aww | Returns random cuteness from the /r/aww subreddit.";
	}

	@Override
	public void onLoad() 
	{
		FileUtils.touchFile(clientIDPath);
	}

	@Override
	public void onUnload()
	{
	}
}
