package heufybot.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import heufybot.utils.FileUtils;

public class Logger 
{
	public static void log(String line, String target)
	{
		//Timestamp line
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm]");
		Date date = new Date();
		
		//Output to console
		if(target.equals(""))
		{
			System.out.println(dateFormat.format(date) + " " + line);
		}
		else
		{
			System.out.println(dateFormat.format(date) + " " + target + " - " + line);
		}
		
		//Output to logfile
		FileUtils.writeFileAppend("test.log", line + "\n");
	}
	
	public static void log(String line)
	{
		log(line, "");
	}
	
	public static void error(String errorSource, String line)
	{
		System.err.println(errorSource + " - ERROR: " + line);
	}
}