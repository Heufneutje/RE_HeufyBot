package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Choose extends Module
{
	public Choose()
	{
		this.authType = Module.AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(choose)($| .*)";
	}
	
	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if (params.size() == 1)
		{
			bot.getIRC().cmdPRIVMSG(source, "Choose what?");
		}
		else
		{
			params.remove(0);
			List<String> choices;
			
			if(message.contains(","))
			{
				choices = StringUtils.parseStringtoList(StringUtils.join(params, " "), ",");
			}
			else if(message.contains(" "))
			{
				choices = params;
			}
			else
			{
				choices = new ArrayList<String>();
				choices.add(params.get(0));
			}
			
			int choiceNumber = (int) (Math.random() * choices.size());
			bot.getIRC().cmdPRIVMSG(source, "Choice: " + choices.get(choiceNumber));
		}
	}

	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "choose <choice1>, <choice2> | Makes the bot choose one of the given options at random.";
	}

	@Override
	public void onLoad() 
	{
	}

	@Override
	public void onUnload()
	{
	}
}
