package heufybot.modules;

import java.util.List;

import com.memetix.mst.language.Language;
import com.memetix.mst.detect.Detect;

import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

public class Translate extends Module
{
	public Translate() 
	{
		this.authType = AuthType.Anyone;
		this.trigger = "^" + commandPrefix + "(translate)($| .*)";
	}

	@Override
	public String getHelp()
	{
		return "Commands: " + commandPrefix + "translate (<fromlanguage/)<tolanguage> <sentence> | Translates a sentence to a different language through Bing Translate. Language codes: http://msdn.microsoft.com/en-us/library/hh456380.aspx";
	}

	@Override
	public void processEvent(String source, String message, String triggerUser, List<String> params) 
	{
		if(params.size() == 1)
		{
			this.bot.getIRC().cmdPRIVMSG(source, "Translate what?");
		}
		else
		{
			params.remove(0);
			String languageParam = "";
			String textToTranslate = "";
			try
			{
				languageParam = params.remove(0).toLowerCase();
				textToTranslate  = StringUtils.join(params, " ");
			}
			catch (IndexOutOfBoundsException e)
			{
				this.bot.getIRC().cmdPRIVMSG(source, "No text to translate.");
				return;
			}
			
			if(textToTranslate.length() > 149)
			{
				textToTranslate = textToTranslate.substring(0, 147) + "...";
			}
			
			String[] authCredentials = FileUtils.readFile("data/msazurekey.txt").split("\n");
			
			if(authCredentials.length > 1)
			{
				com.memetix.mst.translate.Translate.setClientId(authCredentials[0]);
				com.memetix.mst.translate.Translate.setClientSecret(authCredentials[1]);
				
				Detect.setClientId(authCredentials[0]);
				Detect.setClientSecret(authCredentials[1]);
			}
			else
			{
				this.bot.getIRC().cmdPRIVMSG(source, "Error: No MS Azure login credentials were provided.");
				return;
			}
			
			try
			{
				if(languageParam.contains("/") && languageParam.length() == 5)
				{
					String fromLanguage = languageParam.substring(0, 2);
					String toLanguage = languageParam.substring(3, 5);
					System.out.println(fromLanguage + "\n" + toLanguage);
					String translatedText = com.memetix.mst.translate.Translate.execute(textToTranslate, Language.fromString(fromLanguage), Language.fromString(toLanguage));
					bot.getIRC().cmdPRIVMSG(source, translatedText + " | Source Language: " + fromLanguage);
				}
				else
				{
					Language sourceLanguage = Detect.execute(textToTranslate);
					String translatedText = com.memetix.mst.translate.Translate.execute(textToTranslate, Language.fromString(languageParam));
					bot.getIRC().cmdPRIVMSG(source, translatedText +  " | Source Language: Auto-Detect (" + sourceLanguage.toString() + ")");
				}			
			} 
			catch (Exception e)
			{
				bot.getIRC().cmdPRIVMSG(source, "Text could not be translated. Make sure the language code is corrent.");
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
