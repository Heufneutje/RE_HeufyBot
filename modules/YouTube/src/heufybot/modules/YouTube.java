package heufybot.modules;

import heufybot.core.Logger;
import heufybot.utils.StringUtils;
import heufybot.utils.URLUtils;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class YouTube extends Module
{
	public YouTube()
	{
		this.authType = AuthType.Anyone;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(youtube)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getIRC().cmdPRIVMSG(source, "What video do you want me to look up?");
		}
		else
		{
			params.remove(0);
			String searchTerms = StringUtils.join(params, "%20");
			
			String urlString = "https://gdata.youtube.com/feeds/api/videos?q=" + searchTerms +
                    "&orderby=relevance" +
                    "&max-results=1" +
                    "&v=2" +
                    "&alt=json";
			try 
			{
				JSONObject data = (JSONObject) getJSON(urlString).get("feed");
				
				JSONObject jTotalResults = (JSONObject) data.get("openSearch$totalResults");
				int totalResults = StringUtils.tryParseInt(jTotalResults.get("$t").toString());
				
				if(totalResults == 0)
				{
					bot.getIRC().cmdPRIVMSG(source, "No results found for \"" + searchTerms + "\".");
					return;
				}
				
				JSONObject entry = (JSONObject) ((JSONArray)data.get("entry")).get(0);
				JSONObject mediagroup = (JSONObject) entry.get("media$group");

				JSONObject jTitle = (JSONObject) mediagroup.get("media$title");
				JSONObject jDescription = (JSONObject) mediagroup.get("media$description");
				JSONObject jDuration = (JSONObject) mediagroup.get("yt$duration");
				JSONObject jVideoID = (JSONObject) mediagroup.get("yt$videoid");
				
				String title = jTitle.get("$t").toString();
			    String description = jDescription.get("$t").toString().replaceAll("\n", " ");
			    String duration = jDuration.get("seconds").toString();
			    String videoID = jVideoID.get("$t").toString();
			    
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
				
	    		bot.getIRC().cmdPRIVMSG(source, "Results: " + totalResults + " || Top Result: http://www.youtube.com/watch?v=" + videoID + " | Video Title: " + title + " | " + duration + " | " + description);
			} 
			catch (ParseException e)
			{
				Logger.error("Module: YouTube", "Could not parse video data.");
			}
		}
	}
	
	private JSONObject getJSON(String urlString) throws ParseException 
	{
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString));
	}

	@Override
	public String getHelp(String message) 
	{
		return "Commands: " + commandPrefix + "youtube <terms> | Returns the first search result on YouTube for the given search term.";
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
