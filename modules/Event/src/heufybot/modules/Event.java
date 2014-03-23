package heufybot.modules;

import heufybot.core.Logger;
import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Event extends Module
{
	private String eventsPath = "data/events.json";
	private List<MyEvent> events;
	
	public Event()
	{
		this.authType = AuthType.Anyone;
		this.apiVersion = "0.5.0";
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(event|timetill|timesince|r(emove)?event)($| .*)";
		
		this.events = new ArrayList<MyEvent>();
	}

	public void processEvent(String source, String message, String triggerUser, List<String> params)
	{
		if(message.toLowerCase().matches("^" + commandPrefix + "event.*"))
		{
			if(params.size() < 3)
			{
				bot.getIRC().cmdPRIVMSG(source, "You didn't specify an event.");
				return;
			}
			
			params.remove(0);
			
			Date eventDate;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try
			{
				eventDate = dateFormat.parse(params.get(0) + " " + params.get(1));
				
				if(params.size() < 3)
				{
					bot.getIRC().cmdPRIVMSG(source, "You didn't specify an event.");
					return;
				}
				
				params.remove(0);
				params.remove(0);
			}
			catch (java.text.ParseException e) 
			{
				dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				try 
				{
					eventDate = dateFormat.parse(params.get(0));
					params.remove(0);
				} 
				catch (java.text.ParseException e1) 
				{
					bot.getIRC().cmdPRIVMSG(source, "The date you specified is invalid. Use \"yyyy-MM-dd\" or \"yyyy-MM-dd HH:mm\" as the format.");
					return;
				}
			}
			
			MyEvent event = new MyEvent(triggerUser, eventDate, StringUtils.join(params, " "));
			
			int latestDateIndex = 0;
			for(int i = 0; i < events.size(); i++)
			{
				if(eventDate.after(events.get(i).getDate()))
				{
					latestDateIndex = i + 1;
				}
			}
			
			events.add(latestDateIndex, event);
			writeEvents();
			
			bot.getIRC().cmdPRIVMSG(source, "Event \"" + event.getEventString() + "\" on the date " + event.getFormattedDate() + " (UTC) was added to the events database!");
		}
		else if(message.toLowerCase().matches("^" + commandPrefix + "r(emove)?event.*"))
		{
			
		}
		else if(message.toLowerCase().matches("^" + commandPrefix + "timetill.*"))
		{
			if(params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "You didn't specify an event.");
				return;
			}
			
			params.remove(0);
			String search = StringUtils.join(params, " ");
			for(MyEvent event : events)
			{
				Date now = new Date();
				if(event.getEventString().toLowerCase().matches(".*" + search.toLowerCase() + ".*") && event.getDate().after(now))
				{
					String timeDifference = getTimeDifferenceString(now, event.getDate());
					bot.getIRC().cmdPRIVMSG(source, event.getUser() + "'s event \"" + event.getEventString() + "\" will occur in " + timeDifference + ".");
					return;
				}
			}
			bot.getIRC().cmdPRIVMSG(source, "No event matching \"" + search + "\" was found in the events database.");
		}
		else if(message.toLowerCase().matches("^" + commandPrefix + "timesince.*"))
		{
			if(params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "You didn't specify an event.");
				return;
			}
			
			params.remove(0);
			String search = StringUtils.join(params, " ");
			for(MyEvent event : events)
			{
				Date now = new Date();
				if(event.getEventString().toLowerCase().matches(".*" + search.toLowerCase() + ".*") && event.getDate().before(now))
				{
					String timeDifference = getTimeDifferenceString(event.getDate(), now);
					bot.getIRC().cmdPRIVMSG(source, event.getUser() + "'s event \"" + event.getEventString() + "\" occurred " + timeDifference + " ago.");
					return;
				}
			}
			bot.getIRC().cmdPRIVMSG(source, "No event matching \"" + search + "\" was found in the events database.");
		}
	}

	@Override
	public String getHelp(String message) 
	{
		return "Commands: " + commandPrefix + "uptime | Shows how long the bot has been running.";
	}
	
	@Override
	public void onLoad()
	{
		if(FileUtils.touchFile(eventsPath))
		{
			FileUtils.writeFile(eventsPath, "[]");
		}
		readEvents();
	}

	@Override
	public void onUnload()
	{
		writeEvents();
	}
	
	private int elapsed(Calendar before, Calendar after, int field) 
	{
	    Calendar clone = (Calendar) before.clone(); // Otherwise changes are been reflected.
	    int elapsed = -1;
	    while (!clone.after(after)) 
	    {
	        clone.add(field, 1);
	        elapsed++;
	    }
	    return elapsed;
	}
	
	private String getTimeDifferenceString(Date date1, Date date2)
	{
		Calendar start = Calendar.getInstance();
		start.setTime(date1);
		Calendar end = Calendar.getInstance();
		end.setTime(date2);
		
		Integer[] elapsed = new Integer[3];
		Calendar clone = (Calendar) start.clone(); // Otherwise changes are been reflected.
		elapsed[0] = elapsed(clone, end, Calendar.DATE);
		clone.add(Calendar.DATE, elapsed[0]);
		elapsed[1] = (int) (end.getTimeInMillis() - clone.getTimeInMillis()) / 3600000;
		clone.add(Calendar.HOUR, elapsed[1]);
		elapsed[2] = (int) (end.getTimeInMillis() - clone.getTimeInMillis()) / 60000;
		clone.add(Calendar.MINUTE, elapsed[2]);
		
		return elapsed[0] + " day(s), " + elapsed[1] + " hour(s) and " + elapsed[2] + " minute(s)";
	}
	
	private void readEvents()
	{
		try
		{
			JSONArray eventsArray = (JSONArray) new JSONParser().parse(FileUtils.readFile(eventsPath));
			for(int i = 0; i < eventsArray.size(); i++)
			{
				JSONObject eventObject = (JSONObject) eventsArray.get(i);
				String user = eventObject.get("user").toString();
				Date date = MyEvent.formatDate(eventObject.get("date").toString());
				String eventString = eventObject.get("event").toString();
				MyEvent event = new MyEvent(user, date, eventString);
				events.add(event);
			}
		} 
		catch (ParseException e)
		{
			Logger.error("Module: Event", "The events database could not be read.");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void writeEvents()
	{
		JSONArray eventsArray = new JSONArray();
		for(MyEvent event : events)
		{
			JSONObject eventObject = new JSONObject();
			eventObject.put("user", event.getUser());
			eventObject.put("date", event.getFormattedDate());
			eventObject.put("event", event.getEventString());
			eventsArray.add(eventObject);
		}
		FileUtils.writeFile(eventsPath, eventsArray.toJSONString());
	}
}
