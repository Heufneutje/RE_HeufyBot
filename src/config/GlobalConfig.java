package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import heufybot.core.cap.*;

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
	public boolean loadGlobalConfig(String fileName)
	{
		try 
		{
			InputStream input = new FileInputStream(new File(fileName));
			Yaml yaml = new Yaml();
			settings = (HashMap<String, Object>) yaml.load(input);
			
			return true;
		} 
		catch (FileNotFoundException e) 
		{
			return false;
		}
	}
	
	public String getSettingWithDefault(String setting, String defaultValue)
	{
		if(settings.containsKey(setting))
		{
			return (String) settings.get(setting);
		}
		else
		{
			return defaultValue;
		}
	}
	
	public int getSettingWithDefault(String setting, int defaultValue)
	{
		if(settings.containsKey(setting))
		{
			if(settings.get(setting) instanceof Integer)
			{
				return (Integer) settings.get(setting);
			}
		}
		return defaultValue;
	}
	
	public boolean getSettingWithDefault(String setting, boolean defaultValue)
	{
		if(settings.containsKey(setting))
		{
			if(settings.get(setting) instanceof Boolean)
			{
				return (Boolean) settings.get(setting);
			}
		}
		return defaultValue;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getSettingWithDefault(String setting, ArrayList<String> defaultValue)
	{
		if(settings.containsKey(setting))
		{
			if(settings.get(setting) instanceof ArrayList<?>)
			{
				return (ArrayList<String>) settings.get(setting);
			}
		}
		return defaultValue;
	}
	
	public PasswordType getSettingWithDefault(String setting, PasswordType defaultValue)
	{
		if(settings.containsKey(setting))
		{
			if(settings.get(setting) instanceof PasswordType)
			{
				return (PasswordType) settings.get(setting);
			}
		}
		return defaultValue;
	}
	
	public HashMap<String, Object> getSettings()
	{
		return settings;
	}

	public List<CapHandler> getCapHandlers() 
	{
		return capHandlers;
	}

	public void setCapHandlers(List<CapHandler> capHandlers)
	{
		this.capHandlers = capHandlers;
	}
}