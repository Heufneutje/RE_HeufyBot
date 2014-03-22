package heufybot.modules;

import heufybot.core.HeufyBot;
import heufybot.utils.FileUtils;
import heufybot.utils.PasteUtils;
import heufybot.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class OutOfContext extends Module
{
	private String dataPath = "data/ooclog.txt";
	private List<String> quoteLog;
	
	public OutOfContext()
	{
		this.authType = AuthType.Anyone;
		this.apiVersion = "0.5.0";
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(ooc)($| .*)";
	}

	public void processEvent(final String source, String message, String triggerUser, List<String> params)
	{
		final HeufyBot bot = this.bot;
		if(params.size() == 1)
		{
			String data = FileUtils.readFile(dataPath);
			if(data.equalsIgnoreCase(""))
			{
				bot.getIRC().cmdPRIVMSG(source, "No quotes to be posted.");
			}
			else
			{
				String result = PasteUtils.post(data, "HeufyBot OutOfContext Log", "hour");
				if(result == null)
				{
					bot.getIRC().cmdPRIVMSG(source, "Error: OoC Log could not be posted.");
				}
				else
				{
					bot.getIRC().cmdPRIVMSG(source, "OoC Log posted: " + result  + " (Link expires in 60 minutes).");
				}
			}
		}
		else
		{
			params.remove(0);
			String subCommand = params.remove(0).toLowerCase();
			if(subCommand.equalsIgnoreCase("add"))
			{
				String newQuote = StringUtils.removeFormattingAndColors(StringUtils.join(params, " "));
				
				DateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd] [HH:mm]");
			    Date date = new Date();
			    String dateString = dateFormat.format(date);
			    
			    String toQuote = "";
			    
			    if(newQuote.matches("^<.*>.*") || newQuote.matches("^\\* .*") || newQuote.matches("^\\[.*\\] <.*>.*") || newQuote.matches("^\\[.*\\] \\* .*"))
			    {
				    if(newQuote.matches("^\\[.*\\] <.*>.*") || newQuote.matches("^\\[.*\\] \\* .*"))
				    {
				    	if(newQuote.matches("^\\[.*\\] \\* .*"))
				    	{
				    		toQuote = newQuote.substring(newQuote.indexOf("* ") + 2).split(" ")[0];
				    		newQuote = dateString + " " + newQuote.substring(newQuote.indexOf("*"));
				    	}
				    	else
				    	{
				    		toQuote = newQuote.substring(newQuote.indexOf("<") + 1, newQuote.indexOf(">"));
				    		newQuote = dateString + " " + newQuote.substring(newQuote.indexOf("<"));
				    	}
				    	
				    }
				    else if(newQuote.matches("^<.*>.*") || newQuote.matches("^\\* .*"))
				    {
				    	if(newQuote.matches("^\\* .*"))
				    	{
				    		toQuote = newQuote.substring(newQuote.indexOf("* ") + 2).split(" ")[0];
				    	}
				    	else
				    	{
				    		toQuote = newQuote.substring(newQuote.indexOf("<") + 1, newQuote.indexOf(">"));
				    	}
				    	newQuote = dateString + " " + newQuote;
				    }
			    
				    if(bot.getIRC().getServerInfo().getReverseUserPrefixes().containsKey(toQuote.substring(0, 1)) || toQuote.substring(0, 1).equalsIgnoreCase(" "))
				    {
				    	newQuote = newQuote.replace(toQuote, toQuote.substring(1));
				    }
				    
				    for(String quote : quoteLog)
				    {
				    	if(quote.substring(21).equalsIgnoreCase(newQuote.substring(21)))
				    	{
				    		bot.getIRC().cmdPRIVMSG(source, "This quote is already in the log.");
				    		return;
				    	}
				    }
				    quoteLog.add(newQuote);
				    FileUtils.writeFileAppend(dataPath, newQuote + "\n");
				    bot.getIRC().cmdPRIVMSG(source, "Quote \"" + newQuote + "\" was added to the log!");
			    }
			    else
			    {
			    	bot.getIRC().cmdPRIVMSG(source, "No nickname was found in this quote.");
			    }
			}
			else if(subCommand.equalsIgnoreCase("searchnick"))
			{
				if(params.size() == 0)
				{
					bot.getIRC().cmdPRIVMSG(source, search(triggerUser, false, -1));
				}
				else if(params.size() > 1)
				{
					bot.getIRC().cmdPRIVMSG(source, search(params.get(0), false, StringUtils.tryParseInt(params.get(1))));
				}
				else
				{
					bot.getIRC().cmdPRIVMSG(source, search(params.get(0), false, -1));
				}
			}
			else if(subCommand.equalsIgnoreCase("search"))
			{
				bot.getIRC().cmdPRIVMSG(source, search(StringUtils.join(params, " "), true, -1));
			}
			else if(subCommand.equalsIgnoreCase("random"))
			{
				bot.getIRC().cmdPRIVMSG(source, search(".*", true, -1));
			}
			else if(subCommand.equalsIgnoreCase("id"))
			{
				if(params.size() == 0)
				{
					bot.getIRC().cmdPRIVMSG(source, "You didn't give a quote ID.");
				}
				else if(StringUtils.tryParseInt(params.get(0)) == -1)
				{
					bot.getIRC().cmdPRIVMSG(source, "That is not a valid quote ID.");
				}
				else
				{
					bot.getIRC().cmdPRIVMSG(source, search(".*", true, StringUtils.tryParseInt(params.get(0))));
				}
			}
			else if(subCommand.equalsIgnoreCase("remove"))
			{
				String search = StringUtils.join(params, " ");
				ArrayList<String> matches = new ArrayList<String>();
				Pattern pattern = Pattern.compile(".*" + search + ".*", Pattern.CASE_INSENSITIVE);
				
				if(quoteLog.get(0).length() < 21)
				{
					bot.getIRC().cmdPRIVMSG(source, "No quotes in the log.");
				}
				else
				{
					for(String quote : quoteLog)
					{
						if(pattern.matcher(quote.substring(21)).matches())
						{
							matches.add(quote);
						}
					}
					if(matches.size() == 0)
					{
						bot.getIRC().cmdPRIVMSG(source, "No matches for '" + search + "' found.");
					}
					else if(matches.size() > 1)
					{
						bot.getIRC().cmdPRIVMSG(source, "Unable to remove quote, " + matches.size() + " matches were found.");
					}
					else
					{
						for(Iterator<String> iter = quoteLog.iterator(); iter.hasNext();)
			  	  		{
			  	  			String quote = iter.next();
			  	  			if(quote.equalsIgnoreCase(matches.get(0)))
			  	  			{
			  	  				iter.remove();
			  	  			}
			  	  		}
						writeLog();
			  	  		bot.getIRC().cmdPRIVMSG(source, "Quote '" + matches.get(0) + "' was removed from the log!");
					}
				}
			}
			else
			{
				bot.getIRC().cmdPRIVMSG(source, "Invalid subcommand. Subcommands are add/remove/search/searchnick/random/id.");
			}
		}
	}
	
	private String search(String searchString, boolean searchInQuotes, int quoteID)
	{
		ArrayList<String> matches = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile(".*" + searchString + ".*", Pattern.CASE_INSENSITIVE);
		
		if(quoteLog.get(0).length() < 21)
		{
			return "No quotes in the log.";
		}
		else
		{
			if(searchInQuotes) //Search for a word or words in the quotes themselves
			{
				for(String quote : quoteLog)
				{
					if(quote.indexOf("<") == 21)
					{
						if(pattern.matcher(quote.substring(quote.indexOf(">") + 1)).matches())
						{
							matches.add(quote);
						}
					}
					else
					{
						if(pattern.matcher(quote.substring(21)).matches())
						{
							matches.add(quote);
						}
					}
				}
			}
			else //search for nicknames
			{
				for(String quote : quoteLog)
				{
					if(quote.substring(21).matches("^<.*>.*"))
					{
						if(pattern.matcher(quote.substring(quote.indexOf("<") + 1, quote.indexOf(">"))).matches())
						{
							matches.add(quote);
						}
					}
					else if(quote.substring(21).matches("^\\* .*"))
					{
						if(pattern.matcher(quote.substring(quote.indexOf("* ") + 2).split(" ")[0]).matches())
						{
							matches.add(quote);
						}
					}
				}
			}
			
			if(matches.size() == 0)
			{
				return "No matches for \"" + searchString + "\" found.";
			}
			else if(quoteID > matches.size())
			{
				quoteID = -1;
			}
			if(quoteID < 1)
			{
				Random random = new Random();
				quoteID = random.nextInt(matches.size());
			}
			else
			{
				quoteID--;
			}
			return "Quote #" + (quoteID + 1) + "/" + matches.size() + " - " + matches.get(quoteID);
		}
	}
	
	private void writeLog()
	{
		FileUtils.writeFile(dataPath, StringUtils.join(quoteLog, "\n") + "\n");
	}
	
	public String getHelp(String message)
	{
		if(message.matches("ooc add"))
		{
			return "Commands: " + commandPrefix + "ooc add <quote> | Add a quote to the Out of Context log. Format is \"<nick> message\" for normal messages and \"* nick message\" for actions.";
		}
		else if(message.matches("ooc remove"))
		{
			return "Commands: " + commandPrefix + "ooc remove <quote> | Remove a quote from the Out of Context log. Provide words that are in the quote you're trying to remove. Quote will only be removed if there's only one match.";
		}
		else if(message.matches("ooc search"))
		{
			return "Commands: " + commandPrefix + "ooc search <quote> | Search for a quote in the Out of Context log. The results are the ones that have the given words in them";
		}
		else if(message.matches("ooc searchnick"))
		{
			return "Commands: " + commandPrefix + "ooc searchnick <nickname> (<id>) | Search for a quote in the Out of Context log by providing a nickname or part of one. An ID can also be given to get a specific quote.";
		}
		else if(message.matches("ooc random"))
		{
			return "Commands: " + commandPrefix + "ooc random | Returns a random quote from the Out of Context log.";
		}
		else if(message.matches("ooc id"))
		{
			return "Commands: " + commandPrefix + "ooc id <quoteid> | Returns the quote from the Out of Context log that has the given ID.";
		}
		
		return "Commands: " + commandPrefix + "ooc (add/remove/search/searchnick/random/id) | The log of Out of Context quotes! Without a subcommand this will post a link to the log. Type \"" + commandPrefix + "help ooc <subcommand>\" for help on a specific subcommand.";
	}

	public void onLoad()
	{
		FileUtils.touchFile(dataPath);
		quoteLog = StringUtils.parseStringtoList(FileUtils.readFile(dataPath), "\n");
	}

	public void onUnload() 
	{
		writeLog();
	}
}
