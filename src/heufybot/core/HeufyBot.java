package heufybot.core;

import heufybot.config.GlobalConfig;
import heufybot.config.GlobalConfig.PasswordType;
import heufybot.config.ServerConfig;
import heufybot.core.cap.SASLCapHandler;
import heufybot.core.events.LoggingInterface;
import heufybot.modules.Module;
import heufybot.modules.ModuleInterface;
import heufybot.modules.ModuleInterface.ModuleLoaderResponse;
import heufybot.utils.FileUtils;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;

public class HeufyBot
{
    public final static String VERSION = "0.6.0";
    public final static int MODULE_API_VERSION = 60;

    private GlobalConfig config;
    private HashMap<String, IRCServer> servers;

    private static final HeufyBot instance = new HeufyBot();

    private HeufyBot()
    {
        FileUtils.touchDir("config");
        FileUtils.touchDir("data");
        FileUtils.touchDir("modules");

        this.servers = new HashMap<String, IRCServer>();
    }

    public void loadConfigs()
    {
        this.config = new GlobalConfig();
        this.config.loadGlobalConfig("config/globalconfig.yml");
        FileUtils.touchDir(this.config.getSettingWithDefault("logPath", "logs"));

        File[] folder = new File("config").listFiles();

        int foundServerConfigs = 0;
        for (File file : folder)
        {
            if (!file.getName().equals("globalconfig.yml") && file.getName().endsWith(".yml"))
            {
                // We found a config file. Assume it's a server config
                foundServerConfigs++;

                ServerConfig serverConfig = new ServerConfig();
                serverConfig.loadServerConfig(file.getPath(), this.config.getSettings());

                this.addServer(serverConfig);
            }
        }

        if (foundServerConfigs == 0)
        {
            ServerConfig serverConfig = new ServerConfig();
            serverConfig.loadServerConfig(null, this.config.getSettings());
            this.addServer(serverConfig);
        }
    }

    public void addServer(ServerConfig config)
    {
        String serverName = config.getSettingWithDefault("server", "irc.foo.bar");

        int serverID = 1;
        while (this.servers.containsKey(serverName + "-" + serverID))
        {
            serverID++;
        }

        serverName = serverName + "-" + serverID;
        IRCServer server = new IRCServer(serverName, config);
        this.servers.put(serverName, server);

        FileUtils.touchDir(config.getSettingWithDefault("logPath", "logs"));
        FileUtils.touchDir("data/" + serverName);

        ModuleInterface moduleInterface = new ModuleInterface(this, serverName);
        server.getEventListenerManager().addListener(moduleInterface);
        server.setModuleInterface(moduleInterface);
        this.loadModules(server);
    }

    public void start()
    {
        LoggingInterface loggingInterface = new LoggingInterface(this);

        for (IRCServer server : this.servers.values())
        {
            server.getEventListenerManager().addListener(loggingInterface);

            ServerConfig sConfig = server.getConfig();

            if (sConfig.getSettingWithDefault("passwordType", PasswordType.None) == PasswordType.SASL)
            {
                SASLCapHandler handler = new SASLCapHandler(sConfig.getSettingWithDefault(
                        "username", "RE_HeufyBot"), sConfig.getSettingWithDefault("password", ""));
                server.getConfig().getCapHandlers().add(handler);
            }

            if (server.connect(sConfig.getSettingWithDefault("server", "irc.foo.bar"),
                    sConfig.getSettingWithDefault("port", 6667)))
            {
                server.login();
            }
        }
    }

    public void stop(String message)
    {
        for (IRCServer server : this.servers.values())
        {
            server.cmdQUIT(message);
            server.disconnect(false);

            this.unloadModules(server);
        }
        this.servers.clear();
        Logger.log("*** Stopping...");
    }

    public void restart()
    {
        // Disconnect from the server
        this.stop("Restarting...");

        // Reload config and reconnect
        this.loadConfigs();
        this.start();
    }

    public void loadModules(IRCServer server)
    {
        Logger.log("*** Loading modules...");

        for (String module : server.getConfig().getSettingWithDefault("modules",
                new ArrayList<String>()))
        {
            SimpleEntry<ModuleLoaderResponse, String> result = server.getModuleInterface()
                    .loadModule(module);

            switch (result.getKey())
            {
                case Success:
                    Logger.log(" -  Module \"" + result.getValue() + "\" was loaded");
                    break;
                case AlreadyLoaded:
                    Logger.error("Module Loader", "Module \"" + module + "\" is already loaded");
                    break;
                case DoesNotExist:
                    Logger.error("Module Loader", "Module \"" + module + "\" does not exist");
                    break;
                case APIVersionDoesNotMatch:
                    String moduleVersion = result.getValue().split(" ")[0];
                    String apiVersion = result.getValue().split(" ")[1];
                    Logger.error("Module Loader", "Module \"" + module
                            + "\" could not be loaded. Its module API version (" + moduleVersion
                            + ") does not match the bot's API version (" + apiVersion + ")");
                    break;
                default:
                    break;
            }
        }
    }

    public void unloadModules(IRCServer server)
    {
        Logger.log("*** Unloading modules...");

        Module[] loadedModules = new Module[server.getModuleInterface().getModuleList().size()];
        loadedModules = server.getModuleInterface().getModuleList().toArray(loadedModules);

        for (Module module : loadedModules)
        {
            SimpleEntry<ModuleLoaderResponse, String> result = server.getModuleInterface()
                    .unloadModule(module.toString());

            switch (result.getKey())
            {
                case Success:
                    Logger.log(" -  Module " + result.getValue() + " was unloaded");
                    break;
                case DoesNotExist:
                    // If for whatever reason a loaded module doesn't exist
                    Logger.error("Module Loader", "Module " + module
                            + " is already unloaded or does not exist");
                    break;
                default:
                    break;
            }
        }
    }

    public IRCServer getServer(String name)
    {
        return this.servers.get(name);
    }

    public HashMap<String, IRCServer> getServers()
    {
        return this.servers;
    }

    public static HeufyBot getInstance()
    {
        return instance;
    }

    public GlobalConfig getGlobalConfig()
    {
        return this.config;
    }
}
