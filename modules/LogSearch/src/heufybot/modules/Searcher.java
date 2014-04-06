package heufybot.modules;

import heufybot.utils.FileUtils;

import java.io.File;
import java.util.Arrays;
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
}
