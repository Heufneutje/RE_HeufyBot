package heufybot.modules;

import heufybot.utils.StringUtils;
import heufybot.utils.URLUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.commons.lang3.text.WordUtils;

public class WeatherInterface
{
    private final static String WEATHER_ADDRESS = "http://api.openweathermap.org/data/2.5/weather?";
    private final static String FORECAST_ADDRESS = "http://api.openweathermap.org/data/2.5/forecast/daily?";

    public String getWeather(float latitude, float longitude) throws ParseException
    {
        StringBuilder builder = new StringBuilder();
        builder.append(WEATHER_ADDRESS);
        builder.append("lat=" + latitude);
        builder.append("&lon=" + longitude);
        
        JSONObject object = this.getJSON(builder.toString());

        String parsedJSON = this.parseJSONForWeather(object);
        if (parsedJSON == null)
        {
            return null;
        }
        return parsedJSON;
    }

    public String getForecast(float latitude, float longitude) throws ParseException
    {
        StringBuilder builder = new StringBuilder();
        builder.append(FORECAST_ADDRESS);
        builder.append("lat=" + latitude);
        builder.append("&lon=" + longitude);
        builder.append("&cnt=4");
        JSONObject object = this.getJSON(builder.toString());

        String parsedJSON = this.parseJSONForForecast(object);
        if (parsedJSON == null)
        {
            return null;
        }
        return parsedJSON;
    }

    private String parseJSONForWeather(JSONObject object)
    {
        if (!object.get("cod").toString().equals("200"))
        {
            return null;
        }
        
        JSONObject main = (JSONObject) object.get("main");
        JSONObject wind = (JSONObject) object.get("wind");
        JSONObject weather = (JSONObject) ((JSONArray) object.get("weather")).get(0);

        double tempK = StringUtils.tryParseDouble(main.get("temp").toString());
        double tempC = round((tempK - 273.15), 1);
        double tempF = round((tempK - 273.15) * 9 / 5 + 32, 1);
        int humidity = StringUtils.tryParseInt(main.get("humidity").toString());
        
        double windspeed = StringUtils.tryParseDouble(wind.get("speed").toString());
        double windspeedMiles = round(windspeed, 1);
        double windspeedKmph = round(windspeed * 1.60934, 1);
        String windDir = convertWindDirToCardinal(StringUtils.tryParseDouble(wind.get("deg").toString()));
        
        String description = WordUtils.capitalizeFully(weather.get("description").toString());
        long unixTime = System.currentTimeMillis() / 1000L;
        long latestUpdate = (unixTime - StringUtils.tryParseLong(object.get("dt").toString())) / 60;

        return String
                .format("Temp: %s°C / %s°F | Weather: %s | Humidity: %s%c | Wind Speed: %s kmph / %s mph | Wind Direction: %s | Latest Update: %s minute(s) ago.",
                        tempC, tempF, description, humidity, '%', windspeedKmph, windspeedMiles, windDir, latestUpdate);
    }

    private String parseJSONForForecast(JSONObject object)
    {
        if (!object.get("cod").toString().equals("200"))
        {
            return null;
        }
        
        JSONArray list = (JSONArray) object.get("list");
        DateFormat format2 = new SimpleDateFormat("EEEEEEEE", Locale.US);

        List<String> days = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++)
        {
            JSONObject day = (JSONObject) list.get(i);
            JSONObject temp = (JSONObject) day.get("temp");
            JSONObject weather = (JSONObject) ((JSONArray) day.get("weather")).get(0);
            Date date = new Date(StringUtils.tryParseLong(day.get("dt").toString()) * 1000);
            
            String dayOfWeek = format2.format(date);
            double minK = StringUtils.tryParseDouble(temp.get("min").toString());
            double maxK = StringUtils.tryParseDouble(temp.get("max").toString());
            double minC = round((minK - 273.15), 1);
            double minF = round((minK - 273.15) * 9 / 5 + 32, 1);
            double maxC = round((maxK - 273.15), 1);
            double maxF = round((maxK - 273.15) * 9 / 5 + 32, 1);
            
            String description = WordUtils.capitalizeFully(weather.get("description").toString());

            days.add(String.format("%s: %s - %s°C, %s - %s°F, %s", dayOfWeek, minC, maxC, minF,
                    maxF, description));
        }
        return StringUtils.join(days, " | ");
    }

    private JSONObject getJSON(String urlString) throws ParseException
    {
        return (JSONObject) new JSONParser().parse(URLUtils.grab(urlString));
    }
    
    private String convertWindDirToCardinal(double degrees)
    {
    	String directions[] = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
    	int i = (int) ((degrees + 11.25) / 22.5);
        return directions[i % 16];
    }
    
    private double round(double value, int places) 
    {
        if (places < 0)
        {
        	return 0.0;
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
