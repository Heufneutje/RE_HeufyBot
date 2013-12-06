package heufybot.modules;

import heufybot.utils.StringUtils;
import heufybot.utils.URLUtils;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GeocodingInterface {
	private final static String APIAddress = "http://maps.googleapis.com/maps/api/geocode/json?";
	
	public Geolocation getGeolocationForLatLng(float latitude, float longitude) throws ParseException
	{
		StringBuilder builder = new StringBuilder();
		builder.append(APIAddress);
		builder.append("latlng=" + latitude + "," + longitude);
		builder.append("&sensor=false");
		JSONObject json = getJSON(builder.toString());
		Geolocation geo = geolocationFromJson(json);
		if (geo.success)
		{
			return geo;
		}
		
		geo.latitude = latitude;
		geo.longitude = longitude;
		return geo;
	}

	public Geolocation getGeolocationForPlace(String locationName) throws ParseException
	{		
		List<String> locationComponents = StringUtils.parseStringtoList(locationName, " ");
		
		StringBuilder builder = new StringBuilder();
		builder.append(APIAddress);
		builder.append("address=");		
		for (String s : locationComponents) 
		{
			builder.append(s);
			builder.append('+');
		}
		builder.deleteCharAt(builder.length() - 1);
		
		builder.append("&sensor=false");
		JSONObject json = getJSON(builder.toString());
		return geolocationFromJson(json);
	}
	
	private Geolocation geolocationFromJson(JSONObject object)
	{
		Geolocation geo = new Geolocation();
		geo.success = object.get("status").equals("OK");
		if (!geo.success)
		{
			return geo;
		}
		JSONObject firstHit = (JSONObject) ((JSONArray)object.get("results")).get(0);
		geo.locality = siftForCreepy(firstHit);
		System.out.println(firstHit);
		JSONObject location = (JSONObject)((JSONObject)firstHit.get("geometry")).get("location");
		geo.latitude = Float.parseFloat(location.get("lat").toString());
		geo.longitude = Float.parseFloat(location.get("lng").toString());
		
		return geo;
	}
	
	private String siftForCreepy(JSONObject object)
	{
		JSONArray addresses = (JSONArray) object.get("address_components");
		
		List<String> locationInfo = new ArrayList<String>();
		for(int i = 0; i < addresses.size(); i++)
		{
			JSONArray types = (JSONArray) ((JSONObject)addresses.get(i)).get("types");
			
			// Creepy-alarm! Go less specific!
			if (!(types.contains("street_number") || types.contains("route") || types.contains("neighborhood") || types.contains("postal_code") || types.contains("administrative_area_level_2")))
			{
				locationInfo.add(((JSONObject)addresses.get(i)).get("long_name").toString());
			}
		}
		return StringUtils.join(locationInfo, ", ");
	}

	private JSONObject getJSON(String urlString) throws ParseException 
	{
		return (JSONObject)new JSONParser().parse(URLUtils.grab(urlString));
	}

	public Geolocation getGeolocationForIRCUser(String IRCUser) throws ParseException
	{
		String userLocation = URLUtils.grab("http://tsukiakariusagi.net/chatmaplookup.php?nick=" + IRCUser);

		if(userLocation != null)
		{
			if(userLocation.contains(","))
			{
				List<String> floats = StringUtils.parseStringtoList(userLocation, ",");
				return getGeolocationForLatLng(Float.parseFloat(floats.get(0)), Float.parseFloat(floats.get(1)));
			}	
		}
		return null;
	}
}
