package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

public class ServerConfig extends GlobalConfig
{
	@SuppressWarnings("unchecked")
	public boolean loadServerConfig(String fileName, GlobalConfig config)
	{
		this.settings = config.getSettings();
		
		try
		{
			InputStream input = new FileInputStream(new File(fileName));
			Yaml yaml = new Yaml();
			HashMap<String, Object> serverSettings = (HashMap<String, Object>) yaml.load(input);
			
			for(String setting : serverSettings.keySet())
			{
				settings.put(setting, serverSettings.get(setting));
			}
			
			return true;
		} 
		catch (FileNotFoundException e)
		{
			return false;
		}
	}
}