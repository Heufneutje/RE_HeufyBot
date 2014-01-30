package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class LogDB extends Module
{
	private String dateString;

	public LogDB()
	{
		this.authType = Module.AuthType.Anyone;
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(log)($| .*)";
	}

	public void processEvent(String source, String mesage, String triggerUser, List<String> params)
	{
		if(params.size() == 1)
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			this.dateString = dateFormat.format(date);

			post(source);
		}
		else
		{
			if(params.get(1).startsWith("-"))
			{
				try
				{
					int numberdays = Integer.parseInt(params.get(1));
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, numberdays);
					
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					this.dateString = dateFormat.format(cal.getTime());
					
					post(source);
				}
				catch (Exception e)
				{
					bot.getIRC().cmdPRIVMSG(source, "Parser Error: Make sure the format is: log -<numberofdays>");
				}
			}
			else
			{
				try
				{
					List<String> dateParams = StringUtils.parseStringtoList(params.get(1), "-");
					
					int year = Integer.parseInt(dateParams.get(0));
					int month = Integer.parseInt(dateParams.get(1));
					int day = Integer.parseInt(dateParams.get(2));
	    		
					Calendar calendar = new GregorianCalendar(year, month, day);
					calendar.clear();
	    		
					this.dateString = params.get(1);
	    		
					post(source);
		    	}
				catch (Exception e)
				{
					bot.getIRC().cmdPRIVMSG(source, "Parser Error: Make sure the date format for the log is: yyyy-mm-dd");
				}
			}
		}
	}
  
	public void post(String source)
	{
		String targetLog = source;
		String filePath = "logs/" + bot.getIRC().getServerInfo().getNetwork() + "/" + targetLog + "/" + dateString + ".log";
		
		if(!FileUtils.fileExists(filePath))
		{
			bot.getIRC().cmdPRIVMSG(source, "[Log] I do not have that log");
			return;
		}
		
		String url = "http://logs.heufneutje.net/loggrab.php?channel=" + source.replaceAll("#", "") + "&date=" + dateString;
		bot.getIRC().cmdPRIVMSG(source, "Log for " + source + " on " + dateString + ": " + url);
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "log (<YYYY-MM-DD>/-<numberofdays>) | Provides a log of the current channel for today, or another date if specified.";
	}

	@Override
	public void onUnload()
	{
	}
}
