package heufybot.modules;

import com.memetix.mst.MicrosoftTranslatorAPI;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import heufybot.utils.FileUtils;
import heufybot.utils.StringUtils;

import java.util.List;

public class TranslationParty extends Module
{
    private String textToTranslate;

    public TranslationParty(String server)
    {
        super(server);

        this.authType = AuthType.Anyone;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(tp)($| .*)";
    }

    @Override
    public String getHelp(String message)
    {
        return "Commands: " + this.commandPrefix
                + "tp <sentence> | Follow translationparty.com to get a new sentence";
    }

    @Override
    public void processEvent(final String source, String metadata, String triggerUser,
                             List<String> params)
    {
        if (params.size() == 1)
        {
            this.bot.getServer(this.server).cmdPRIVMSG(source, "Translate what?");
        }
        else
        {
            params.remove(0);
            this.textToTranslate = StringUtils.join(params, " ");
            if (this.textToTranslate.length() > 99)
            {
                this.textToTranslate = this.textToTranslate.substring(0, 99);
            }

            String[] authCredentials = FileUtils.readFile("data/msazurekey.txt").split("\n");
            try
            {
                MicrosoftTranslatorAPI.setClientId(authCredentials[0]);
                MicrosoftTranslatorAPI.setClientSecret(authCredentials[1]);
            }
            catch (IndexOutOfBoundsException e)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "Error: No MS Azure login credentials were provided.");
                return;
            }
            try
            {
                String newText = this.textToTranslate;
                String lastEnglishEntry = this.textToTranslate;
                for (int tries = 0; tries < 21; tries++)
                {
                    if (newText.length() > 99)
                    {
                        newText = newText.substring(0, 99);
                    }
                    newText = Translate.execute(newText, Language.ENGLISH, Language.JAPANESE);
                    if (newText.length() > 99)
                    {
                        newText = newText.substring(0, 99);
                    }
                    newText = Translate.execute(newText, Language.JAPANESE, Language.ENGLISH);
                    if (newText.equals(lastEnglishEntry))
                    {
                        this.bot.getServer(this.server).cmdPRIVMSG(source,
                                newText + " | Steps: " + tries);
                        return;
                    }
                    else
                    {
                        lastEnglishEntry = newText;
                    }
                }
                this.bot.getServer(this.server).cmdPRIVMSG(source, newText + " | Steps: 20+");
            }
            catch (Exception e)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source,
                        "Error: Text could not be translated.");
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
