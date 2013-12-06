package heufybot.modules;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;

import heufybot.core.events.EventListenerAdapter;
import heufybot.core.events.types.*;
import heufybot.utils.StringUtils;
import heufybot.utils.enums.ModuleLoaderResponse;

public class ModuleInterface extends EventListenerAdapter
{
	private ArrayList<Module> modules;
	
	public ModuleInterface()
	{
		this.modules = new ArrayList<Module>();
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
  			if(module.getClass().getSimpleName().equalsIgnoreCase(moduleName))
  			{
  				module.onUnload();
  				iter.remove();
  				return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.Success, module.getClass().getSimpleName());
  			}
		}
		return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.DoesNotExist, "");
	}
	
	public void onMessage(MessageEvent event)
	{
		String message = event.getMessage();
		Module[] listCopy = new Module[modules.size()];
		listCopy = modules.toArray(listCopy);
		for (int l = 0; l < listCopy.length; l++)
		{
			Module module = listCopy[l];
			if(message.toLowerCase().matches(module.getTrigger()))
			{
				module.processEvent(event.getChannel().getName(), message, event.getUser().getNickname(), StringUtils.parseStringtoList(message, " "));
			}
		}
	}
	
	public ArrayList<Module> getModuleList()
	{
		return modules;
	}
	
	public String getModuleHelp(String moduleName)
	{
		for(Module module : modules)
		{
			if(module.getClass().getSimpleName().equalsIgnoreCase(moduleName))
			{
				return module.getHelp();
			}
		}
		return null;
	}
}
