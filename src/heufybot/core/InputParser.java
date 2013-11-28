package heufybot.core;

import heufybot.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputParser 
{
	private IRC irc;
	private List<String> connectCodes;
	
	public InputParser(IRC irc)
	{
		this.irc = irc;
		this.connectCodes = new ArrayList<String>(Arrays.asList("001", "002", "003", "004", "005",
				"251", "252", "253", "254", "255", "375", "376"));
	}
	
	public void parseLine(String line)
	{
		Logger.log(line);
		
		List<String> parsedLine = MessageUtils.tokenizeLine(line);

        String senderInfo = "";
        if (parsedLine.get(0).charAt(0) == ':')
                senderInfo = parsedLine.remove(0);
        
        String command = parsedLine.remove(0).toUpperCase();
        
        if (command.equals("PING"))
        {
        	Logger.log("PONG " + parsedLine.get(0));
            irc.cmdPONG(parsedLine.get(0));
        	return;
        }
        else if(command.startsWith("ERROR"))
        {
        	//Connection closed by server
        	Logger.log(parsedLine.get(0));
        	irc.disconnect();
        }
        else
        {
        	
        }
	}
}