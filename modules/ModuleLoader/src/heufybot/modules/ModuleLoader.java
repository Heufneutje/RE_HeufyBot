package heufybot.modules;

import heufybot.modules.ModuleInterface.ModuleLoaderResponse;
import heufybot.utils.StringUtils;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class ModuleLoader extends Module
{
    public ModuleLoader(String server)
    {
        super(server);

        this.authType = AuthType.BotAdmins;
        this.apiVersion = 60;
        this.triggerTypes = new TriggerType[] { TriggerType.Message };
        this.trigger = "^" + this.commandPrefix + "(load|unload|reload)($| .*)";
    }

    @Override
    public void processEvent(String source, String message, String triggerUser, List<String> params)
    {
        if (message.toLowerCase().matches("^" + this.commandPrefix + "load.*"))
        {
            if (params.size() == 1)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source, "Load what?");
            }
            else if (params.size() == 2)
            {
                String moduleName = params.get(1);
                SimpleEntry<ModuleLoaderResponse, String> result = this.bot.getServer(this.server)
                        .getModuleInterface().loadModule(moduleName);

                switch (result.getKey())
                {
                    case Success:
                        this.bot.getServer(this.server).cmdPRIVMSG(source,
                                "Module \"" + result.getValue() + "\" was successfully loaded!");
                        break;
                    case AlreadyLoaded:
                        this.bot.getServer(this.server).cmdPRIVMSG(source,
                                "Module \"" + moduleName + "\" is already loaded.");
                        break;
                    case DoesNotExist:
                        this.bot.getServer(this.server).cmdPRIVMSG(source,
                                "Module \"" + moduleName + "\" does not exist.");
                        break;
                    case APIVersionDoesNotMatch:
                        String moduleVersion = result.getValue().split(" ")[0];
                        String apiVersion = result.getValue().split(" ")[1];
                        this.bot.getServer(this.server).cmdPRIVMSG(
                                source,
                                "Module \"" + moduleName
                                        + "\" could not be loaded. Its module API version ("
                                        + moduleVersion
                                        + ") does not match the bot's API version (" + apiVersion
                                        + ")");
                        break;
                    default:
                        break;
                }
            }
            else
            {
                params.remove(0);

                List<String> successList = new ArrayList<String>();
                List<String> alreadyLoadedList = new ArrayList<String>();
                List<String> doesNotExistList = new ArrayList<String>();

                for (String moduleName : params)
                {
                    SimpleEntry<ModuleLoaderResponse, String> result = this.bot
                            .getServer(this.server).getModuleInterface().loadModule(moduleName);

                    switch (result.getKey())
                    {
                        case Success:
                            successList.add(result.getValue());
                            break;
                        case AlreadyLoaded:
                            alreadyLoadedList.add(moduleName);
                            break;
                        case DoesNotExist:
                            doesNotExistList.add(moduleName);
                            break;
                        default:
                            break;
                    }
                }
                if (successList.size() > 0)
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(
                            source,
                            "Module(s) \"" + StringUtils.join(successList, "\", \"")
                                    + "\" were successfully loaded!");
                }
                if (alreadyLoadedList.size() > 0)
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(
                            source,
                            "Module(s) \"" + StringUtils.join(alreadyLoadedList, "\", \"")
                                    + "\" were already loaded!");
                }
                if (doesNotExistList.size() > 0)
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(
                            source,
                            "Module(s) \"" + StringUtils.join(doesNotExistList, "\", \"")
                                    + "\" do not exist.");
                }
            }
        }
        else if (message.toLowerCase().matches("^" + this.commandPrefix + "unload.*"))
        {
            if (params.size() == 1)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source, "Unload what?");
            }
            else if (params.size() == 2)
            {
                String moduleName = params.get(1);

                if (moduleName.equals("*"))
                {
                    List<String> moduleNames = new ArrayList<String>();
                    for (Module module : this.bot.getServer(this.server).getModuleInterface()
                            .getModuleList())
                    {
                        moduleNames.add(module.toString());
                    }

                    for (String module : moduleNames)
                    {
                        if (!module.equalsIgnoreCase("ModuleLoader"))
                        {
                            this.bot.getServer(this.server).getModuleInterface()
                                    .unloadModule(module);
                        }
                    }
                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                            "All modules have been unloaded!");
                }
                else
                {
                    if (moduleName.equalsIgnoreCase("ModuleLoader"))
                    {
                        this.bot.getServer(this.server).cmdPRIVMSG(source,
                                "The \"ModuleLoader\" module cannot be unloaded!");
                        return;
                    }
                    SimpleEntry<ModuleLoaderResponse, String> result = this.bot
                            .getServer(this.server).getModuleInterface().unloadModule(moduleName);

                    switch (result.getKey())
                    {
                        case Success:
                            this.bot.getServer(this.server).cmdPRIVMSG(
                                    source,
                                    "Module \"" + result.getValue()
                                            + "\" was successfully unloaded!");
                            break;
                        case DoesNotExist:
                            this.bot.getServer(this.server).cmdPRIVMSG(
                                    source,
                                    "Module \"" + moduleName
                                            + "\" is not loaded or does not exist.");
                            break;
                        default:
                            break;
                    }
                }
            }
            else
            {
                params.remove(0);

                List<String> successList = new ArrayList<String>();
                List<String> doesNotExistList = new ArrayList<String>();

                for (String moduleName : params)
                {
                    SimpleEntry<ModuleLoaderResponse, String> result = this.bot
                            .getServer(this.server).getModuleInterface().unloadModule(moduleName);

                    switch (result.getKey())
                    {
                        case Success:
                            successList.add(result.getValue());
                            break;
                        case DoesNotExist:
                            doesNotExistList.add(moduleName);
                            break;
                        default:
                            break;
                    }
                }

                if (successList.size() > 0)
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(
                            source,
                            "Module(s) \"" + StringUtils.join(successList, "\", \"")
                                    + "\" were successfully unloaded!");
                }
                if (doesNotExistList.size() > 0)
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(
                            source,
                            "Module(s) \"" + StringUtils.join(doesNotExistList, "\", \"")
                                    + "\" are not loaded or do not exist.");
                }
            }
        }
        else
        {
            if (params.size() == 1)
            {
                this.bot.getServer(this.server).cmdPRIVMSG(source, "Reload what?");
            }
            else if (params.size() == 2)
            {
                String moduleName = params.get(1);

                if (moduleName.equals("*"))
                {
                    List<String> moduleNames = new ArrayList<String>();
                    for (Module module : this.bot.getServer(this.server).getModuleInterface()
                            .getModuleList())
                    {
                        moduleNames.add(module.toString());
                    }

                    for (String moduleName2 : moduleNames)
                    {
                        this.bot.getServer(this.server).getModuleInterface()
                                .unloadModule(moduleName2);
                        this.bot.getServer(this.server).getModuleInterface()
                                .loadModule(moduleName2);
                    }

                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                            "All modules have been reloaded!");
                }
                else
                {
                    SimpleEntry<ModuleLoaderResponse, String> result = this.bot
                            .getServer(this.server).getModuleInterface().unloadModule(moduleName);

                    switch (result.getKey())
                    {
                        case Success:
                        {
                            SimpleEntry<ModuleLoaderResponse, String> result2 = this.bot
                                    .getServer(this.server).getModuleInterface()
                                    .loadModule(moduleName);

                            switch (result2.getKey())
                            {
                                case Success:
                                    this.bot.getServer(this.server).cmdPRIVMSG(
                                            source,
                                            "Module \"" + result2.getValue()
                                                    + "\" was successfully reloaded!");
                                    break;
                                case AlreadyLoaded:
                                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                                            "Module \"" + moduleName + "\" is already loaded.");
                                    break;
                                case DoesNotExist:
                                    this.bot.getServer(this.server).cmdPRIVMSG(source,
                                            "Module \"" + moduleName + "\" does not exist.");
                                    break;
                                case APIVersionDoesNotMatch:
                                    String moduleVersion = result2.getValue().split(" ")[0];
                                    String apiVersion = result2.getValue().split(" ")[1];
                                    this.bot.getServer(this.server)
                                            .cmdPRIVMSG(
                                                    source,
                                                    "Module \""
                                                            + moduleName
                                                            + "\" could not be loaded. Its module API version ("
                                                            + moduleVersion
                                                            + ") does not match the bot's API version ("
                                                            + apiVersion + ")");
                                    break;
                                default:
                                    break;
                            }
                            break;
                        }
                        case DoesNotExist:
                            this.bot.getServer(this.server).cmdPRIVMSG(
                                    source,
                                    "Module \"" + moduleName
                                            + "\" is not loaded or does not exist.");
                            break;
                        default:
                            break;
                    }
                }
            }
            else
            {
                params.remove(0);

                List<String> successList = new ArrayList<String>();
                List<String> doesNotExistList = new ArrayList<String>();

                for (String moduleName : params)
                {
                    SimpleEntry<ModuleLoaderResponse, String> result = this.bot
                            .getServer(this.server).getModuleInterface().unloadModule(moduleName);

                    switch (result.getKey())
                    {
                        case Success:
                        {
                            if (this.bot.getServer(this.server).getModuleInterface()
                                    .loadModule(moduleName).getKey() == ModuleLoaderResponse.Success)
                            {
                                successList.add(moduleName);
                            }
                            else
                            {
                                doesNotExistList.add(moduleName);
                            }
                            break;
                        }
                        case DoesNotExist:
                            doesNotExistList.add(moduleName);
                            break;
                        default:
                            break;
                    }
                }

                if (successList.size() > 0)
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(
                            source,
                            "Module(s) \"" + StringUtils.join(successList, "\", \"")
                                    + "\" were successfully reloaded!");
                }
                if (doesNotExistList.size() > 0)
                {
                    this.bot.getServer(this.server).cmdPRIVMSG(
                            source,
                            "Module(s) \"" + StringUtils.join(doesNotExistList, "\", \"")
                                    + "\" are not loaded or do not exist.");
                }
            }
        }
    }

    @Override
    public String getHelp(String message)
    {
        if (message.toLowerCase().matches("load.*"))
        {
            return "Commands: "
                    + this.commandPrefix
                    + "load <module> | Load one or more modules. Separate module names by spaces if more.";
        }
        else if (message.toLowerCase().matches("unload.*"))
        {
            return "Commands: "
                    + this.commandPrefix
                    + "unload <module> | Unload one or more modules. Separate module names by spaces if more.";
        }
        else if (message.toLowerCase().matches("reload.*"))
        {
            return "Commands: "
                    + this.commandPrefix
                    + "reload <module> | Reload one or more modules. Separate module names by spaces if more.";
        }

        return "Commands: "
                + this.commandPrefix
                + "load <module>, "
                + this.commandPrefix
                + "unload <module>, "
                + this.commandPrefix
                + "reload <module> | Load, unload or reload one or more modules. Separate module names by spaces if " +
                "more.";
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
