package heufybot.modules;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyEvent 
{
	private Date date;
	private String eventString;
	
	public MyEvent(Date date, String eventString)
	{
		this.date = date;
		this.eventString = eventString;
	}

	public Date getDate() 
	{
		return date;
	}
	
	public String getFormattedDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return dateFormat.format(date);
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getEventString() 
	{
		return eventString;
	}

	public void setEventString(String eventString)
	{
		this.eventString = eventString;
	}
	
	public static Date formatDate(String dateString)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try 
		{
			return dateFormat.parse(dateString);
		} 
		catch (ParseException e) 
		{
			return null;
		}
	}
}
