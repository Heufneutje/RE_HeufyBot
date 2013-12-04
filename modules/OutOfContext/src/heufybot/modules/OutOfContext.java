package heufybot.modules;

import heufybot.core.HeufyBot;
import heufybot.utils.FileUtils;
import heufybot.utils.PastebinUtils;
import heufybot.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class OutOfContext extends Module
{
	private String dataPath = "data/ooclog.txt";
	
	public OutOfContext()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(ooc)($| .*)";
	}

	public void processEvent(final String source, String message, String triggerUser, List<String> params)
	{
		final HeufyBot bot = this.bot;
		if(params.size() == 1)
		{
			Thread thread = new Thread(new Runnable()
			{
				public void run()
				{
					String data = FileUtils.readFile(dataPath);
					if(data.equals(""))
					{
						bot.getIRC().cmdPRIVMSG(source, "No quotes to be posted.");
					}
					else
					{
						String result = PastebinUtils.post(data, "HeufyBot OutOfContext Log", "10M");
						if(result == null)
						{
							bot.getIRC().cmdPRIVMSG(source, "Error: OoC Log could not be posted.");
						}
						else if(result.startsWith("http://pastebin.com/"))
						{
							bot.getIRC().cmdPRIVMSG(source, "OoC Log posted: " + result  + " (Link expires in 10 minutes).");
						}
						else
						{
							bot.getIRC().cmdPRIVMSG(source, "Error: " + result + ".");
						}
					}
				}
			});
			thread.start();
		}
		else
		{
			params.remove(0);
			String subCommand = params.remove(0).toLowerCase();
			if(subCommand.equals("add"))
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
			    
				    if(bot.getIRC().getServerInfo().getReverseUserPrefixes().containsKey(toQuote.substring(0, 1)))
				    {
				    	newQuote = newQuote.replace(toQuote, toQuote.substring(1));
				    }
				    
				    FileUtils.writeFileAppend(dataPath, newQuote + "\n");
					bot.getIRC().cmdPRIVMSG(source, "Quote \"" + newQuote + "\" was added to the log!");
			    }
			    else
			    {
			    	bot.getIRC().cmdPRIVMSG(source, "No nickname was found in this quote.");
			    }
			}
			else if(subCommand.equals("searchnick"))
			{
				if(params.size() == 0)
				{
					bot.getIRC().cmdPRIVMSG(source, search(triggerUser, false));
				}
				else
				{
					bot.getIRC().cmdPRIVMSG(source, search(params.get(0), false));
				}
			}
			else if(subCommand.equals("search"))
			{
				bot.getIRC().cmdPRIVMSG(source, search(StringUtils.join(params, " "), true));
			}
			else if(subCommand.equals("random"))
			{
				bot.getIRC().cmdPRIVMSG(source, search(".*", true));
			}
			else if(subCommand.equals("remove"))
			{
				String search = StringUtils.join(params, " ");
				
				String quoteFile = FileUtils.readFile(dataPath);
				String[] quotes = quoteFile.split("\n");
				ArrayList<String> quoteList = new ArrayList<String>();
				ArrayList<String> matches = new ArrayList<String>();
				Pattern pattern = Pattern.compile(".*" + search + ".*", Pattern.CASE_INSENSITIVE);
				
				if(quotes[0].length() < 21)
				{
					bot.getIRC().cmdPRIVMSG(source, "No quotes in the log.");
				}
				else
				{
					for(int i = 0; i < quotes.length; i++)
					{
						quoteList.add(quotes[i]);
						if(pattern.matcher(quotes[i].substring(21)).matches())
						{
							matches.add(quotes[i]);
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
						for(Iterator<String> iter = quoteList.iterator(); iter.hasNext();)
			  	  		{
			  	  			String quote = iter.next();
			  	  			if(quote.equalsIgnoreCase(matches.get(0)))
			  	  			{
			  	  				iter.remove();
			  	  			}
			  	  		}
						FileUtils.deleteFile(dataPath);
		  	  			FileUtils.touchFile(dataPath);
			  	  		for(String quote : quoteList)
			  	  		{
			  	  			FileUtils.writeFileAppend(dataPath, quote + "\n");
			  	  		}
			  	  		bot.getIRC().cmdPRIVMSG(source, "[OutOfContext] Quote '" + matches.get(0) + "' was removed from the log!");
					}
				}
			}
			else
			{
				bot.getIRC().cmdPRIVMSG(source, "[OutOfContext] Invalid operation. Please try again.");
			}
		}
	}
	
	private String search(String searchString, boolean searchInQuotes)
	{
		String quoteFile = FileUtils.readFile(dataPath);
		String[] quotes = quoteFile.split("\n");
		ArrayList<String> matches = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile(".*" + searchString + ".*", Pattern.CASE_INSENSITIVE);
		
		if(quotes[0].length() < 21)
		{
			return "No quotes in the log.";
		}
		else
		{
			if(searchInQuotes) //Search for a word or words in the quotes themselves
			{
				for(int i = 0; i < quotes.length; i++)
				{
					if(quotes[i].indexOf("<") == 21)
					{
						if(pattern.matcher(quotes[i].substring(quotes[i].indexOf(">") + 1)).matches())
						{
							matches.add(quotes[i]);
						}
					}
					else
					{
						if(pattern.matcher(quotes[i].substring(21)).matches())
						{
							matches.add(quotes[i]);
						}
					}
				}
			}
			else //search for nicknames
			{
				for(int i = 0; i < quotes.length; i++)
				{
					if(quotes[i].substring(21).matches("^<.*>.*"))
					{
						if(pattern.matcher(quotes[i].substring(quotes[i].indexOf("<") + 1, quotes[i].indexOf(">"))).matches())
						{
							matches.add(quotes[i]);
						}
					}
					else if(quotes[i].substring(21).matches("^\\* .*"))
					{
						if(pattern.matcher(quotes[i].substring(quotes[i].indexOf("* ") + 2).split(" ")[0]).matches())
						{
							matches.add(quotes[i]);
						}
					}
				}
			}
			
			if(matches.size() == 0)
			{
				return "No matches for \"" + searchString + "\" found.";
			}
			else
			{
				int quoteID = (int) (Math.random() * matches.size());
				
				
				return "Quote #" + (quoteID + 1) + "/" + matches.size() + " - " + matches.get(quoteID);
			}
		}
	}
	
	public String getHelp()
	{
		return "Commands: " + commandPrefix + "ooc, " + commandPrefix + "ooc add <quote>, " + commandPrefix + "ooc search <quote>, " + commandPrefix + "ooc searchnick <nick>, " + commandPrefix + "ooc random, " + commandPrefix + "ooc remove <quote> | Grab the OoC log, add an entry to it, search the log by providing a nickname or sentence or remove an entry.";
	}

	public void onLoad()
	{
		FileUtils.touchFile(dataPath);
	}

	public void onUnload() 
	{
	}
}
