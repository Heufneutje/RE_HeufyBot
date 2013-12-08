package heufybot.modules;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import heufybot.core.Logger;
import heufybot.utils.FileUtils;
import heufybot.utils.PastebinUtils;
import heufybot.utils.RegexUtils;
import heufybot.utils.StringUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	private HashMap<String, ArrayList<Message>> tellsMap;
	private String settingsPath = "data/tells.xml";
	
	public Tell()
	{
		this.authType = AuthType.Anyone;
		this.trigger = ".*";
		
		this.tellsMap = new HashMap<String, ArrayList<Message>>();
	}

	@Override
	public String getHelp()
	{
		return "Commands: " + commandPrefix + "tell <user> <message>, " + commandPrefix + "rtell <message>, " + commandPrefix + "senttells | Tells the specified user a message the next time they speak, removes a message sent by you from the database or lists your pending messages.";
	}

	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params)
	{
		if(message.matches("^" + commandPrefix + "tell.*"))
		{
			if(params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "Tell what?");
			}
			else
			{
				params.remove(0);
				if(params.size() == 1)
				{
					bot.getIRC().cmdPRIVMSG(source, "[Tell] What do you want me to tell them?");
				}
				else
				{
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
						}
						else if(recepients[i].equalsIgnoreCase(bot.getIRC().getNickname()))
						{
							bot.getIRC().cmdPRIVMSG(source, "Thanks for telling me that " + triggerUser + ".");
						}
						else
						{
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
							bot.getIRC().cmdPRIVMSG(source, "Okay, I'll tell " + recepients[i] + " that next time they speak.");
						}
					}
					writeMessages();
				}
			}
		}
		else if(message.matches("^" + commandPrefix + "rtell.*"))
		{
			if(params.size() == 1)
			{
				bot.getIRC().cmdPRIVMSG(source, "Remove what?");
			}
			else
			{
				for(Iterator<String> iter = tellsMap.keySet().iterator(); iter.hasNext();)
				{
					String user = iter.next();
					for(Iterator<Message> iter2 = tellsMap.get(user).iterator(); iter2.hasNext();)
					{
						Message messageToRemove = iter2.next();
						boolean messageFound = false;
						if(messageToRemove.from.equalsIgnoreCase(triggerUser) && messageToRemove.text.matches(".*" + params.get(1) + ".*"))
						{
							bot.getIRC().cmdPRIVMSG(source, "[Tell] Message '" + messageToRemove.text + "' sent to " + user + " on " + messageToRemove.dateSent + " was removed from the message database!");
							iter2.remove();
							messageFound = true;
						}
						if(messageFound)
							break;
					}
					if(tellsMap.get(user).size() == 0)
					{
						iter.remove();
					}
				}
			}
		}
		if(message.matches("^" + commandPrefix + "senttells.*"))
		{
			if(params.size() == 1)
			{
				String foundTells = "";
				String foundPMs = "";
				for(String user : tellsMap.keySet())
				{
					for(Message message2 : tellsMap.get(user))
					{
						if(message2.from.equalsIgnoreCase(triggerUser))
						{
							if(message2.messageSource.equals("PM"))
							{
								foundPMs += message2.text + " < Sent to " + user + " on " + message2.dateSent + "\n";
							}
							else
							{
								foundTells += message2.text + " < Sent to " + user + " on " + message2.dateSent + "\n";
							}
						}
					}
				}
				if(foundTells.equals("") && foundPMs.equals(""))
				{
					bot.getIRC().cmdPRIVMSG(source, "There are no messages sent by you that have not been received yet.");
				}
				else
				{
					if(!foundTells.equals(""))
					{
						String[] tells = foundTells.split("\n");
						if(tells.length > 3)
						{
							String result = PastebinUtils.post(foundTells, triggerUser + "'s Messages", "10M");
							if(result == null)
							{
								bot.getIRC().cmdPRIVMSG(source, "Error: Messages could not be posted.");
							}
							else if(result.startsWith("http://pastebin.com/"))
							{
								bot.getIRC().cmdPRIVMSG(source, "These messages sent by you have not yet been received: " + result  + " (Link expires in 10 minutes)");
							}
							else
							{
								bot.getIRC().cmdPRIVMSG(source, "Error: " + result);
							}
						}
						else
						{
							for(int i = 0; i < tells.length; i++)
							{
								bot.getIRC().cmdPRIVMSG(source, tells[i]);
							}
						}
					}
					if(!foundPMs.equals(""))
					{
						if(!source.equals(triggerUser) && foundTells.equals(""))
						{
							bot.getIRC().cmdPRIVMSG(source, "There are no public messages sent by you that have not been received yet, but there are private messages.");
						}
						String[] tells = foundPMs.split("\n");
						for(int i = 0; i < tells.length; i++)
						{
							bot.getIRC().cmdPRIVMSG(triggerUser, tells[i]);
						}
					}
				}
			}
		}
		for(Iterator<String> iter = tellsMap.keySet().iterator(); iter.hasNext();)
		{
			String user = iter.next();
			if(triggerUser.toLowerCase().matches(user.toLowerCase()))
			{
				String tells = "";
				String pms = "";					
				
				for(Message message2 : tellsMap.get(user))
				{
					if(message2.messageSource.equals("PM"))
					{
						pms += message2.text + " < From " + message2.from + " on " + message2.dateSent + "\n";
					}
					else
					{
						tells += triggerUser + ": " + message2.text + " < From " + message2.from + " on " + message2.dateSent + "\n";
					}
				}
				if(!tells.equals(""))
				{
					String[] receivedTells = tells.split("\n");
					if(receivedTells.length > 3)
					{
						String result = PastebinUtils.post(tells, triggerUser + "'s Messages", "1H");
						if(result == null)
						{
							bot.getIRC().cmdPRIVMSG(source, "Error: Messages could not be posted.");
						}
						else if(result.startsWith("http://pastebin.com/"))
						{
							bot.getIRC().cmdPRIVMSG(source, triggerUser + ", you have " + receivedTells.length + " messages. Go here to read them: " + result  + " (Link expires in 60 minutes)");
						}
						else
						{
							bot.getIRC().cmdPRIVMSG(source, "Error: " + result);
						}
					}
					else
					{
						for(int i = 0; i < receivedTells.length; i++)
						{
							bot.getIRC().cmdPRIVMSG(source, receivedTells[i]);
						}
					}
				}
				if(!pms.equals(""))
				{
					String[] receivedPMs = pms.split("\n");
					for(int i = 0; i < receivedPMs.length; i++)
					{
						bot.getIRC().cmdPRIVMSG(triggerUser, receivedPMs[i]);
					}
				}
				iter.remove();
				writeMessages();
			}
		}
	}

	@Override
	public void onLoad()
	{
		if(FileUtils.touchFile(settingsPath))
		{
			try
			{
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				
				Element root = doc.createElement("recepients");
				doc.appendChild(root);
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
			    Transformer transformer = transformerFactory.newTransformer();
			    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			    DOMSource source = new DOMSource(doc);
			    StreamResult result = new StreamResult(new File(settingsPath));
	
			    transformer.transform(source, result);
			}
			catch (Exception e)
			{
				Logger.error("Module: Tell", "Message database could not be written.");
			}
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
			File fXmlFile = new File(settingsPath);
	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
	
			NodeList n = doc.getElementsByTagName("recepient");
	        for(int i = 0; i < n.getLength(); i++)
	        {
	        	String name = "";
	        	ArrayList<Message> messages = new ArrayList<Message>();
	        	
	        	if (n.item(i).getNodeType() == 1)
        		{
	        		Element eElement = (Element)n.item(i);
	        		name = getTagValue("name", eElement);
        		}
	        	NodeList list1 = n.item(i).getChildNodes();
	        	for(int j = 0; j < list1.getLength(); j++)
	        	{     		
	        		if(list1.item(j).getNodeName().equals("messages"))
	        		{
	        			NodeList list2 = list1.item(j).getChildNodes();
	        			for(int k = 0; k < list2.getLength(); k++)
	        			{
	        				if(list2.item(k).getNodeName().equals("message"))
	        				{
	        					Node nNode = list2.item(k);
        				        if (nNode.getNodeType() != 1)
        				          continue;
        				        Element eElement2 = (Element)nNode;
        				        Message message = new Message(getTagValue("from", eElement2), getTagValue("text", eElement2), getTagValue("datesent", eElement2), getTagValue("messagesource", eElement2));
        				        messages.add(message);
	        				}
	        			}
	        		}
	        	}
		        tellsMap.put(name, messages);
			}
		}
		catch(Exception e)
		{
			Logger.error("Module: Tell", "Message database could not be read.");
		}
	}
	
	public void writeMessages()
	{
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element root = doc.createElement("recepients");
			doc.appendChild(root);
			
			for(String user : tellsMap.keySet())
			{
				Element userNode = doc.createElement("recepient");
				root.appendChild(userNode);
				Element name = doc.createElement("name");
				userNode.appendChild(name);
				name.appendChild(doc.createTextNode(user));
				Element messages = doc.createElement("messages");
				userNode.appendChild(messages);
				
				for(Message message : tellsMap.get(user))
				{
					Element messageElement = doc.createElement("message");
					messages.appendChild(messageElement);
					
					Element fromElement = doc.createElement("from");
					messageElement.appendChild(fromElement);
					fromElement.appendChild(doc.createTextNode(message.from));
					
					Element textElement = doc.createElement("text");
					messageElement.appendChild(textElement);
					textElement.appendChild(doc.createTextNode(message.text));
					
					Element dateElement = doc.createElement("datesent");
					messageElement.appendChild(dateElement);
					dateElement.appendChild(doc.createTextNode(message.dateSent));
					
					Element sourceElement = doc.createElement("messagesource");
					messageElement.appendChild(sourceElement);
					sourceElement.appendChild(doc.createTextNode(message.messageSource));
				}
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    DOMSource source = new DOMSource(doc);
		    StreamResult result = new StreamResult(new File(settingsPath));

		    transformer.transform(source, result);
		}
		catch (Exception e)
		{
			Logger.error("Module: Tell", "Message database could not be written.");
		}
	}
	
	private String getTagValue(String sTag, Element eElement)
	{
	    try
	    {
	    	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    	Node nValue = nlList.item(0);
	    	return nValue.getNodeValue();
	    }
	    catch (NullPointerException e) 
	    {
	    	return "";
	    }
	}
	
	private String fixRegex(String regex)
	{
		return "^" + RegexUtils.escapeRegex(regex).
				replaceAll("\\*", ".*").
				replaceAll("\\?", ".").
				replaceAll("\\(", "(").
                replaceAll("\\)", ")").
                replaceAll(",", "|").
                replaceAll("/", "|") + "$";
	}
}
