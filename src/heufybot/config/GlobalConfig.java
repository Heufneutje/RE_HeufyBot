package heufybot.config;

import heufybot.core.Logger;
import heufybot.core.cap.CapHandler;
import heufybot.core.cap.EnablingCapHandler;
import heufybot.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

public class GlobalConfig
{
    public enum PasswordType
    {
        None, ServerPass, NickServPass, SASL;
    }

    protected List<CapHandler> capHandlers;
    protected HashMap<String, Object> settings;

    public GlobalConfig()
    {
        this.capHandlers = new ArrayList<CapHandler>();
        this.capHandlers.add(new EnablingCapHandler("multi-prefix"));
    }

    @SuppressWarnings("unchecked")
    public void loadGlobalConfig(String fileName)
    {
        Yaml yaml = new Yaml();
        if (FileUtils.fileExists(fileName))
        {
            String settingsYaml = FileUtils.readFile(fileName);
            this.settings = (HashMap<String, Object>) yaml.load(settingsYaml);
        }
        else
        {
            Logger.error("Config", "Config file \"" + fileName
                    + "\" could not be found. Generating default config...");

            HashMap<String, Object> settings = new LinkedHashMap<String, Object>();

            settings.put("nickname", "RE_HeufyBot");
            settings.put("username", "RE_HeufyBot");
            settings.put("realname", "RE_HeufyBot IRC Bot");
            settings.put("server", "irc.foo.bar");
            settings.put("port", 6667);
            settings.put("ssl", false);
            settings.put("password", "");
            settings.put("passwordType", "None");
            settings.put("autoJoin", false);
            settings.put("autoJoinChannels", new ArrayList<String>());
            settings.put("autoNickChange", true);
            settings.put("autoReconnect", false);
            settings.put("reconnectAttempts", 3);
            settings.put("reconnectInterval", 600);
            settings.put("messageDelay", 500);
            settings.put("logPath", "logs");
            settings.put("logPMs", false);
            settings.put("commandPrefix", "~");
            settings.put("modules", new ArrayList<String>());
            settings.put("botAdmins", new ArrayList<String>());

            String settingsYaml = yaml.dump(settings);
            this.settings = settings;
            FileUtils.writeFile(fileName, settingsYaml);
        }
    }

    public String getSettingWithDefault(String setting, String defaultValue)
    {
        if (this.settings.containsKey(setting))
        {
            return (String) this.settings.get(setting);
        }
        else
        {
            Logger.error(
                    "Config",
                    "The setting \""
                            + setting
                            + "\" was not found in your global and local configs! Using the default value \""
                            + defaultValue + "\" instead.");
            return defaultValue;
        }
    }

    public int getSettingWithDefault(String setting, int defaultValue)
    {
        if (this.settings.containsKey(setting))
        {
            if (this.settings.get(setting) instanceof Integer)
            {
                return (Integer) this.settings.get(setting);
            }
            Logger.error("Config", "The setting \"" + setting
                    + "\" was invalid! Using the default value \"" + defaultValue + "\" instead.");
        }
        else
        {
            Logger.error(
                    "Config",
                    "The setting \""
                            + setting
                            + "\" was not found in your global and local configs! Using the default value \""
                            + defaultValue + "\" instead.");
        }
        return defaultValue;
    }

    public boolean getSettingWithDefault(String setting, boolean defaultValue)
    {
        if (this.settings.containsKey(setting))
        {
            if (this.settings.get(setting) instanceof Boolean)
            {
                return (Boolean) this.settings.get(setting);
            }
            Logger.error("Config", "The setting \"" + setting
                    + "\" was invalid! Using the default value \"" + defaultValue + "\" instead.");
        }
        else
        {
            Logger.error(
                    "Config",
                    "The setting \""
                            + setting
                            + "\" was not found in your global and local configs! Using the default value \""
                            + defaultValue + "\" instead.");
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getSettingWithDefault(String setting, ArrayList<String> defaultValue)
    {
        if (this.settings.containsKey(setting))
        {
            if (this.settings.get(setting) instanceof ArrayList<?>)
            {
                return (ArrayList<String>) this.settings.get(setting);
            }
            Logger.error("Config", "The setting \"" + setting
                    + "\" was invalid! Using the default value \"" + defaultValue + "\" instead.");
        }
        else
        {
            Logger.error(
                    "Config",
                    "The setting \""
                            + setting
                            + "\" was not found in your global and local configs! Using the default value \""
                            + defaultValue + "\" instead.");
        }
        return defaultValue;
    }

    public PasswordType getSettingWithDefault(String setting, PasswordType defaultValue)
    {
        if (this.settings.containsKey(setting))
        {
            try
            {
                return PasswordType.valueOf((String) this.settings.get(setting));
            }
            catch (IllegalArgumentException e)
            {
                Logger.error("Config", "The setting \"" + setting
                        + "\" was invalid! Using the default value \"" + defaultValue
                        + "\" instead.");
            }
        }
        else
        {
            Logger.error(
                    "Config",
                    "The setting \""
                            + setting
                            + "\" was not found in your global and local configs! Using the default value \""
                            + defaultValue + "\" instead.");
        }
        return defaultValue;
    }

    public HashMap<String, Object> getSettings()
    {
        return this.settings;
    }

    public List<CapHandler> getCapHandlers()
    {
        return this.capHandlers;
    }
}
