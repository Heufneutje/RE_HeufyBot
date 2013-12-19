package heufybot.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import heufybot.core.cap.*;
import heufybot.utils.enums.PasswordType;

public class Config 
{
	private String nickname, username, realname, server, password, commandPrefix;
	private int port, reconnectAttempts, reconnectInterval;
	private PasswordType passwordType;
	private boolean autoJoinEnabled, sslEnabled, autoNickChange, autoReconnect, opAdmins;
	private List<String> autoJoinChannels, modulesToLoad, botAdmins;
	private long messageDelay;
	private List<CapHandler> capHandlers;
	
	private static final Config instance = new Config();
	
	private Config()
	{
		this.messageDelay = 500;
		this.capHandlers = new ArrayList<CapHandler>();
		this.capHandlers.add(new EnablingCapHandler("multi-prefix"));
	}
	
	public static Config getInstance()
	{
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public boolean loadConfigFromFile(String fileName)
	{
		Logger.log("*** Loading settings...");
		
		try
		{
			InputStream input = new FileInputStream(new File(fileName));
			Yaml yaml = new Yaml();

			List<Map<?, ?>> readSettings = (List<Map<?, ?>>) yaml.load(input);
			
			Map<String, String> nickSetting = (Map<String, String>) readSettings.get(0);
			this.nickname = nickSetting.get("nickname");
			
			Map<String, String> userSetting = (Map<String, String>) readSettings.get(1);
			this.username = userSetting.get("username");
			
			Map<String, String> realnameSetting = (Map<String, String>) readSettings.get(2);
			this.realname = realnameSetting.get("realname");
			
			Map<String, String> serverSetting = (Map<String, String>) readSettings.get(3);
			this.server = serverSetting.get("server");
			
			Map<String, String> portSetting = (Map<String, String>) readSettings.get(4);
			String stringPort = portSetting.get("port");
			if(stringPort.startsWith("+"))
			{
				stringPort = stringPort.substring(1);
				sslEnabled = true;
			}
			this.port = Integer.parseInt(stringPort);
			
			Map<String, String> passSetting = (Map<String, String>) readSettings.get(5);
			this.password = passSetting.get("password");
			
			Map<String, String> passTypeSetting = (Map<String, String>) readSettings.get(6);
			this.passwordType = PasswordType.valueOf(passTypeSetting.get("passwordType"));
			
			Map<String, Boolean> autojoinSetting = (Map<String, Boolean>) readSettings.get(7);
			this.autoJoinEnabled = autojoinSetting.get("autoJoinEnabled");
			
			Map<String, List<String>> joinChannelSetting = (Map<String, List<String>>) readSettings.get(8);
			this.autoJoinChannels = joinChannelSetting.get("autoJoinChannels");
			
			Map<String, Boolean> nickChangeSetting = (Map<String, Boolean>) readSettings.get(9);
			this.autoNickChange = nickChangeSetting.get("autoNickChange");
			
			Map<String, Boolean> reconnectSetting = (Map<String, Boolean>) readSettings.get(10);
			this.autoReconnect = reconnectSetting.get("autoReconnect");
			
			Map<String, Integer> attemptsSetting = (Map<String, Integer>) readSettings.get(11);
			this.reconnectAttempts = attemptsSetting.get("reconnectAttempts");
			
			Map<String, Integer> intervalSetting = (Map<String, Integer>) readSettings.get(12);
			this.reconnectInterval = intervalSetting.get("reconnectInterval");
			
			Map<String, String> prefixSetting = (Map<String, String>) readSettings.get(13);
			this.commandPrefix = prefixSetting.get("commandPrefix");
			
			Map<String, List<String>> modulesSetting = (Map<String, List<String>>) readSettings.get(14);
			this.modulesToLoad = modulesSetting.get("modulesToLoad");
			
			Map<String, List<String>> adminSetting = (Map<String, List<String>>) readSettings.get(15);
			this.botAdmins = adminSetting.get("botAdmins");
			
			Map<String, Boolean> opSetting = (Map<String, Boolean>) readSettings.get(16);
			this.opAdmins = opSetting.get("opAdmins");
			
			if(autoJoinChannels == null)
			{
				this.autoJoinChannels = new ArrayList<String>();
			}
			if(modulesToLoad == null)
			{
				this.modulesToLoad = new ArrayList<String>();
			}
			if(botAdmins == null)
			{
				this.botAdmins = new ArrayList<String>();
			}
			
			if(nickname.equals(""))
			{
				Logger.error("Configuration", "Nickname cannot be empty.");
				return false;
			}
			if(username.equals(""))
			{
				username = "Unknown";
			}
			if(realname.equals(""))
			{
				realname = "Unknown";
			}
			
			if(passwordType == PasswordType.SASL)
			{
				this.capHandlers.add(new SASLCapHandler(username, password));
			}
			
			Logger.log("*** Loaded settings successfully");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Logger.error("Configuration", "The config file could not be found. Make sure to create one or copy the default settings.yml.example into settings.yml.");
			return false;
		}
		catch (NullPointerException e)
		{
			Logger.error("Configuration", "The config file could not be read. One of the setting fields is empty.");
			return false;
		}
		catch (IllegalArgumentException e)
		{
			Logger.error("Configuration", "The config file could not be read. One of the setting fields has an invalid value.");
			return false;
		}
		catch (Exception e)
		{
			Logger.error("Configuration", "The config file could not be read. Make sure the syntax is correct.");
			return false;
		}
	}
	
	public String getNickname()
	{
		return nickname;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getRealname()
	{
		return realname;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public boolean isSSLEnabled()
	{
		return sslEnabled;
	}
	
	public PasswordType getPasswordType()
	{
		return passwordType;
	}
	
	public boolean getAutoJoinEnabled()
	{
		return autoJoinEnabled;
	}
	
	public boolean getAutoNickChange()
	{
		return autoNickChange;
	}
	
	public List<String> getAutoJoinChannels()
	{
		return autoJoinChannels;
	}

	public int getReconnectAttempts() 
	{
		return reconnectAttempts;
	}

	public boolean autoReconnect() 
	{
		return autoReconnect;
	}

	public int getReconnectInterval()
	{
		return reconnectInterval;
	}

	public long getMessageDelay()
	{
		return messageDelay;
	}
	
	public List<CapHandler> getCapHandlers()
	{
		return capHandlers;
	}
	
	public String getCommandPrefix()
	{
		return commandPrefix;
	}

	public List<String> getModulesToLoad()
	{
		return modulesToLoad;
	}
	
	public List<String> getBotAdmins()
	{
		return botAdmins;
	}
	
	public boolean isOpAdmins()
	{
		return opAdmins;
	}
}