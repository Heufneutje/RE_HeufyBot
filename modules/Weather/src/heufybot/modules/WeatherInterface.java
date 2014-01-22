package heufybot.modules;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;
import heufybot.utils.URLUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WeatherInterface 
{
	private final static String APIkey = FileUtils.readFile("data/worldweatheronlineapikey.txt").replaceAll("\n", "");
	private final static String APIAddress = "http://api.worldweatheronline.com/free/v1/weather.ashx?";
	private final static String web = "http://www.worldweatheronline.com/v2/weather.aspx?q=";
	
	public String getWeather(float latitude, float longitude) throws ParseException
	{
		StringBuilder builder = new StringBuilder();
		builder.append(APIAddress);
		builder.append("q=" + latitude + "," + longitude);
		builder.append("&key=" + APIkey);
		builder.append("&format=json");
		JSONObject object = getJSON(builder.toString());
		
		String parsedJSON = parseJSONForWeather(object);
		if(parsedJSON == null)
		{
			return null;
		}
		return parsedJSON + " | More info: " + URLUtils.shortenURL(web + latitude + "," + longitude);
	}
	
	public String getForecast(float latitude, float longitude) throws ParseException
	{
		StringBuilder builder = new StringBuilder();
		builder.append(APIAddress);
		builder.append("q=" + latitude + "," + longitude);
		builder.append("&key=" + APIkey);
		builder.append("&num_of_days=4");
		builder.append("&format=json");
		JSONObject object = getJSON(builder.toString());
		System.out.println(builder.toString());
		
		String parsedJSON = parseJSONForForecast(object);
		if(parsedJSON == null)
		{
			return null;
		}
		return parsedJSON;
	}
	
	private String parseJSONForWeather(JSONObject object)
	{
		JSONObject data = (JSONObject)object.get("data");
		
		if((JSONArray)data.get("current_condition") == null)
		{
			return null;
		}
		
		JSONObject currentCondition = (JSONObject) ((JSONArray)data.get("current_condition")).get(0);

		String tempC = currentCondition.get("temp_C").toString();
		String tempF = currentCondition.get("temp_F").toString();
		String windspeedMiles = currentCondition.get("windspeedMiles").toString();
		String windspeedKmph = currentCondition.get("windspeedKmph").toString();
		String windDir = currentCondition.get("winddir16Point").toString();
		String desc = ((JSONObject)((JSONArray)currentCondition.get("weatherDesc")).get(0)).get("value").toString();
		String humidity = currentCondition.get("humidity").toString();
		
		return String.format("Temp: %s°C / %s°F | Weather: %s | Humidity: %s%c | Wind Speed: %s kmph / %s mph | Wind Direction: %s", tempC, tempF, desc, humidity, '%', windspeedKmph, windspeedMiles, windDir);
	}
	
	private String parseJSONForForecast(JSONObject object)
	{
		JSONObject data = (JSONObject)object.get("data");
		if((JSONArray)data.get("current_condition") == null)
		{
			return null;
		}
		
		JSONArray weather = (JSONArray)data.get("weather");
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat format2 = new SimpleDateFormat("EEEEEEEE", Locale.US);
		
		List<String> days = new ArrayList<String>();
		
		for(int i = 0; i < weather.size(); i++)
		{
			JSONObject day = (JSONObject) weather.get(i);
			Date date;
			try 
			{
				date = format1.parse(day.get("date").toString());
			} 
			catch (java.text.ParseException e)
			{
				date = new Date();
			}
			String dayOfWeek = format2.format(date);
			String minC = day.get("tempMinC").toString();
			String maxC = day.get("tempMaxC").toString();
			String minF = day.get("tempMinF").toString();
			String maxF = day.get("tempMaxF").toString();
			String weatherDescription = ((JSONObject)((JSONArray)day.get("weatherDesc")).get(0)).get("value").toString();
			
			days.add(String.format("%s: %s - %s°C, %s - %s°F, %s", dayOfWeek, minC, maxC, minF, maxF, weatherDescription));
		}
		return StringUtils.join(days, " | ");
	}
	
	private JSONObject getJSON(String urlString) throws ParseException 
	{
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString));
	}
}
