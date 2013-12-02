package heufybot.modules;

public class Moduleloader extends Module
{
	public Moduleloader()
	{
		this.name = "Moduleloader";
		this.authType = Module.AuthType.OPs;
		
		this.triggers = new String[3];
		this.triggers[0] = bot.getConfig().getCommandPrefix() + "load";
		this.triggers[1] = bot.getConfig().getCommandPrefix() + "unload";
		this.triggers[2] = bot.getConfig().getCommandPrefix() + "reload";
	}

	public void processEvent(String source, String metadata, String triggerUser, String triggerCommand)
	{
		if(triggerCommand.equals(bot.getConfig().getCommandPrefix() + "load"))
		{
			if ((metadata.equals("")) || (metadata.equals(" ")))
			{
				bot.getIRC().cmdPRIVMSG(source, "Load what?");
			}
			else if (metadata.startsWith(" "))
			{
				String[] modules = metadata.substring(1).split(" ");
				for(int i = 0; i < modules.length; i++)
				{
					String moduleName = Character.toUpperCase(modules[i].toLowerCase().charAt(0)) + modules[i].toLowerCase().substring(1);
					
					switch (bot.getModuleInterface().loadModule(moduleName)) 
					{
					case Success:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + moduleName + "\" was successfully loaded!");
						break;
					case AlreadyLoaded:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + moduleName + "\" is already loaded!");
						break;
					case DoesNotExist:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + moduleName + "\" does not exist!");
					default:
						break;
					}
				}
			}
		}
		else if(triggerCommand.equals(bot.getConfig().getCommandPrefix() + "unload"))
		{
			if ((metadata.equals("")) || (metadata.equals(" ")))
			{
				bot.getIRC().cmdPRIVMSG(source, "Unload what?");
			}
			else if (metadata.startsWith(" "))
			{
				String[] modules = metadata.substring(1).split(" ");
				for(int i = 0; i < modules.length; i++)
				{
					String moduleName = Character.toUpperCase(modules[i].toLowerCase().charAt(0)) + modules[i].toLowerCase().substring(1);
	
					switch (bot.getModuleInterface().unloadModule(moduleName)) 
					{
					case Success:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + moduleName + "\" was successfully unloaded!");
						break;
					case DoesNotExist:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + moduleName + "\" is not loaded or does not exist!");
						break;
					default:
						break;
					}
				}
			}
		}
		else
		{
			if ((metadata.equals("")) || (metadata.equals(" ")))
			{
				bot.getIRC().cmdPRIVMSG(source, "Reload what?");
			}
			else if (metadata.startsWith(" "))
			{
				String[] modules = metadata.substring(1).split(" ");
				for(int i = 0; i < modules.length; i++)
				{
					String moduleName = Character.toUpperCase(modules[i].toLowerCase().charAt(0)) + modules[i].toLowerCase().substring(1);
					
					switch (bot.getModuleInterface().unloadModule(moduleName)) 
					{
					case Success:
						bot.getModuleInterface().loadModule(moduleName);
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + moduleName + "\" was successfully reloaded!");
						break;
					case DoesNotExist:
						bot.getIRC().cmdPRIVMSG(source, "Module \"" + moduleName + "\" is not loaded or does not exist!");
						break;
					default:
						break;
					}
				}
			}
		}
	}

	@Override
	public String getHelp()
	{
		return "Commands: " + bot.getConfig().getCommandPrefix() + "load <module>, " + bot.getConfig().getCommandPrefix() + "unload <module>, " + bot.getConfig().getCommandPrefix() + "reload <module> | Load, unload or reload one or more modules. Seperate module names by spaces if more.";
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onUnload()
	{	
	}
}
