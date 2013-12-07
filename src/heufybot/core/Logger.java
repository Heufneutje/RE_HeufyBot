package heufybot.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import heufybot.utils.FileUtils;

public class Logger 
{
	public static void log(String line, String target, String network)
	{
		//Timestamp line
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm]");
		Date date = new Date();
		
		//Output to console
		String consoleLogLine = "";
		if(target == null)
		{
			consoleLogLine = dateFormat.format(date) + " " + line;
		}
		else
		{
			consoleLogLine = dateFormat.format(date) + " " + target + " - " + line;
		}
		
		System.out.println(consoleLogLine);
		
		//Output to logfile
		if(network == null)
		{
			FileUtils.writeFileAppend("logs/server.log", consoleLogLine + "\n");
		}
		else
		{
			FileUtils.touchDir("logs/" + network);
		    FileUtils.touchDir("logs/" + network + "/" + target);

		    line = dateFormat.format(date) + " " + line;
		    
		    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    FileUtils.writeFileAppend("logs/" + network + "/" + target + "/" + dateFormat.format(date) + ".log", line + "\n");
		}
	}
	
	public static void log(String line)
	{
		log(line, null, null);
	}
	
	public static void error(String errorSource, String line)
	{
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm]");
		Date date = new Date();
		
		System.err.println(dateFormat.format(date) + " " + errorSource + " - ERROR: " + line);
	}
}