package heufybot.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import heufybot.utils.URLUtils;

public class URLFollow extends Module
{
	public URLFollow()
	{
		this.authType = AuthType.Anyone;
		this.trigger = ".*https?://.*";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		ArrayList<String> urls = new ArrayList<String>();
		for(int i = 0; i < params.size(); i++)
		{
			if(params.get(i).toLowerCase().contains("http"))
			{
				urls.add(params.get(i).substring(params.get(i).indexOf("http")));
			}
		}
	  
		while(urls.size() > 3)
		{
			urls.remove(urls.size() - 1);
		}
	  
		for(String urlstring : urls)
		{	
			String fullHostname = URLUtils.getFullHostname(urlstring);
			if(!urlstring.toLowerCase().matches(".*(jpe?g|gif|png|bmp)"))
			{
				if(fullHostname != null)
				{
					if(fullHostname.contains("youtube.com/watch"))
					{
						String videoID = fullHostname.split("watch\\?v=")[1];
						if(videoID.contains("&"))
						{
							videoID = videoID.substring(0, videoID.indexOf("&"));
						}
						if(videoID.contains("#"))
						{
							videoID = videoID.substring(0, videoID.indexOf("#"));
						}
						bot.getIRC().cmdPRIVMSG(source, followYouTubeURL(videoID));
					}
					else
					{
						bot.getIRC().cmdPRIVMSG(source, followNormalURL(urlstring));
					}
				}
			}
			else
			{
				//Pattern pattern = Pattern.compile(".*" + searchString + ".*", Pattern.CASE_INSENSITIVE);
			}
		}
	}

	@Override
	public String getHelp(String message)
	{
		return "Commands: None | Looks up and posts the title of a URL when posted.";
	}
	
	private String followNormalURL(String urlString)
	{
		String data = URLUtils.grab(urlString);

		Pattern p = Pattern.compile("<title>(.*?)</title>");
		Matcher m = p.matcher(data);
		if (m.find() == true)
		{
			return "Title: " + m.group(1) + " || At host: " + URLUtils.getHost(urlString);
		}
		return "No title found || At host: " + URLUtils.getHost(urlString);
	}
	
	private String followYouTubeURL(String videoID)
	{
		String urlString = "https://gdata.youtube.com/feeds/api/videos/" + videoID + "?v=2&alt=json";
		try
		{
			JSONObject entry = (JSONObject) getJSON(urlString).get("entry");
			JSONObject mediagroup = (JSONObject) entry.get("media$group");

			JSONObject jTitle = (JSONObject) mediagroup.get("media$title");
			JSONObject jDescription = (JSONObject) mediagroup.get("media$description");
			JSONObject jDuration = (JSONObject) mediagroup.get("yt$duration");
			
			String title = jTitle.get("$t").toString();
		    String description = jDescription.get("$t").toString().replaceAll("\n", " ");
		    String duration = jDuration.get("seconds").toString();
		    
		    if(description.length() > 149)
    		{
    			description = description.substring(0, 147) + "...";
    		}
		    
		    int durationSeconds = Integer.parseInt(duration);
    		if(durationSeconds >= 3600)
    		{
    			duration = ( durationSeconds / 3600 < 10 ? "0": "") + durationSeconds / 3600 +":"+
    					( (durationSeconds % 3600) / 60 < 10 ? "0": "") + (durationSeconds % 3600) / 60 +":"+
    					( (durationSeconds % 3600) % 60 < 10 ? "0": "") + (durationSeconds % 3600) % 60;
    		}
    		else
    		{
    			duration = ( durationSeconds / 60 < 10 ? "0": "") + durationSeconds / 60 +":"+
    					( durationSeconds % 60 < 10 ? "0": "") + durationSeconds % 60;
    		}
    		return "Video Title: " + title + " | " + duration + " | " + description;
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private JSONObject getJSON(String urlString) throws ParseException 
	{
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString));
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
