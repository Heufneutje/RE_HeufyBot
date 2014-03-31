package heufybot.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

public class Logger 
{
	public static void log(String line, String target, String network)
	{
		String baseLogPath = HeufyBot.getInstance().getGlobalConfig().getLogPath();
		
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
			consoleLogLine = dateFormat.format(date) + " " + target + "@" + network + " - " + line;
		}
		
		consoleLogLine = StringUtils.toValid3ByteUTF8String(consoleLogLine);
		
		System.out.println(consoleLogLine);
		
		//Output to logfile
		Path path = Paths.get(baseLogPath).toAbsolutePath();
		if(network == null)
		{
			FileUtils.writeFileAppend(path.resolve("server.log").toString(), consoleLogLine + "\n");
		}
		else
		{
			FileUtils.touchDir(path.resolve(network + "/" + target).toString());
			
			line = dateFormat.format(date) + " " + line;
			
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			FileUtils.writeFileAppend(path.resolve(network + "/" + target + "/" + dateFormat.format(date) + ".log").toString(), line + "\n");
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
