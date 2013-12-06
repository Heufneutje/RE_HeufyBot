package heufybot.modules;

import heufybot.core.HeufyBot;
import heufybot.utils.FileUtils;
import heufybot.utils.PastebinUtils;
import heufybot.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Log extends Module
{
	private String source;
	private String dateString;

	public Log()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(log)($| .*)";
	}

	public void processEvent(String source, String mesage, String triggerUser, List<String> params)
	{
		if(params.size() == 1)
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			this.dateString = dateFormat.format(date);

			this.source = source;
			post(bot);
		}
		else
		{
			this.source = source;
			
			if(params.get(1).startsWith("-"))
			{
				try
				{
					int numberdays = Integer.parseInt(params.get(1));
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, numberdays);
					
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					this.dateString = dateFormat.format(cal.getTime());
					
					post(bot);
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
	    		
					post(bot);
		    	}
				catch (Exception e)
				{
					bot.getIRC().cmdPRIVMSG(source, "Parser Error: Make sure the date format for the log is: yyyy-mm-dd");
				}
			}
		}
	}
  
	public void post(final HeufyBot bot)
	{
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				String targetLog = source;
				String filePath = "logs/" + bot.getIRC().getServerInfo().getNetwork() + "/" + targetLog + "/" + dateString + ".log";
				
				if(FileUtils.readFile(filePath) == null)
				{
					bot.getIRC().cmdPRIVMSG(source, "[Log] I do not have that log");
					return;
				}
				
				String result = PastebinUtils.post(FileUtils.readFile(filePath), "Log for " + source + " on " + dateString, "10M");
				if(result != null)
				{
					if(result.startsWith("http://pastebin.com/"))
					{
						bot.getIRC().cmdPRIVMSG(source, "Log for " + source + " on " + dateString + " posted: " + result + " (Link expires in 10 minutes)");
					}
					else
					{
						bot.getIRC().cmdPRIVMSG(source, "Error: " + result);
					}
				}
				else
				{
					bot.getIRC().cmdPRIVMSG(source, "Error: Log could not be posted");
				}
			}
		});
		thread.start();
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public String getHelp()
	{
		return "Commands: " + commandPrefix + "log, " + commandPrefix + "log <YYYY-MM-DD>, " + commandPrefix + "log -<numberofdays> | Provides a log of the current channel for today, or another date if specified.";
	}

	@Override
	public void onUnload()
	{
	}
}
