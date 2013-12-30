package heufybot.modules;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import heufybot.core.Logger;
import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;
import heufybot.utils.URLUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TimeInterface 
{
	private final static String APIkey = FileUtils.readFile("data/worldweatheronlineapikey.txt").replaceAll("\n", "");
	private final static String APIAddress = "http://api.worldweatheronline.com/free/v1/tz.ashx?";
	
	public String getTime(float latitude, float longitude) throws ParseException
	{
		StringBuilder builder = new StringBuilder();
		builder.append(APIAddress);
		builder.append("q=" + latitude + "," + longitude);
		builder.append("&key=" + APIkey);
		builder.append("&format=json");
		JSONObject object = getJSON(builder.toString());
		
		return parseJSON(object);
	}
	
	private String parseJSON(JSONObject object)
	{
		JSONObject data = (JSONObject)object.get("data");
		JSONObject time_zone = (JSONObject) ((JSONArray)data.get("time_zone")).get(0);

		String localtime = time_zone.get("localtime").toString();
		
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		Date date;
		try 
		{
			date = dt.parse(localtime);
			SimpleDateFormat dt1 = new SimpleDateFormat("HH:mm (hh:mm aa) 'on' EEEEEEEE, dd'" + getSuffix(StringUtils.tryParseInt(new SimpleDateFormat("dd").format(date))) + " of' MMMMMMMMMM, yyyy", Locale.US);
			return "Local time is " + dt1.format(date);
		}
		catch (java.text.ParseException e) 
		{
			Logger.error("Module: Time", "Could not parse the time.");
			return null;
		}
	}
	
	private JSONObject getJSON(String urlString) throws ParseException 
	{
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString));
	}
	
	private String getSuffix(int dayOfMonth)
	{
		String suffix = "";
		switch (dayOfMonth)
		{
		    case 1:
		    case 21:
		    case 31:
		        suffix = "st";
		        break;
		    case 2:
		    case 22:
		        suffix = "nd";
		        break;
		    case 3:
		    case 23:
		        suffix = "rd";
		        break;
		    default:
		        suffix = "th";
		}
		return suffix;
	}
}
