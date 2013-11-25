package heufybot.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import heufybot.utils.FileUtils;

public class Logger 
{
	public static void log(String line)
	{
		//Timestamp line
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm]");
		Date date = new Date();
		line = dateFormat.format(date) + " " + line;
		
		//Output to console
		System.out.println(line);
		
		//Output to logfile
		FileUtils.writeFileAppend("test.log", line + "\n");
	}
	
	public static void error(String errorSource, String line)
	{
		System.err.println(errorSource + " - ERROR: " + line);
	}
}