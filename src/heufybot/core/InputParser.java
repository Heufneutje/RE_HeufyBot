package heufybot.core;

import heufybot.utils.MessageUtils;
import heufybot.utils.ParsingUtils;
import heufybot.utils.enums.ConnectionState;
import heufybot.utils.enums.PasswordType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputParser 
{
	private IRC irc;
	private List<String> connectCodes;
	private int nickSuffix;
	
	public InputParser(IRC irc)
	{
		this.irc = irc;
		this.connectCodes = new ArrayList<String>(Arrays.asList("001", "002", "003", "004", "005",
				"251", "252", "253", "254", "255", "375", "376"));
		this.nickSuffix = 1;
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
        	return;
        }
        
        String sourceNick;
        String sourceLogin = "";
        String sourceHostname = "";
        String target = !parsedLine.isEmpty() ? parsedLine.get(0) : "";
        
        if(target.startsWith(":"))
        {
        	target = target.substring(1);
        }
        
        int exclamationPosition = senderInfo.indexOf("!");
        int atPosition = senderInfo.indexOf("@");
        
        if (senderInfo.startsWith(":"))
        {
	        if(exclamationPosition > 0 && atPosition > 0 && exclamationPosition < atPosition)
	        {
	        	//This is a command
	        	sourceNick = senderInfo.substring(1, exclamationPosition);
	        	sourceLogin = senderInfo.substring(exclamationPosition + 1, atPosition);
	        	sourceHostname = senderInfo.substring(atPosition + 1);
	        }
	        else
	        {
	        	//This is probably a server response
	        	int code = ParsingUtils.tryParseInt(command);
	        	if(code != -1)
	        	{
	        		if(irc.getConnectionState() != ConnectionState.Connected)
	        		{
	        			handleConnect(line, parsedLine, command);
	        		}
	        		else
	        		{
	        			handleServerResponse(line, parsedLine, code);
	        		}
	        		return;
	        	}
	        	else
	        	{
	        		//Could be a nick without a login or host
	            	sourceNick = senderInfo;
	        	}
	        }
        }
        else
        {
        	//No idea what this is
    		return;
        }
        
        if(sourceNick.startsWith(":"))
        {
        	sourceNick = sourceNick.substring(1);
        }
        
        handleCommand(line, parsedLine, sourceNick, sourceLogin, sourceHostname, command, target);
	}
	
	public void handleConnect(String line, List<String> parsedLine, String code)
	{
		if(connectCodes.contains(code))
		{
			irc.setConnectionState(ConnectionState.Connected);
			irc.setLoggedInNick(irc.getConfig().getNickname() + (nickSuffix == 1 ? "" : nickSuffix));
			
			nickSuffix = 1;
			
			Logger.log("*** Logged onto server");
			
			if(irc.getConfig().getPasswordType() == PasswordType.NickServPass)
			{
				irc.nickservIdentify(irc.getConfig().getPassword());
			}
			
			if(irc.getConfig().getAutoJoinEnabled() && irc.getConfig().getAutoJoinChannels().length > 0)
			{
				for(int i = 0; i < irc.getConfig().getAutoJoinChannels().length; i++)
				{
					String[] channel = irc.getConfig().getAutoJoinChannels()[i].split(" ");
					if(channel.length > 1)
					{
						//This channel has a key
						irc.cmdJOIN(channel[0], channel[1]);
					}
					else
					{
						//This channel doesn't need a key
						irc.cmdJOIN(channel[0], "");
					}
					//TODO Process channel joins properly
				}
			}
		}
		else if(code.equals("433"))
		{
			//Nickname is already taken
			if(irc.getConfig().getAutoNickChange())
			{
				//Try a different nickname
				String usedNick = parsedLine.get(1);
				nickSuffix++;
				Logger.log("*** Nickname " + usedNick + " was already taken. Trying " + irc.getConfig().getNickname() + nickSuffix + "...");
				irc.cmdNICK(irc.getConfig().getNickname() + nickSuffix);
			}
			else
			{
				//Give up
				Logger.error("IRC Login", "Login failed. Nickname was already taken");
				irc.disconnect();
			}
		}
		else if(code.startsWith("4") || code.startsWith("5") && !code.equals("439"))
		{
			//Couldn't login. Disconnect.
			Logger.error("IRC Login", "Login failed.");
			irc.disconnect();
		}
	}
	
	public void handleServerResponse(String line, List<String> parsedLine, int code)
	{
		
	}
	
	public void handleCommand(String line, List<String> parsedLine, String sourceNick, String sourceLogin, String sourceHostname, String command, String target)
	{
		
	}
}