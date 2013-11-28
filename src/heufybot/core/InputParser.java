package heufybot.core;

import heufybot.utils.MessageUtils;
import java.util.List;

public class InputParser 
{
	private IRC irc;
	
	public InputParser(IRC irc)
	{
		this.irc = irc;
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
	}
}