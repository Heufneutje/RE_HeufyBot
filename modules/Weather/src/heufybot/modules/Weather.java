package heufybot.modules;

import java.io.IOException;
import java.util.List;

import org.json.simple.parser.ParseException;

public class Weather extends Module {

	public Weather(){
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(weather)($| .*)";
	}

	// TODO Fix code redundancy, and get rid of those labels
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) {
		if (params.size() == 1)
			params.add(triggerUser);

		params.remove(0);
		GeocodingInterface geo = new GeocodingInterface();

		// First try latitude and longitude. If these are not in fact lat/lon this will fail before any network stuff is done
		latlong: {
			try {
				float latitude = Float.parseFloat(params.get(0));
				float longitude = Float.parseFloat(params.get(1));
				try {
					Geolocation location = geo.getGeolocationForLatLng(latitude, longitude);
					String weather = getWeatherFromGeolocation(location);
					String prefix = location.success ? "City: " + location.locality : "Location: " + latitude + "," + longitude;

					bot.getIRC().cmdPRIVMSG(source, String.format("%s | %s", prefix, weather));
					return;
				} catch (IOException e) {
					bot.getIRC().cmdPRIVMSG(source, "I'm sorry " + triggerUser + ", I'm afraid I can't let you do that.");
					e.printStackTrace();
					return;
				} catch (ParseException e) {
					bot.getIRC().cmdPRIVMSG(source, "I don't think that's even a location in this multiverse...");
					e.printStackTrace();
					return;
				}
			} catch (NumberFormatException e){
				// Nothing to see here, just not latitude/longitude, continuing.
			} catch (IndexOutOfBoundsException e){
				// Either this is fuzzing or invalid input. Either way we don't care, and should check the next two cases.
			}
		}

		ircuser: {
			try {
				Geolocation location = geo.getGeolocationForIRCUser(params.get(0));
				if (location == null){
					break ircuser;
				}
				String weather = getWeatherFromGeolocation(location);

				bot.getIRC().cmdPRIVMSG(source, String.format("Location: %s | %s", location.locality, weather));
				return;
			} catch (IOException e) {
				bot.getIRC().cmdPRIVMSG(source, "I'm sorry " + triggerUser + ", I'm afraid I can't let you do that.");
				e.printStackTrace();
				return;
			} catch (ParseException e) {
				bot.getIRC().cmdPRIVMSG(source, "I don't think that's even a user in this multiverse...");
				e.printStackTrace();
				return;
			}
		}

		place: {
			try {
				Geolocation location = geo.getGeolocationForPlace(message.substring(message.indexOf(' ') + 1));
				if (!location.success){
					bot.getIRC().cmdPRIVMSG(source, "[Weather] I don't think that's even a location in this multiverse...");
					return;
				}
				String weather = getWeatherFromGeolocation(location);
				bot.getIRC().cmdPRIVMSG(source, String.format("[Weather]: %s | %s", location.locality, weather));
				return;
			} catch (IOException e) {
				bot.getIRC().cmdPRIVMSG(source, "[Weather] That doesn't look like it's gonna work");
				e.printStackTrace();
				return;
			} catch (ParseException e) {
				bot.getIRC().cmdPRIVMSG(source, "[Weather] I don't think that's even a location in this multiverse...");
				e.printStackTrace();
				return;
			}
		}
	}

	private String getWeatherFromGeolocation(Geolocation location) throws IOException, ParseException{
		WeatherInterface weatherInterface = new WeatherInterface();
		String weather = weatherInterface.getWeather(location.latitude, location.longitude);
		return weather;
	}

	@Override
	public String getHelp() {
		return "Commands: " + commandPrefix + "weather <place>/<latitude longitude>/<ircuser> | Makes the bot get the current weather conditions at the location specified or at the location of the ircuser.";
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub

	}

}
