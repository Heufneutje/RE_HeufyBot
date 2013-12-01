package heufybot.core.cap;

public class CAPException extends RuntimeException
{
	private static final long serialVersionUID = -329500288011547355L;
	
	public CAPException(Reason reason, String detail)
	{
		this(reason, detail, null);
	}

	public CAPException(Reason reason, String detail, Throwable cause) 
	{
		super(generateMessage(reason, detail), cause);
	}
	
	protected static String generateMessage(Reason reason, String message) 
	{
		return reason + ": " + message;
	}
	
	public static enum Reason 
	{
		UnsupportedCapability,
		SASLFailed,
		Other
	}
}
