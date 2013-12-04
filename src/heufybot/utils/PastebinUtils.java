package heufybot.utils;

import heufybot.core.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class PastebinUtils
{
	public static String post(final String data, final String title, String expire)
	{
		try
	    {
			FileUtils.touchFile("featuredata/pastebinapikey.txt");
			String log = "";
			
			String api_dev_key = FileUtils.readFile("featuredata/pastebinapikey.txt").replaceAll("\n", "");
			if(api_dev_key.equals(""))
			{
				Logger.error("PasteBin", "No API key found");
				return null;
			}
			
			URL url = new URL("http://pastebin.com/api/api_post.php");
	        URLConnection connection = url.openConnection();
	        connection.setDoOutput(true);

	        String api_option = "paste";
	        String api_paste_code = log;

	        String api_user_key = "";
	        String api_paste_name = title;
	        String api_paste_format = "text";
	        String api_paste_private = "1";
	        String api_paste_expire_date = expire;

	        String postData = URLEncoder.encode("api_dev_key", "UTF8") + "=" + URLEncoder.encode(api_dev_key, "UTF8") + "&" + 
	        		URLEncoder.encode("api_option", "UTF8") + "=" + URLEncoder.encode(api_option, "UTF8") + "&" + 
	        		URLEncoder.encode("api_paste_code", "UTF8") + "=" + URLEncoder.encode(api_paste_code, "UTF8") + "&" + 
	        		URLEncoder.encode("api_user_key", "UTF8") + "=" + URLEncoder.encode(api_user_key, "UTF8") + "&" + 
	        		URLEncoder.encode("api_paste_name", "UTF8") + "=" + URLEncoder.encode(api_paste_name, "UTF8") + "&" + 
	        		URLEncoder.encode("api_paste_format", "UTF8") + "=" + URLEncoder.encode(api_paste_format, "UTF8") + "&" + 
	        		URLEncoder.encode("api_paste_private", "UTF8") + "=" + URLEncoder.encode(api_paste_private, "UTF8") + "&" + 
	        		URLEncoder.encode("api_paste_expire_date", "UTF8") + "=" + URLEncoder.encode(api_paste_expire_date, "UTF8");

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
	        return result;
	    }
	    catch (Exception e)
	    {
	    	Logger.error("PasteBin", "Something went wrong while posting to PasteBin");
	    	return null;
	    }
	}	
}