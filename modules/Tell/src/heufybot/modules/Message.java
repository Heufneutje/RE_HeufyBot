package heufybot.modules;

public class Message
{
	private String from;
	private String text;
	private String dateSent;
	private String messageSource;
	
	Message(String from, String text, String dateSent, String messageSource)
	{
		this.from = from;
		this.text = text;
		this.dateSent = dateSent;
		this.messageSource = messageSource;
	}
	
	public String getFrom() 
	{
		return from;
	}
	public String getText() 
	{
		return text;
	}
	public String getDateSent()
	{
		return dateSent;
	}
	public String getMessageSource()
	{
		return messageSource;
	}
}
