package heufybot.modules;

import heufybot.utils.FileUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher
{
	private String rootLogPath;
	
	public Searcher(String rootLogPath)
	{
		this.rootLogPath = rootLogPath;
	}
	
	public String firstSeen(String source, String searchTerms, boolean includeToday)
	{
		File[] logsFolder = new File(rootLogPath + "/" + source + "/").listFiles();
		for(int i = 0; i < logsFolder.length; i++)
		{
			File file = logsFolder[i];
			
			int dateStart = file.getPath().lastIndexOf(File.separator) + 1;
			String date = "[" + file.getPath().substring(dateStart, dateStart + 10) + "] ";
			
			String[] lines = FileUtils.readFile(file.getPath()).split("\n");
			Pattern normalPattern = Pattern.compile(".*<(.?" + searchTerms + ")> .*", Pattern.CASE_INSENSITIVE);
			Pattern actionPattern = Pattern.compile(".*\\* (" + searchTerms + ") .*", Pattern.CASE_INSENSITIVE);
			
			for(int j = 0; j < lines.length; j++)
			{
				Matcher matcher = normalPattern.matcher(lines[j]);
				if(matcher.find())
				{
					return date + lines[j];
				}
				else
				{
					matcher = actionPattern.matcher(lines[j]);
					if(matcher.find())
					{
						return date + lines[j];
					}
				}
			}
		}
		return "No user matching \"" + searchTerms + "\" was found in the logs.";
	}
}
