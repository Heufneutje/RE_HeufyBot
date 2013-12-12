package heufybot.modules;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import heufybot.core.Channel;
import heufybot.core.HeufyBot;
import heufybot.core.User;
import heufybot.core.events.EventListenerAdapter;
import heufybot.core.events.types.*;
import heufybot.utils.StringUtils;
import heufybot.utils.enums.ModuleLoaderResponse;

public class ModuleInterface extends EventListenerAdapter
{
	private ArrayList<Module> modules;
	private List<String> ignores;
	private HeufyBot bot;
	
	public ModuleInterface(HeufyBot bot)
	{
		this.modules = new ArrayList<Module>();
		this.setIgnores(new ArrayList<String>());
		this.bot = bot;
	}
	
	public SimpleEntry<ModuleLoaderResponse, String> loadModule(String moduleName)
	{
		try
		{
			for(Module module : modules)
			{
				String toLoad = "heufybot.modules." + moduleName;
				if(module.getClass().getName().toLowerCase().equals(toLoad.toLowerCase()))
				{
					return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.AlreadyLoaded, "");
				}
			}
			
			File[] folder = new File("modules").listFiles();
			for(int i = 0; i < folder.length; i++)
			{
				if(folder[i].getName().equalsIgnoreCase(moduleName + ".jar"))
				{
					String foundFileName = folder[i].getName();
					moduleName = foundFileName.substring(0, foundFileName.indexOf(".jar"));
					URL[] urls = { folder[i].toURI().toURL() };
					
					ClassLoader loader = URLClassLoader.newInstance(urls, getClass().getClassLoader());
					
					Class<?> moduleClass = Class.forName("heufybot.modules." + moduleName, true, loader);
					Module module = (Module) moduleClass.newInstance();
					
					modules.add(module);
					module.onLoad();
					
					return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.Success, moduleName);
				}
			}
			return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.DoesNotExist, "");
		} 
		catch (Exception e)
		{
			return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.DoesNotExist, "");
		}
	}
	
	public SimpleEntry<ModuleLoaderResponse, String> unloadModule(String moduleName)
	{
		for(Iterator<Module> iter = modules.iterator(); iter.hasNext();)
		{
  			Module module = iter.next();
  			if(module.toString().equalsIgnoreCase(moduleName))
  			{
  				module.onUnload();
  				iter.remove();
  				return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.Success, module.toString());
  			}
		}
		return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.DoesNotExist, "");
	}
	
	public void onPMMessage(PMMessageEvent event)
	{
		handleMessage(event.getUser(), null, event.getMessage());
	}
	
	public void onMessage(MessageEvent event)
	{
		handleMessage(event.getUser(), event.getChannel(), event.getMessage());
	}
	
	private void handleMessage(User user, Channel channel, String message)
	{
		if(ignores.contains(user.getNickname()))
		{
			return;
		}
		
		Module[] listCopy = new Module[modules.size()];
		listCopy = modules.toArray(listCopy);
		for (int l = 0; l < listCopy.length; l++)
		{
			Module module = listCopy[l];
			if(message.toLowerCase().matches(module.getTrigger()))
			{
				if(channel == null)
				{
					if(bot.getConfig().getBotAdmins().contains(user.getNickname()))
					{
						module.processEvent(user.getNickname(), message, user.getNickname(), StringUtils.parseStringtoList(message, " "));
					}
					else
					{
						bot.getIRC().cmdACTION(user.getNickname(), "You are not authorized to use the \"" + module.toString() + "\" module!");
					}
				}
				else
				{
					if(isAuthorized(module, channel, user) || bot.getConfig().getBotAdmins().contains(user.getNickname()))
					{
						module.processEvent(channel.getName(), message, user.getNickname(), StringUtils.parseStringtoList(message, " "));
					}
					else
					{
						bot.getIRC().cmdPRIVMSG(channel.getName(), "You are not authorized to use the \"" + module.toString() + "\" module!");
					}
				}
			}
		}
	}
	
	public boolean isAuthorized(Module module, Channel channel, User user)
	{
		if(module.authType == Module.AuthType.Anyone)
		{
			return true;
		}
		else
		{
			if(bot.getConfig().isOpAdmins())
			{
				return channel.checkOpStatus(user);
			}
			else
			{
				return false;
			}
		}
	}
	
	public String getModuleHelp(String moduleName)
	{
		for(Module module : modules)
		{
			if(module.toString().equalsIgnoreCase(moduleName))
			{
				return module.getHelp();
			}
		}
		return null;
	}
	
	public ArrayList<Module> getModuleList()
	{
		return modules;
	}
	
	public List<String> getIgnores() 
	{
		return ignores;
	}

	public void setIgnores(List<String> ignores) 
	{
		this.ignores = ignores;
	}
}
