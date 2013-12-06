package heufybot.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WeatherInterface {
	private final static String APIkey = "rgeqdqv8vze8ytefbw3sa62c";
	private final static String APIAddress = "http://api.worldweatheronline.com/free/v1/weather.ashx?";
	private final static String web = "http://www.worldweatheronline.com/v2/weather.aspx?q=";
	private final static String weatherFormat = "Temp: %s\u00B0C/%s\u00B0F | Weather: %s | Humidity: %s%c | Wind: %s kmph/%smph %s | Local Observation Time: %s";
	
	public String getWeather(float latitude, float longitude) throws IOException, ParseException{
		StringBuilder builder = new StringBuilder();
		builder.append(APIAddress);
		builder.append("q=" + latitude + "," + longitude);
		builder.append("&key=" + APIkey);
		builder.append("&format=json");
		builder.append("&extra=localObsTime");
		URL url = new URL(builder.toString());
		JSONObject object = getJSON(url);
		
		return parseJSON(object);// + " | More info: " + URLShortener.getShortenedURL(web + latitude + "," + longitude);
	}
	
	private String parseJSON(JSONObject object){
		JSONObject data = (JSONObject)object.get("data");
		JSONObject currentCondition = (JSONObject) ((JSONArray)data.get("current_condition")).get(0);

		String curTime = currentCondition.get("localObsDateTime").toString();
		String tempC = currentCondition.get("temp_C").toString();
		String tempF = currentCondition.get("temp_F").toString();
		String windspeedMiles = currentCondition.get("windspeedMiles").toString();
		String windspeedKmph = currentCondition.get("windspeedKmph").toString();
		String windDir = currentCondition.get("winddir16Point").toString();
		String desc = ((JSONObject)((JSONArray)currentCondition.get("weatherDesc")).get(0)).get("value").toString();
		String humidity = currentCondition.get("humidity").toString();
		
		return String.format(weatherFormat, tempC, tempF, desc, humidity, '%', windspeedKmph, windspeedMiles, windDir, curTime);
	}
	
	private JSONObject getJSON(URL url) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		StringBuilder builder = new StringBuilder();
		String nextLine;
		while ((nextLine = reader.readLine()) != null)
			builder.append(nextLine + "\n");
		return (JSONObject)new JSONParser().parse(builder.toString());
	}
}
