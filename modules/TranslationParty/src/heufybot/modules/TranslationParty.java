package heufybot.modules;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.util.List;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class TranslationParty extends Module
{
	private String textToTranslate;

	public TranslationParty() 
	{
		this.authType = AuthType.Anyone;
		this.apiVersion = "0.5.0";
		this.triggerTypes = new TriggerType[] { TriggerType.Message };
		this.trigger = "^" + commandPrefix + "(tp)($| .*)";
	}

	@Override
	public String getHelp(String message)
	{
		return "Commands: " + commandPrefix + "tp <sentence> | Follow translationparty.com to get a new sentence";
	}

	@Override
	public void processEvent(final String source, String metadata, String triggerUser, List<String> params) 
	{
		if(params.size() == 1)
		{
			bot.getIRC().cmdPRIVMSG(source, "Translate what?");
		}
		else
		{
			params.remove(0);
			textToTranslate  = StringUtils.join(params, " ");
			if(textToTranslate.length() > 99)
			{
				textToTranslate = textToTranslate.substring(0, 99);
			}
			
			String[] authCredentials = FileUtils.readFile("data/msazurekey.txt").split("\n");
			try
			{
				Translate.setClientId(authCredentials[0]);
				Translate.setClientSecret(authCredentials[1]);
			}
			catch(IndexOutOfBoundsException e)
			{
				bot.getIRC().cmdPRIVMSG(source, "Error: No MS Azure login credentials were provided.");
				return;
			}
			try
			{
				String newText = textToTranslate;
				String lastEnglishEntry = textToTranslate;
				for(int tries = 0; tries < 21; tries++)
				{
					if(newText.length() > 99)
					{
						newText = newText.substring(0, 99);
					}
					newText = Translate.execute(newText, Language.ENGLISH, Language.JAPANESE);
					if(newText.length() > 99)
					{
						newText = newText.substring(0, 99);
					}
					newText = Translate.execute(newText, Language.JAPANESE, Language.ENGLISH);
					if(newText.equals(lastEnglishEntry))
					{
						bot.getIRC().cmdPRIVMSG(source, newText + " | Steps: " + tries);
						return;
					}
					else
					{
						lastEnglishEntry = newText;
					}
				}
				bot.getIRC().cmdPRIVMSG(source, newText + " | Steps: 20+");
			}
			catch (Exception e)
			{
				bot.getIRC().cmdPRIVMSG(source, "Error: Text could not be translated.");
			}
		} 
	}

	@Override
	public void onLoad()
	{
		FileUtils.touchFile("data/msazurekey.txt");
	}

	@Override
	public void onUnload()
	{
	}
}