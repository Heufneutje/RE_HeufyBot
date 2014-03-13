package heufybot.modules;

import heufybot.core.Logger;
import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Tell extends Module
{
	private class Message
	{
		public String from;
		public String text;
		public String dateSent;
		public String messageSource;
		
		Message(String from, String text, String dateSent, String messageSource)
		{
			this.from = from;
			this.text = text;
			this.dateSent = dateSent;
			this.messageSource = messageSource;
		}
	}
	private LinkedHashMap<String, ArrayList<Message>> tellsMap;
	private HashMap<String, Date> tellers;
	
	private String databasePath = "data/tells.json";
	
	public Tell()
	{
		this.authType = AuthType.Anyone;
		this.triggerTypes = new TriggerType[] { TriggerType.Message, TriggerType.Action };
		this.trigger = "^" + commandPrefix + "(tell|r(emove)?tell|s(ent)?tells)($| .*)";
		this.triggerOnEveryMessage = true;
		
		this.tellsMap = new LinkedHashMap<String, ArrayList<Message>>();
		this.tellers = new HashMap<String, Date>();
	}

	@Override
	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "tell <user> <message>, " + commandPrefix + "rtell <message>, " + commandPrefix + "senttells | Tells the specified user a message the next time they speak, removes a message sent by you from the database or lists your pending messages.";
	}

	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params)
	{
		if(message.toLowerCase().matches("^" + commandPrefix + "tell.*"))
		{
			if(params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "Tell what?");
				return;
			}
			params.remove(0);
			if(params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "What do you want me to tell them?");
				return;
			}
			
			Date timestamp = tellers.get(triggerUser);
			if(timestamp != null)
			{
				long timeStampDifference = (new Date().getTime() - timestamp.getTime()) / 1000;
				System.out.println(timeStampDifference);
				if(timeStampDifference < 60)
				{
					bot.getIRC().cmdPRIVMSG(source, "Calm down, " + triggerUser + "! You just sent a message " + timeStampDifference + " seconds ago! You have to wait " + (60 - timeStampDifference) + " more seconds.");
					return;
				}
			}
			
			String[] recepients;
			if(params.get(0).contains("&"))
			{
				recepients = params.get(0).split("&");
			}
			else
			{
				recepients = new String[] {params.get(0)};
			}
			params.remove(0);
			for(int i = 0; i < recepients.length; i++)
			{
				String recepient = fixRegex(recepients[i]);
				if(triggerUser.toLowerCase().matches(recepient.toLowerCase()))
				{
					bot.getIRC().cmdPRIVMSG(source, "Why are you telling yourself that?");
					return;
				}
				else if(recepients[i].equalsIgnoreCase(bot.getIRC().getNickname()))
				{
					bot.getIRC().cmdPRIVMSG(source, "Thanks for telling me that, " + triggerUser + ".");
					return;
				}

				String messageToSend = StringUtils.join(params, " ");
				Date date = new Date();
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z)");
				String dateString = format.format(date);
				String messageSource = "";
				if(source.equals(triggerUser))
				{
					messageSource = "PM";
				}
				else
				{
					messageSource = "Channel";
				}
				
				Message tellMessage = new Message(triggerUser, messageToSend, dateString, messageSource);
				
				if(!tellsMap.containsKey(recepient))
				{
					tellsMap.put(recepient, new ArrayList<Message>());
				}
				
				tellsMap.get(recepient).add(tellMessage);
				tellers.put(triggerUser, new Date());
				
				writeMessages();
				bot.getIRC().cmdPRIVMSG(source, "Okay, I'll tell " + recepients[i] + " that next time they speak.");
			}
		}
		else if(message.toLowerCase().matches("^" + commandPrefix + "r(emove)?tell.*"))
		{
			if(params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "Remove what?");
				return;
			}
			
			params.remove(0);
			String searchString = fixRegex(StringUtils.join(params, " "));
			boolean matchFound = false;
			for(Iterator<String> iter = tellsMap.keySet().iterator(); iter.hasNext() && !matchFound;)
			{
				String user = iter.next();
				ArrayList<Message> sentMessages = tellsMap.get(user);
				for(Iterator<Message> iter2 = sentMessages.iterator(); iter2.hasNext() && !matchFound;)
				{
					Message sentMessage = iter2.next();
					if(sentMessage.from.equalsIgnoreCase(triggerUser) && sentMessage.text.matches(".*" + searchString + ".*"))
					{
						String messageString = "Message \"" + sentMessage.text + "\" sent to " + user + " on " + sentMessage.dateSent + " was removed from the message database!";
						if(sentMessage.messageSource.equals("PM"))
						{
							bot.getIRC().cmdNOTICE(triggerUser, messageString);
						}
						else
						{
							bot.getIRC().cmdPRIVMSG(source, messageString);
						}
						iter2.remove();
						writeMessages();
						matchFound = true;
					}
				}
				if(sentMessages.size() == 0)
				{
					tellsMap.remove(user);
				}
				if(!matchFound)
				{
					bot.getIRC().cmdPRIVMSG(source, "No message matching \"" + searchString + "\" was found.");
				}
			}
		}
		else if(message.toLowerCase().matches("^" + commandPrefix + "s(ent)?tells.*"))
		{
			if(params.size() > 1)
			{
				return;
			}
			
			ArrayList<Message> foundMessages = new ArrayList<Message>();
			for(Iterator<String> iter = tellsMap.keySet().iterator(); iter.hasNext();)
			{
				String user = iter.next();
				ArrayList<Message> sentMessages = tellsMap.get(user);
				for(Iterator<Message> iter2 = sentMessages.iterator(); iter2.hasNext();)
				{
					Message sentMessage = iter2.next();
					if(sentMessage.from.equalsIgnoreCase(triggerUser))
					{
						foundMessages.add(sentMessage);
					}
				}
				
				if(foundMessages.size() == 0)
				{
					bot.getIRC().cmdNOTICE(triggerUser, "There are no messages sent by you that have not been received yet.");
				}
				else
				{
					for(Message sentMessage : foundMessages)
					{
						bot.getIRC().cmdNOTICE(source, sentMessage.text + " < Sent to " + user + " on " + sentMessage.dateSent);
					}
				}
			}
		}
		
		//Automatic stuff
		int messageCount = 0;
		for(Iterator<String> iter = tellsMap.keySet().iterator(); iter.hasNext();)
		{
			String user = iter.next();
			if(triggerUser.toLowerCase().matches(user.toLowerCase()))
			{
				ArrayList<Message> sentMessages = tellsMap.get(user);
				for(Iterator<Message> iter2 = sentMessages.iterator(); iter2.hasNext() && messageCount < 3; messageCount++)
				{
					Message sentMessage = iter2.next();
					String messageString = sentMessage.text + " < From " + sentMessage.from + " on " + sentMessage.dateSent;
					if(sentMessage.messageSource.equals("PM"))
					{
						bot.getIRC().cmdNOTICE(triggerUser, messageString);
					}
					else
					{
						bot.getIRC().cmdPRIVMSG(source, messageString);
					}
					iter2.remove();
				}
				if(sentMessages.size() == 0)
				{
					tellsMap.remove(user);
				}
			}
		}
		writeMessages();
	}

	@Override
	public void onLoad()
	{
		if(FileUtils.touchFile(databasePath))
		{
			FileUtils.writeFile(databasePath, "[]");
		}
		readMessages();
	}

	@Override
	public void onUnload()
	{
		writeMessages();
	}
	
	public void readMessages()
	{
		try 
		{
			JSONArray recepients = (JSONArray) new JSONParser().parse(FileUtils.readFile(databasePath));
			for(int i = 0; i < recepients.size(); i++)
			{
				JSONObject recepient = (JSONObject) recepients.get(i);
				String name = recepient.get("name").toString();
				JSONArray messages = (JSONArray) recepient.get("messages");
				
				ArrayList<Message> messageList = new ArrayList<Message>();
				for(int j = 0; j < messages.size(); j++)
				{
					JSONObject messageObject = (JSONObject) messages.get(j);
					Message message = new Message(messageObject.get("from").toString(), messageObject.get("text").toString(), messageObject.get("dateSent").toString(), messageObject.get("messageSource").toString());
					messageList.add(message);
				}
				tellsMap.put(name, messageList);
			}
		}
		catch (ParseException e)
		{
			Logger.error("Module: Tell", "The tells database could not be read.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void writeMessages()
	{
		JSONArray recepients = new JSONArray();
		for(String recepient : tellsMap.keySet())
		{
			JSONArray messages = new JSONArray();
			JSONObject recepientObject = new JSONObject();
			
			for(Message message : tellsMap.get(recepient))
			{
				JSONObject messageObject = new JSONObject();
				messageObject.put("from", message.from);
				messageObject.put("text", message.text);
				messageObject.put("dateSent", message.dateSent);
				messageObject.put("messageSource", message.messageSource);
				
				messages.add(messageObject);
			}
			
			recepientObject.put("name", recepient);
			recepientObject.put("messages", messages);
			recepients.add(recepientObject);
		}
		FileUtils.writeFile(databasePath, recepients.toJSONString());
	}
	
	private String fixRegex(String regex)
	{
		return "^" + StringUtils.escapeRegex(regex).
				replaceAll("\\*", ".*").
				replaceAll("\\?", ".").
				replaceAll("\\(", "(").
                replaceAll("\\)", ")").
                replaceAll(",", "|").
                replaceAll("/", "|") + "$";
	}
}
