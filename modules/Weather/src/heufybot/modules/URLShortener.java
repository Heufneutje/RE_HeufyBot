package heufybot.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class URLShortener {
	private static final String apiURL = "https://www.googleapis.com/urlshortener/v1/url";

	public static String getShortenedURL(String longURL) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(apiURL);
		post.addHeader("Content-Type", "application/json");
		String entity = "{\"longUrl\": \"" + longURL + "\"}";
	
		try {
			post.setEntity(new ByteArrayEntity(
					entity.getBytes("UTF8")));
			HttpResponse response = client.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String nextLine;
			String json = "";
			while ((nextLine = reader.readLine()) != null){
				json += nextLine;
			}
			JSONObject object = (JSONObject)new JSONParser().parse(json);
			return object.get("id").toString();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return longURL;
	}
}
