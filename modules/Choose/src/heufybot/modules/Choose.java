package heufybot.modules;

import heufybot.utils.StringUtils;

import java.util.List;
import java.util.Random;

public class Choose extends Module
{
	public Choose()
	{
		this.authType = Module.AuthType.Anyone;
		this.apiVersion = "0.5.0";
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(choose|choice)($| .*)";
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
			String[] choices = new String[0];
			
			if(message.contains(","))
			{
				choices = StringUtils.parseStringtoList(StringUtils.join(params, " "), ",").toArray(choices);
			}
			else if(message.contains(" "))
			{
				choices = params.toArray(choices);
			}
			else
			{
				choices = new String[] { params.get(0) };
			}

			for(int i = 0; i < choices.length; i++)
			{
				choices[i] = choices[i].trim();
			}
			
			Random random = new Random();
			int choiceNumber = random.nextInt(choices.length);
			bot.getIRC().cmdPRIVMSG(source, "Choice: " + choices[choiceNumber]);
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
