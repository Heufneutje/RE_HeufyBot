package heufybot.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;
import heufybot.utils.URLUtils;

public class URLFollow extends Module
{
	private final String imgurClientIDPath = "data/imgurclientid.txt";
	
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
			if(urlstring.toLowerCase().contains("imgur.com"))
			{
				if(FileUtils.readFile(imgurClientIDPath).equals(""))
				{
					bot.getIRC().cmdPRIVMSG(source, followNormalURL(urlstring));
					return;
				}
				else
				{
					Pattern p = Pattern.compile("imgur\\.com/([a-zA-Z0-9/]*)");
					Matcher m = p.matcher(urlstring);
					if(m.find())
					{
						bot.getIRC().cmdPRIVMSG(source, followImgur(m.group(1)));
					}
				}
			}
			else if(!urlstring.toLowerCase().matches(".*(jpe?g|gif|png|bmp)"))
			{
				if(fullHostname != null)
				{
					Pattern p = Pattern.compile("youtube\\.com/watch\\?v=([a-zA-Z0-9_]*)");
					Matcher m = p.matcher(fullHostname);
					
					if(m.find())
					{
						bot.getIRC().cmdPRIVMSG(source, followYouTubeURL(m.group(1)));
						return;
					}
					bot.getIRC().cmdPRIVMSG(source, followNormalURL(urlstring));
				}
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
		if (m.find())
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
			return "Something went wrong while parsing the data";
		}
	}
	
	private String followImgur(String imgurID)
	{
		String url = "";
		boolean isAlbum = false;
		
		if(imgurID.startsWith("gallery/"))
		{
			imgurID = imgurID.replaceAll("gallery/", "");
			url = "https://api.imgur.com/3/gallery/" + imgurID;
		}
		else if(imgurID.startsWith("a/"))
		{
			imgurID = imgurID.replaceAll("a/", "");
			isAlbum = true;
			url = "https://api.imgur.com/3/album/" + imgurID;
		}
		else
		{
			url = "https://api.imgur.com/3/image/" + imgurID;
		}
		
		try 
		{
			JSONObject data = (JSONObject) getImgurJSON(url).get("data");
			List<String> imageData = new ArrayList<String>();
			
			if(data.get("title") == null)
			{
				imageData.add("No Title");
			}
			else
			{
				imageData.add("Title: " + data.get("title").toString());
			}
			
			if(data.get("nsfw") != null)
			{
				imageData.add("NSFW!");
			}
			
			if(isAlbum)
			{
				imageData.add("Album, " + data.get("images_count").toString() + " images");
			}
			else
			{
				if(data.containsKey("is_album") && data.get("is_album") != null)
				{
					JSONArray images = (JSONArray) data.get("images");
					imageData.add("Album, " + images.size() + " images");
				}
				else
				{
					if(data.get("animated").toString().equals("true"))
					{
						imageData.add("Animated");
					}
					imageData.add("Dimensions: " + data.get("width").toString() + "x" + data.get("height").toString());
					imageData.add("File size: " + StringUtils.tryParseInt(data.get("size").toString()) / 1024 + " kB");
				}
			}
			imageData.add("Views: " + data.get("views").toString());
			
			return StringUtils.join(imageData, " | ");
		}
		catch (ParseException e) 
		{
			return "Something went wrong while parsing the data";
		}
	}
	
	private JSONObject getJSON(String urlString) throws ParseException 
	{
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString));
	}
	
	private JSONObject getImgurJSON(String urlString) throws ParseException
	{
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Client-ID " + FileUtils.readFile(imgurClientIDPath).replaceAll("\n", ""));
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString, headers));
	}
	
	@Override
	public void onLoad()
	{
		FileUtils.touchFile(imgurClientIDPath);
	}

	@Override
	public void onUnload()
	{
	}
}
