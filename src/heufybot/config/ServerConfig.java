package heufybot.config;

import heufybot.core.Logger;
import heufybot.utils.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;

public class ServerConfig extends GlobalConfig
{
    @SuppressWarnings("unchecked")
    public void loadServerConfig(String fileName, HashMap<String, Object> globalSettings)
    {
        this.settings = (HashMap<String, Object>) globalSettings.clone();
        if (fileName == null)
        {
            Logger.error("Config",
                    "No seperate server configs found. Using the settings from the global config instead.");
            return;
        }

        Yaml yaml = new Yaml();
        String settingsYaml = FileUtils.readFile(fileName);
        HashMap<String, Object> serverSettings = (HashMap<String, Object>) yaml.load(settingsYaml);

        for (String setting : serverSettings.keySet())
        {
            this.settings.put(setting, serverSettings.get(setting));
        }
    }
}
