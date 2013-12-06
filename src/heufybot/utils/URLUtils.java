package heufybot.utils;

import heufybot.core.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils
{
	public static String grab(String urlString)
	{
		try
		{
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader bufReader;
			String line;
			String data = "";
		    bufReader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
		    
		    while( (line = bufReader.readLine()) != null)
		    {
		    	data += line + " ";
		    }
		    bufReader.close();
		    return data;
		}
		catch(Exception e)
		{
			Logger.log("URL Utilities", "Couldn't grab URL \"" + urlString + "\"");
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getFullHostname(String urlString)
	{
		try
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
			connection.setInstanceFollowRedirects(false);
			while (connection.getResponseCode() / 100 == 3)
			{
				urlString = connection.getHeaderField("location");
			    connection = (HttpURLConnection) new URL(urlString).openConnection();
			}
			return urlString;
		}
		catch (Exception e)
		{
			Logger.log("URL Utilities", "Couldn't get full hostname for \"" + urlString + "\"");
			return null;
		}
	}
	
	public static String getHost(String urlString)
	{
		URL url;
		try
		{
			url = new URL(urlString);
			return url.getHost();
		}
		catch (MalformedURLException e)
		{
			Logger.log("URL Utilities", "Couldn't get the host for \"" + urlString + "\"");
			return null;
		}
	}
	
	public static String shortenURL(String urlstring)
	{
		try
		{
			String json = "";
			if(urlstring.startsWith("http:"))
			{
				json = "{\"longUrl\": \"" + urlstring + "\"}";
			}
			else
			{
				json = "{\"longUrl\": \"http://"+urlstring+"/\"}";
			}
			
			URL url = new URL("https://www.googleapis.com/urlshortener/v1/url");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST"); 
	        connection.setRequestProperty("Content-Type", "application/json"); 
	        connection.setDoOutput(true);

	        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
	        out.write(json);
	        out.close();

	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String decodedString;
	        String result = "";
	        while ((decodedString = in.readLine()) != null)
	        {
	        	result += decodedString;
	        }
	        in.close();
	        return result.substring(result.indexOf("http://goo.gl"), result.indexOf("\"", result.indexOf("http://goo.gl")));
		}
		catch(Exception e)
		{
			Logger.log("URL Utilities", "Couldn't shorten URL \"" + urlstring + "\"");
			return null;
		}
	}
}