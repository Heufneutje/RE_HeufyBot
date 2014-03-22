package heufybot.modules;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import heufybot.core.Channel;
import heufybot.core.HeufyBot;
import heufybot.core.User;
import heufybot.core.events.EventListenerAdapter;
import heufybot.core.events.types.*;
import heufybot.modules.Module.TriggerType;
import heufybot.utils.StringUtils;

public class ModuleInterface extends EventListenerAdapter
{
	private ArrayList<Module> modules;
	private List<String> ignores;
	private HeufyBot bot;
	
	public enum ModuleLoaderResponse
	{
		Success, DoesNotExist, AlreadyLoaded, APIVersionDoesNotMatch
	}
	
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
					
					if(!module.getAPIVersion().equals(HeufyBot.MODULE_API_VERSION))
					{
						return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.APIVersionDoesNotMatch, HeufyBot.MODULE_API_VERSION + " " + module.getAPIVersion());
					}
					
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
		handleMessage(event.getUser(), null, event.getMessage(), TriggerType.Message);
	}
	
	public void onPMAction(PMActionEvent event)
	{
		handleMessage(event.getUser(), null, event.getMessage(), TriggerType.Action);
	}
	
	public void onMessage(MessageEvent event)
	{
		handleMessage(event.getUser(), event.getChannel(), event.getMessage(), TriggerType.Message);
	}
	
	public void onAction(ActionEvent event)
	{
		handleMessage(event.getUser(), event.getChannel(), event.getMessage(), TriggerType.Action);
	}
	
	private void handleMessage(final User user, final Channel channel, final String message, TriggerType triggerType)
	{
		if(ignores.contains(user.getNickname()))
		{
			return;
		}
		
		Module[] listCopy = new Module[modules.size()];
		listCopy = modules.toArray(listCopy);
		for (int l = 0; l < listCopy.length; l++)
		{
			final Module module = listCopy[l];
			if((message.toLowerCase().matches(module.getTrigger()) || module.getTriggerOnEveryMessage()) && Arrays.asList(module.getTriggerTypes()).contains(triggerType))
			{
				final String target;
				if(channel == null)
				{
					target = user.getNickname();
				}
				else
				{
					target = channel.getName();
				}
				
				if(isAuthorized(module, channel, user))
				{
					if(module.getTriggerOnEveryMessage())
					{
						module.processEvent(target, message, user.getNickname(), StringUtils.parseStringtoList(message, " "));
					}
					else if (Thread.activeCount() < 6)
					{
						//Thread limit might have to become a setting in the future
						Thread thread = new Thread()
						{
							public void run()
							{
								module.processEvent(target, message, user.getNickname(), StringUtils.parseStringtoList(message, " "));
							}
						};
						thread.start();
					}
					else
					{
						bot.getIRC().cmdPRIVMSG(target, "Calm down, " + user.getNickname() + "! Can't you see I'm busy?");
					}
				}
				else
				{
					bot.getIRC().cmdPRIVMSG(target, "You are not authorized to use the \"" + module.toString() + "\" module!");
				}
			}
		}
	}
	
	public boolean isAuthorized(Module module, Channel channel, User user)
	{
		return module.authType == Module.AuthType.Anyone || bot.getConfig().getBotAdmins().contains(user.getNickname());
	}
	
	public boolean isModuleLoaded(String moduleName)
	{
		for(Module module : modules)
		{
			if(module.toString().equalsIgnoreCase(moduleName))
			{
				return true;
			}
		}
		return false;
	}
	
	public String getModuleHelp(String message)
	{
		for(Module module : modules)
		{
			String moduleTrigger = module.getTrigger();
			String commandPrefix = bot.getConfig().getCommandPrefix();
			
			if(moduleTrigger.startsWith("^" + commandPrefix))
			{
				moduleTrigger = moduleTrigger.substring(moduleTrigger.indexOf(commandPrefix) + commandPrefix.length());
			}
			
			if(module.toString().equalsIgnoreCase(message) || message.toLowerCase().matches(moduleTrigger))
			{
				return module.getHelp(message.toLowerCase());
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
