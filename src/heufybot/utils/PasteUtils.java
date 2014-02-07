package heufybot.utils;

import heufybot.core.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class PasteUtils
{
	public static String post(final String data, final String title, String expire)
	{
		try
	    {
			URL url = new URL("http://paste.ee/api");
	        URLConnection connection = url.openConnection();
	        connection.setDoOutput(true);

	        String key = "public";
	        String format = "json";

	        String postData = URLEncoder.encode("key", "UTF8") + "=" + URLEncoder.encode(key, "UTF8") + "&" + 
	        		URLEncoder.encode("description", "UTF8") + "=" + URLEncoder.encode(title, "UTF8") + "&" + 
	        		URLEncoder.encode("paste", "UTF8") + "=" + URLEncoder.encode(data, "UTF8") + "&" + 
	        		URLEncoder.encode("expiration", "UTF8") + "=" + URLEncoder.encode(expire, "UTF8") + "&" + 
	        		URLEncoder.encode("format", "UTF8") + "=" + URLEncoder.encode(format, "UTF8") + "&";

	        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
	        out.write(postData);
	        out.close();

	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String decodedString;
	        String result = "";
	        while ((decodedString = in.readLine()) != null)
	        {
	        	result += decodedString;
	        }
	        in.close();
	        
	        JSONObject json = (JSONObject) new JSONParser().parse(result);
	        JSONObject paste = (JSONObject) json.get("paste");
	        return paste.get("raw").toString();
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    	Logger.error("Paste.ee", "Something went wrong while posting to Paste.ee");
	    	return null;
	    }
	}	
}