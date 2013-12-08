package heufybot.modules;

import heufybot.utils.FileUtils;
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
	private final static String weatherFormat = "Temp: %s °C/%s °F | Weather: %s | Humidity: %s%c | Wind: %s kmph/%smph %s";
	
	public String getWeather(float latitude, float longitude) throws ParseException
	{
		StringBuilder builder = new StringBuilder();
		builder.append(APIAddress);
		builder.append("q=" + latitude + "," + longitude);
		builder.append("&key=" + APIkey);
		builder.append("&format=json");
		JSONObject object = getJSON(builder.toString());
		
		return parseJSON(object) + " | More info: " + URLUtils.shortenURL(web + latitude + "," + longitude);
	}
	
	private String parseJSON(JSONObject object)
	{
		JSONObject data = (JSONObject)object.get("data");
		JSONObject currentCondition = (JSONObject) ((JSONArray)data.get("current_condition")).get(0);

		String tempC = currentCondition.get("temp_C").toString();
		String tempF = currentCondition.get("temp_F").toString();
		String windspeedMiles = currentCondition.get("windspeedMiles").toString();
		String windspeedKmph = currentCondition.get("windspeedKmph").toString();
		String windDir = currentCondition.get("winddir16Point").toString();
		String desc = ((JSONObject)((JSONArray)currentCondition.get("weatherDesc")).get(0)).get("value").toString();
		String humidity = currentCondition.get("humidity").toString();
		
		return String.format(weatherFormat, tempC, tempF, desc, humidity, '%', windspeedKmph, windspeedMiles, windDir);
	}
	
	private JSONObject getJSON(String urlString) throws ParseException 
	{
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString));
	}
}
