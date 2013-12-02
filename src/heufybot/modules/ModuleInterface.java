package heufybot.modules;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;

import heufybot.core.events.EventListenerAdapter;
import heufybot.core.events.types.*;
import heufybot.utils.enums.ModuleLoaderResponse;

public class ModuleInterface extends EventListenerAdapter
{
	private ArrayList<Module> modules;
	
	public ModuleInterface()
	{
		this.modules = new ArrayList<Module>();
	}
	
	public ModuleLoaderResponse loadModule(String moduleName)
	{
		try
		{
			for(Module module : modules)
			{
				if(module.getClass().getName().equals("heufybot.modules." + moduleName))
				{
					return ModuleLoaderResponse.AlreadyLoaded;
				}
			}
			
			File moduleJar = new File(moduleName + ".jar");
			URL[] urls = { moduleJar.toURI().toURL() };
			
			ClassLoader loader = URLClassLoader.newInstance(urls, getClass().getClassLoader());
			
			Class<?> moduleClass = Class.forName("heufybot.modules." + moduleName, true, loader);
			Module module = (Module) moduleClass.newInstance();
			
			modules.add(module);
			module.onLoad();
			
			return ModuleLoaderResponse.Success;
		} 
		catch (Exception e)
		{
			return ModuleLoaderResponse.DoesNotExist;
		}
	}
	
	public ModuleLoaderResponse unloadModule(String moduleName)
	{
		for(Iterator<Module> iter = modules.iterator(); iter.hasNext();)
  		{
  			Module module = iter.next();
  			if(module.getName().equals(moduleName))
  			{
  				module.onUnload();
  				iter.remove();
  				return ModuleLoaderResponse.Success;
  			}
  		}
		return ModuleLoaderResponse.DoesNotExist;
	}
	
	public void onMessage(MessageEvent event)
	{
		String message = event.getMessage();
		Module[] listCopy = new Module[modules.size()];
		listCopy = modules.toArray(listCopy);
		for (int l = 0; l < listCopy.length; l++)
		{
			Module module = listCopy[l];
			for(int i = 0; i < module.getTriggers().length; i++)
			{
				if(module.getTriggers().length > 0 && message.toLowerCase().split(" ")[0].matches("^" + module.getTriggers()[i] + "$"))
				{
					module.processEvent(event.getChannel().getName(), message.substring(module.getTriggers()[i].length()), event.getUser().getNickname(), module.getTriggers()[i]);
				}
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
			if(module.getName().equals(moduleName))
			{
				return module.getHelp();
			}
		}
		return null;
	}
}
