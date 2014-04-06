package heufybot.modules;

import heufybot.utils.FileUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher
{
	private String rootLogPath;
	
	public Searcher(String rootLogPath)
	{
		this.rootLogPath = rootLogPath;
	}
	
	public String firstSeen(String source, String searchTerms)
	{
		File[] logsFolder = new File(rootLogPath).listFiles();
		Arrays.sort(logsFolder);

		for(File file : logsFolder)
		{
			int dateStart = file.getPath().lastIndexOf(File.separator) + 1;
			String date = "[" + file.getPath().substring(dateStart, dateStart + 10) + "] ";
			
			String[] lines = FileUtils.readFile(file.getPath()).split("\n");
			Pattern normalPattern = Pattern.compile(".*<(.?" + searchTerms + ")> .*", Pattern.CASE_INSENSITIVE);
			Pattern actionPattern = Pattern.compile(".*\\* (" + searchTerms + ") .*", Pattern.CASE_INSENSITIVE);
			
			for(String line : lines)
			{
				Matcher matcher = normalPattern.matcher(line);
				if(matcher.find())
				{
					return date + line;
				}
				else
				{
					matcher = actionPattern.matcher(line);
					if(matcher.find())
					{
						return date + line;
					}
				}
			}
		}
		return "No user matching \"" + searchTerms + "\" was found in the logs.";
	}
	
	public String lastSeen(String source, String searchTerms, boolean includeToday)
	{
		File[] logsFolder = new File(rootLogPath).listFiles();
		Arrays.sort(logsFolder);
		
		Date today = new Date();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String todayString = formatter.format(today);
		
		for(int i = logsFolder.length - 1; i >= 0; i--)
		{
			File file = logsFolder[i];
			
			int dateStart = file.getPath().lastIndexOf(File.separator) + 1;
			String date = file.getPath().substring(dateStart, dateStart + 10);

			if(includeToday || !date.equals(todayString))
			{
				String[] lines = FileUtils.readFile(file.getPath()).split("\n");
				Pattern normalPattern = Pattern.compile(".*<(.?" + searchTerms + ")> .*", Pattern.CASE_INSENSITIVE);
				Pattern actionPattern = Pattern.compile(".*\\* (" + searchTerms + ") .*", Pattern.CASE_INSENSITIVE);
				
				for(int j = lines.length - 1; j >= 0; j--)
				{
					String line = lines[j];
					Matcher matcher = normalPattern.matcher(line);
					if(matcher.find())
					{
						return "[" + date + "] " + line;
					}
					else
					{
						matcher = actionPattern.matcher(line);
						if(matcher.find())
						{
							return "[" + date + "] " + line;
						}
					}
				}
			}
		}
		return "No user matching \"" + searchTerms + "\" was found in the logs.";
	}
	
	public String firstSaid(String source, String searchTerms)
	{
		File[] logsFolder = new File(rootLogPath).listFiles();
		Arrays.sort(logsFolder);
		
		for(File file : logsFolder)
		{
			int dateStart = file.getPath().lastIndexOf(File.separator) + 1;
			String date = "[" + file.getPath().substring(dateStart, dateStart + 10) + "] ";
			
			String[] lines = FileUtils.readFile(file.getPath()).split("\n");
			Pattern normalPattern = Pattern.compile(".*<.*> .*(" + searchTerms + ").*", Pattern.CASE_INSENSITIVE);
			
			for(String line : lines)
			{
				Matcher matcher = normalPattern.matcher(line);
				if(matcher.find())
				{
					return date + line;
				}
			}
		}
		return "No message matching \"" + searchTerms + "\" was found in the logs.";
	}
	
	public String lastSaid(String source, String searchTerms)
	{
		File[] logsFolder = new File(rootLogPath).listFiles();
		Arrays.sort(logsFolder);

		Date today = new Date();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String todayString = formatter.format(today);
		
		for(int i = logsFolder.length - 1; i >= 0; i--)
		{
			File file = logsFolder[i];
			
			int dateStart = file.getPath().lastIndexOf(File.separator) + 1;
			String date = file.getPath().substring(dateStart, dateStart + 10);

			String[] lines = FileUtils.readFile(file.getPath()).split("\n");
			Pattern normalPattern = Pattern.compile(".*<.*> .*(" + searchTerms + ").*", Pattern.CASE_INSENSITIVE);
			
			int startLine = lines.length;
			if(date.equals(todayString) && lines.length > 5)
			{
				startLine = startLine - 5;
			}
			
			for(int j = startLine - 1; j >= 0; j--)
			{
				String line = lines[j];
				Matcher matcher = normalPattern.matcher(line);
				if(matcher.find())
				{
					return "[" + date + "] " + line;
				}
			}
		}
		return "No message matching \"" + searchTerms + "\" was found in the logs.";
	}
}
