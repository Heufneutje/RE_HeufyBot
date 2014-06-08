package heufybot.core;

import heufybot.config.GlobalConfig.PasswordType;
import heufybot.config.ServerConfig;
import heufybot.core.events.EventListenerManager;
import heufybot.core.events.types.BotMessageEvent;
import heufybot.modules.ModuleInterface;
import heufybot.utils.SSLSocketUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.SocketFactory;

public class IRCServer
{
    // Not sure what to do with this one since the RFC doesn't specify it.
    // Assume 512 until documentation states otherwise.
    private final int MAX_LINE_LENGTH = 512;

    public enum ConnectionState
    {
        Initializing, Connected, Disconnected
    }

    private String name;
    private ServerConfig config;
    private ModuleInterface moduleInterface;

    private Socket socket;
    private BufferedReader inputReader;
    private OutputStreamWriter outputWriter;
    private Thread inputThread;
    private InputParser inputParser;
    private ConnectionState connectionState;
    private ArrayList<IRCChannel> channels;
    private ArrayList<IRCUser> users;
    private ServerInfo serverInfo;
    private List<String> userModes;
    private List<String> enabledCapabilities;
    private EventListenerManager eventListenerManager;

    // Locking stuff for the output to server
    private final ReentrantLock writeLock = new ReentrantLock(true);
    private final Condition writeNowCondition = this.writeLock.newCondition();
    private long lastSentLine = 0;

    private String nickname;

    public IRCServer(String name, ServerConfig config)
    {
        this.socket = new Socket();
        this.inputParser = new InputParser(this);
        this.connectionState = ConnectionState.Initializing;

        this.channels = new ArrayList<IRCChannel>();
        this.users = new ArrayList<IRCUser>();

        this.serverInfo = new ServerInfo();
        this.enabledCapabilities = new ArrayList<String>();
        this.eventListenerManager = new EventListenerManager();
        this.userModes = new ArrayList<String>();

        this.name = name;
        this.config = config;
        this.nickname = "";
    }

    public boolean connect(String server, int port)
    {
        if (this.connectionState == ConnectionState.Connected)
        {
            Logger.error("IRC Connect", "Already connected to a server. Connection failed.");
            return false;
        }

        SocketFactory sf;
        if (this.config.getSettingWithDefault("ssl", false))
        {
            // Trust all certificates, since making Java recognize a valid
            // certificate is annoying.
            sf = new SSLSocketUtils().trustAllCertificates();
        }
        else
        {
            sf = SocketFactory.getDefault();
        }
        Logger.log("*** Trying to connect to " + server + "...");

        try
        {
            InetAddress[] foundIPs = InetAddress.getAllByName(server);
            Logger.log("*** " + foundIPs.length + " IP(s) found for host " + server + ".");
            for (InetAddress curAddress : foundIPs)
            {
                try
                {
                    Logger.log("*** Trying IP address " + curAddress.getHostAddress() + ":" + port
                            + "...");
                    this.socket = sf.createSocket(curAddress, port, null, 0);
                    this.inputReader = new BufferedReader(new InputStreamReader(
                            this.socket.getInputStream(), Charset.forName("UTF-8")));
                    this.outputWriter = new OutputStreamWriter(this.socket.getOutputStream(),
                            Charset.forName("UTF-8"));
                    Logger.log("*** Connected to the server.");
                    return true;
                }
                catch (Exception e)
                {
                    Logger.error("IRC Connect", "Could not connect to " + server
                            + " on IP address " + curAddress);
                }
            }
            Logger.error("IRC Connect", "Could not connect to " + server);
            return false;
        }
        catch (UnknownHostException e)
        {
            Logger.error("IRC Connect", "Host could not be resolved. Connection failed.");
            return false;
        }
    }

    public void login()
    {
        String nickname = this.config.getSettingWithDefault("nickname", "RE_HeufyBot");
        String password = this.config.getSettingWithDefault("password", "");
        String username = this.config.getSettingWithDefault("username", "RE_HeufyBot");
        String realname = this.config.getSettingWithDefault("realname", "RE_HeufyBot IRC Bot");
        PasswordType passwordType = this.config.getSettingWithDefault("passwordType",
                PasswordType.None);

        if (passwordType == PasswordType.ServerPass)
        {
            this.cmdPASS(password);
        }

        this.cmdCAP("LS", "");

        this.cmdNICK(nickname);
        this.cmdUSER(username, realname);

        this.startProcessing();
    }

    public void disconnect(boolean reconnect)
    {
        this.connectionState = ConnectionState.Disconnected;
        this.nickname = "";
        this.serverInfo.clear();
        this.enabledCapabilities.clear();
        this.userModes.clear();

        try
        {
            this.inputThread.interrupt();
            this.inputReader.close();
            this.outputWriter.flush();
            this.outputWriter.close();
            this.socket.close();

            this.channels.clear();
            this.users.clear();
        }
        catch (IOException e)
        {
            Logger.error("IRC Disconnect", "Error closing connection");
        }

        if (reconnect && this.config.getSettingWithDefault("autoReconnect", false))
        {
            this.reconnect();
        }
    }

    public void reconnect()
    {
        // TODO This code needs a major overhaul, because it doesn't work
        // properly and multiserver will break it even more
        int reconnectAttempts = this.config.getSettingWithDefault("reconnectAttempts", 3);
        int reconnectInterval = this.config.getSettingWithDefault("reconnectInterval", 600);
        String server = this.config.getSettingWithDefault("server", "irc.foo.bar");
        int port = this.config.getSettingWithDefault("port", 6667);

        int reconnects = 0;
        while (reconnects < reconnectAttempts)
        {
            reconnects++;
            Logger.log("*** Reconnection attempt #" + reconnects + "...");
            boolean success = this.connect(server, port);
            if (success)
            {
                this.login();
                return;
            }
            else
            {
                if (reconnects < reconnectAttempts)
                {
                    Logger.log("*** Connection failed. Trying again in " + reconnectInterval
                            + " second(s)...");
                    try
                    {
                        Thread.sleep(reconnectInterval * 1000);
                    }
                    catch (InterruptedException e)
                    {
                        Logger.error("IRC - Reconnect",
                                "Thread interrupted while trying to reconnect");
                    }
                }
            }
        }
        Logger.log("*** Connection failed. Giving up.");
    }

    public void startProcessing()
    {
        this.inputThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    String line;
                    try
                    {
                        line = IRCServer.this.inputReader.readLine();
                    }
                    catch (InterruptedIOException iioe)
                    {
                        IRCServer.this.cmdPING("" + System.currentTimeMillis() / 1000);
                        continue;
                    }
                    catch (Exception e)
                    {
                        line = null;
                        Logger.log("*** Connection to the server was lost. Trying to reconnect...");
                        IRCServer.this.disconnect(true);
                    }

                    if (line == null)
                    {
                        break;
                    }

                    if (!line.equals(""))
                    {
                        IRCServer.this.inputParser.parseLine(line);
                    }

                    if (Thread.interrupted())
                    {
                        return;
                    }
                }
            }
        });
        this.inputThread.start();
    }

    public void sendRaw(String line)
    {
        int messageDelay = this.config.getSettingWithDefault("messageDelay", 500);

        this.writeLock.lock();
        try
        {
            long currentNanos = System.nanoTime();
            while (this.lastSentLine + messageDelay * 1000000 > currentNanos)
            {
                this.writeNowCondition.await(this.lastSentLine + messageDelay * 1000000
                        - currentNanos, TimeUnit.NANOSECONDS);
                currentNanos = System.nanoTime();
            }
            this.lastSentLine = System.nanoTime();
            this.outputWriter.write(line + "\r\n");
            this.outputWriter.flush();
        }
        catch (IOException e)
        {
            Logger.error("IRC Output", "Error sending line");
        }
        catch (InterruptedException e)
        {
            Logger.error("IRC Output", "Error while waiting to send line");
        }
        finally
        {
            this.writeLock.unlock();
        }
    }

    public void sendRawNow(String line)
    {
        this.writeLock.lock();
        try
        {
            this.lastSentLine = System.nanoTime();
            this.outputWriter.write(line + "\r\n");
            this.outputWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Logger.error("IRC Output", "Error sending line");
        }
        finally
        {
            this.writeLock.unlock();
        }
    }

    public void sendRawSplit(String prefix, String message, String suffix)
    {
        String fullMessage = prefix + message + suffix;
        if (fullMessage.length() < this.MAX_LINE_LENGTH - 2)
        {
            this.sendRaw(fullMessage);
            return;
        }

        int maxLength = this.MAX_LINE_LENGTH - 2 - (prefix + suffix).length();
        int iterations = (int) Math.ceil(message.length() / (double) maxLength);
        for (int i = 0; i < iterations; i++)
        {
            int endPoint = i != iterations - 1 ? (i + 1) * maxLength : message.length();
            String currentPart = prefix + message.substring(i * maxLength, endPoint) + suffix;
            this.sendRaw(currentPart);
        }
    }

    public void sendRawSplit(String prefix, String message)
    {
        this.sendRawSplit(prefix, message, "");
    }

    public void cmdNICK(String nick)
    {
        this.sendRawNow("NICK " + nick);
    }

    public void cmdUSER(String user, String realname)
    {
        this.sendRawNow("USER " + user + " 8 * :" + realname);
    }

    public void cmdPING(String ping)
    {
        this.sendRawNow("PING " + ping);
    }

    public void cmdPONG(String response)
    {
        this.sendRawNow("PONG " + response);
    }

    public void cmdQUIT(String message)
    {
        this.sendRaw("QUIT :" + message);
    }

    public void cmdPRIVMSG(String target, String message)
    {
        this.sendRawSplit("PRIVMSG " + target + " :", message);
        this.eventListenerManager.dispatchEvent(new BotMessageEvent(this.name, this
                .getUser(this.nickname), target, message));
    }

    public void cmdJOIN(String channel, String key)
    {
        this.sendRaw("JOIN " + channel + " " + key);
    }

    public void cmdPART(String channel, String message)
    {
        this.sendRaw("PART " + channel + " :" + message);
    }

    public void cmdPASS(String password)
    {
        this.sendRaw("PASS " + password);
    }

    public void cmdWHO(String target)
    {
        this.sendRaw("WHO " + target);
    }

    public void cmdWHOIS(String target)
    {
        this.sendRaw("WHOIS " + target);
    }

    public void cmdWHOWAS(String target)
    {
        this.sendRaw("WHOWAS " + target);
    }

    public void cmdMODE(String target, String mode)
    {
        this.sendRaw("MODE " + target + " " + mode);
    }

    public void cmdNOTICE(String target, String notice)
    {
        this.sendRawSplit("NOTICE " + target + " :", notice);
    }

    public void cmdCAP(String capCommand, String arguments)
    {
        this.sendRawNow("CAP " + capCommand + arguments);
    }

    public void cmdACTION(String target, String action)
    {
        this.ctcpCommand(target, "ACTION " + action);
    }

    public void nickservIdentify(String password)
    {
        this.cmdPRIVMSG("NickServ", "IDENTIFY " + password);
    }

    public void ctcpCommand(String target, String command)
    {
        this.sendRawSplit("PRIVMSG " + target + " :\u0001", command, "\u0001");
    }

    public void ctcpReply(String target, String replyType, String reply)
    {
        this.cmdNOTICE(target, "\u0001" + replyType + " " + reply + "\u0001");
    }

    public String getAccessLevelOnUser(IRCChannel channel, IRCUser user)
    {
        for (String accessLevel : this.serverInfo.getUserPrefixes().keySet())
        {
            if (channel.getModesOnUser(user).contains(accessLevel))
            {
                return accessLevel;
            }
        }
        return "";
    }

    public IRCChannel getChannel(String channelName)
    {
        for (IRCChannel channel : this.channels)
        {
            if (channel.getName().equalsIgnoreCase(channelName))
            {
                return channel;
            }
        }
        return null;
    }

    public IRCUser getUser(String nickname)
    {
        for (IRCUser user : this.users)
        {
            if (user.getNickname().equalsIgnoreCase(nickname))
            {
                return user;
            }
        }

        IRCUser user = new IRCUser(nickname);
        this.users.add(user);
        return user;
    }

    public String getName()
    {
        return this.name;
    }

    public ServerConfig getConfig()
    {
        return this.config;
    }

    public ModuleInterface getModuleInterface()
    {
        return this.moduleInterface;
    }

    public void setModuleInterface(ModuleInterface moduleInterface)
    {
        this.moduleInterface = moduleInterface;
    }

    public ConnectionState getConnectionState()
    {
        return this.connectionState;
    }

    public void setConnectionState(ConnectionState connectionState)
    {
        this.connectionState = connectionState;
    }

    public String getNickname()
    {
        return this.nickname;
    }

    public void setLoggedInNick(String nickname)
    {
        this.nickname = nickname;
    }

    public ArrayList<IRCChannel> getChannels()
    {
        return this.channels;
    }

    public ArrayList<IRCUser> getUsers()
    {
        return this.users;
    }

    public ServerInfo getServerInfo()
    {
        return this.serverInfo;
    }

    public List<String> getEnabledCapabilities()
    {
        return this.enabledCapabilities;
    }

    public EventListenerManager getEventListenerManager()
    {
        return this.eventListenerManager;
    }

    public List<String> getUserModes()
    {
        return this.userModes;
    }

    public void parseUserModesChange(String modeChange)
    {
        boolean adding = true;
        for (char curChar : modeChange.toCharArray())
        {
            if (curChar == '-')
            {
                adding = false;
            }
            else if (curChar == '+')
            {
                adding = true;
            }
            else if (adding)
            {
                String current = Character.toString(curChar);
                if (!this.userModes.contains(current))
                {
                    this.userModes.add(current);
                }
            }
            else
            {
                String current = Character.toString(curChar);
                if (this.userModes.contains(current))
                {
                    this.userModes.remove(current);
                }
            }
        }
    }
}
