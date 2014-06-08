package heufybot.modules;

import heufybot.core.HeufyBot;
import heufybot.core.IRCChannel;
import heufybot.core.IRCUser;
import heufybot.core.events.EventListenerAdapter;
import heufybot.core.events.types.ActionEvent;
import heufybot.core.events.types.MessageEvent;
import heufybot.core.events.types.PMActionEvent;
import heufybot.core.events.types.PMMessageEvent;
import heufybot.modules.Module.TriggerType;
import heufybot.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ModuleInterface extends EventListenerAdapter
{
    private ArrayList<Module> modules;
    private List<String> ignores;
    private HeufyBot bot;
    private String server;

    public enum ModuleLoaderResponse
    {
        Success, DoesNotExist, AlreadyLoaded, APIVersionDoesNotMatch
    }

    public ModuleInterface(HeufyBot bot, String server)
    {
        this.modules = new ArrayList<Module>();
        this.setIgnores(new ArrayList<String>());
        this.bot = bot;
        this.server = server;
    }

    public SimpleEntry<ModuleLoaderResponse, String> loadModule(String moduleName)
    {
        try
        {
            for (Module module : this.modules)
            {
                String toLoad = "heufybot.modules." + moduleName;
                if (module.getClass().getName().toLowerCase().equals(toLoad.toLowerCase()))
                {
                    return new SimpleEntry<ModuleLoaderResponse, String>(
                            ModuleLoaderResponse.AlreadyLoaded, "");
                }
            }

            File[] folder = new File("modules").listFiles();
            for (File element : folder)
            {
                if (element.getName().equalsIgnoreCase(moduleName + ".jar"))
                {
                    String foundFileName = element.getName();
                    moduleName = foundFileName.substring(0, foundFileName.indexOf(".jar"));
                    URL[] urls = { element.toURI().toURL() };

                    ClassLoader loader = URLClassLoader.newInstance(urls, this.getClass()
                            .getClassLoader());

                    Class<?> moduleClass = Class.forName("heufybot.modules." + moduleName, true,
                            loader);
                    Class<?>[] argTypes = { String.class };
                    Constructor<?> ctor = moduleClass.getDeclaredConstructor(argTypes);
                    Module module = (Module) ctor.newInstance(this.server);

                    if (module.getAPIVersion() != HeufyBot.MODULE_API_VERSION)
                    {
                        return new SimpleEntry<ModuleLoaderResponse, String>(
                                ModuleLoaderResponse.APIVersionDoesNotMatch, module.getAPIVersion()
                                        + " " + HeufyBot.MODULE_API_VERSION);
                    }

                    this.modules.add(module);
                    module.onLoad();

                    return new SimpleEntry<ModuleLoaderResponse, String>(
                            ModuleLoaderResponse.Success, moduleName);
                }
            }
            return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.DoesNotExist,
                    "");
        }
        catch (Exception e)
        {
            return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.DoesNotExist,
                    "");
        }
    }

    public SimpleEntry<ModuleLoaderResponse, String> unloadModule(String moduleName)
    {
        for (Iterator<Module> iter = this.modules.iterator(); iter.hasNext();)
        {
            Module module = iter.next();
            if (module.toString().equalsIgnoreCase(moduleName))
            {
                module.onUnload();
                iter.remove();
                return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.Success,
                        module.toString());
            }
        }
        return new SimpleEntry<ModuleLoaderResponse, String>(ModuleLoaderResponse.DoesNotExist, "");
    }

    @Override
    public void onPMMessage(PMMessageEvent event)
    {
        this.handleMessage(event.getServerName(), event.getUser(), null, event.getMessage(),
                TriggerType.Message);
    }

    @Override
    public void onPMAction(PMActionEvent event)
    {
        this.handleMessage(event.getServerName(), event.getUser(), null, event.getMessage(),
                TriggerType.Action);
    }

    @Override
    public void onMessage(MessageEvent event)
    {
        this.handleMessage(event.getServerName(), event.getUser(), event.getChannel(),
                event.getMessage(), TriggerType.Message);
    }

    @Override
    public void onAction(ActionEvent event)
    {
        this.handleMessage(event.getServerName(), event.getUser(), event.getChannel(),
                event.getMessage(), TriggerType.Action);
    }

    private void handleMessage(final String serverName, final IRCUser user,
            final IRCChannel channel, final String message, TriggerType triggerType)
    {
        for (String ignore : this.ignores)
        {
            if (user.getFullHost().matches(ignore))
            {
                return;
            }
        }

        Module[] listCopy = new Module[this.modules.size()];
        listCopy = this.modules.toArray(listCopy);
        for (final Module module : listCopy)
        {
            if ((message.toLowerCase().matches(module.getTrigger()) || module
                    .getTriggerOnEveryMessage())
                    && Arrays.asList(module.getTriggerTypes()).contains(triggerType))
            {
                final String target;
                if (channel == null)
                {
                    target = user.getNickname();
                }
                else
                {
                    target = channel.getName();
                }

                if (this.isAuthorized(module, user))
                {
                    if (module.getTriggerOnEveryMessage())
                    {
                        module.processEvent(target, message, user.getNickname(),
                                StringUtils.parseStringtoList(message, " "));
                    }
                    else if (Thread.activeCount() < this.bot.getServers().size() + 5)
                    {
                        // Thread limit might have to become a setting in the
                        // future
                        Thread thread = new Thread()
                        {
                            @Override
                            public void run()
                            {
                                module.processEvent(target, message, user.getNickname(),
                                        StringUtils.parseStringtoList(message, " "));
                            }
                        };
                        thread.start();
                    }
                    else
                    {
                        this.bot.getServer(serverName).cmdPRIVMSG(target,
                                "Calm down, " + user.getNickname() + "! Can't you see I'm busy?");
                    }
                }
                else
                {
                    this.bot.getServer(serverName).cmdPRIVMSG(
                            target,
                            "You are not authorized to use the \"" + module.toString()
                                    + "\" module!");
                }
            }
        }
    }

    public boolean isAuthorized(Module module, IRCUser user)
    {
        if (module.authType == Module.AuthType.Anyone)
        {
            return true;
        }
        else
        {
            ArrayList<String> botAdmins = this.bot.getServer(this.server).getConfig()
                    .getSettingWithDefault("botAdmins", new ArrayList<String>());
            for (String admin : botAdmins)
            {
                String regex = "^"
                        + StringUtils.escapeRegex(admin).replaceAll("\\*", ".*")
                                .replaceAll("\\?", ".").replaceAll("\\(", "(")
                                .replaceAll("\\)", ")").replaceAll(",", "|").replaceAll("/", "|")
                        + "$";

                if (user.getFullHost().matches(regex))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isModuleLoaded(String moduleName)
    {
        for (Module module : this.modules)
        {
            if (module.toString().equalsIgnoreCase(moduleName))
            {
                return true;
            }
        }
        return false;
    }

    public String getModuleHelp(String message)
    {
        for (Module module : this.modules)
        {
            String moduleTrigger = module.getTrigger();
            String commandPrefix = this.bot.getServer(this.server).getConfig()
                    .getSettingWithDefault("commandPrefix", "~");

            if (moduleTrigger.startsWith("^" + commandPrefix))
            {
                moduleTrigger = moduleTrigger.substring(moduleTrigger.indexOf(commandPrefix)
                        + commandPrefix.length());
            }

            if (module.toString().equalsIgnoreCase(message)
                    || message.toLowerCase().matches(moduleTrigger))
            {
                return module.getHelp(message.toLowerCase());
            }
        }
        return null;
    }

    public ArrayList<Module> getModuleList()
    {
        return this.modules;
    }

    public List<String> getIgnores()
    {
        return this.ignores;
    }

    public void setIgnores(List<String> ignores)
    {
        this.ignores = ignores;
    }
}
