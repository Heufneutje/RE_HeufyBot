package config;

import heufybot.utils.FileUtils;

import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

public class ServerConfig extends GlobalConfig
{
	@SuppressWarnings("unchecked")
	public void loadServerConfig(String fileName, HashMap<String, Object> globalSettings)
	{
		this.settings = globalSettings;
		
		Yaml yaml = new Yaml();
		String settingsYaml = FileUtils.readFile(fileName);
		HashMap<String, Object> serverSettings = (HashMap<String, Object>) yaml.load(settingsYaml);
		
		for(String setting : serverSettings.keySet())
		{
			settings.put(setting, serverSettings.get(setting));
		}
	}
}