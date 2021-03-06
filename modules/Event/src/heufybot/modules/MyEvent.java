package heufybot.modules;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyEvent
{
    private String user;
    private Date date;
    private String eventString;

    public MyEvent(String user, Date date, String eventString)
    {
        this.setUser(user);
        this.date = date;
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

    public String getUser()
    {
        return this.user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public Date getDate()
    {
        return this.date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getFormattedDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(this.date);
    }

    public String getEventString()
    {
        return this.eventString;
    }

    public void setEventString(String eventString)
    {
        this.eventString = eventString;
    }
}
