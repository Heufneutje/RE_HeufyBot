package heufybot.core;

import java.util.HashMap;
import java.util.Set;

/**
 * This class represents a joined channel on the IRC server the bot is connected
 * to.
 *
 * @author Stefan "Heufneutje" Frijters
 */
public class IRCChannel
{
    private String name;
    private HashMap<IRCUser, String> usersInChannel;
    private String topic;
    private String topicSetter;
    private long topicSetTimestamp;
    private HashMap<String, String> modes;

    public IRCChannel(String name)
    {
        this.name = name;
        this.usersInChannel = new HashMap<IRCUser, String>();
        this.topic = "";
        this.topicSetter = "";
        this.modes = new HashMap<String, String>();
    }

    /**
     * @return The name of the channel (prefixed by a channel type, most
     * commonly #).
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Parses a string of mode changes that are being set or unset on this
     * channel.
     *
     * @param modeChange The string of modes that is to be parsed.
     */
    public void parseModeChange(String modeChange)
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
                if (!this.modes.containsKey(current))
                {
                    this.modes.put(current, "");
                }
            }
            else
            {
                String current = Character.toString(curChar);
                if (this.modes.containsKey(current))
                {
                    this.modes.remove(current);
                }
            }
        }
    }

    /**
     * Parses status modes that are being set or unset on users in the channel.
     *
     * @param user       The user that these status mode changes affect.
     * @param modeChange The string of modes that is to be set on the user.
     */
    public void parseModeChangeOnUser(IRCUser user, String modeChange)
    {
        String modesOnUser = this.usersInChannel.get(user);

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
                if (!modesOnUser.contains(Character.toString(curChar)))
                {
                    modesOnUser += curChar;
                }
            }
            else
            {
                String parsedString = Character.toString(curChar);
                modesOnUser = modesOnUser.replace(parsedString, "");
            }
        }
        this.usersInChannel.put(user, modesOnUser);
    }

    /**
     * Adds a user to this channel.
     *
     * @param user The user to be added.
     */
    public void addUser(IRCUser user)
    {
        this.usersInChannel.put(user, "");
    }

    /**
     * Removes a user from this channel.
     *
     * @param user The user to be removed.
     */
    public void removeUser(IRCUser user)
    {
        this.usersInChannel.remove(user);
    }

    /**
     * Gets all status modes set on a given user in the channel.
     *
     * @param user The user status modes need to be retrieved from.
     * @return A string of status modes set on the given user.
     */
    public String getModesOnUser(IRCUser user)
    {
        return this.usersInChannel.get(user);
    }

    /**
     * Used to show all users that are currently in this channel.
     *
     * @return An array of all current users in the channel.
     */
    public IRCUser[] getUsers()
    {
        Set<IRCUser> userSet = this.usersInChannel.keySet();
        IRCUser[] userArray = new IRCUser[userSet.size()];
        return userSet.toArray(userArray);
    }

    /**
     * This is mainly used to see whether a user is currently in this channel.
     *
     * @param nickname The nickname of the user to be checked.
     * @return The user that the given nickname belongs to if they are in the
     * channel, otherwise null.
     */
    public IRCUser getUser(String nickname)
    {
        for (IRCUser user : this.usersInChannel.keySet())
        {
            if (user.getNickname().equalsIgnoreCase(nickname))
            {
                return user;
            }
        }
        return null;
    }

    /**
     * Used to get the channel's current topic.
     *
     * @return The current topic.
     */
    public String getTopic()
    {
        return this.topic;
    }

    /**
     * Changes the channel's topic
     *
     * @param topic The new topic to be set
     */
    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    /**
     * Returns the nickname of the user that set the channel's topic.
     *
     * @return The topic setter.
     */
    public String getTopicSetter()
    {
        return this.topicSetter;
    }

    /**
     * Changes the last person who set the channel's topic. Generally called
     * when setting a new topic.
     *
     * @param topicSetter The new topic setter.
     */
    public void setTopicSetter(String topicSetter)
    {
        this.topicSetter = topicSetter;
    }

    /**
     * Returns a timestamp of the date when this channel's topic was set.
     *
     * @return The topic timestamp.
     */
    public long getTopicSetTimestamp()
    {
        return this.topicSetTimestamp;
    }

    /**
     * Changes the timestamp of when the channel's topic was set. Generally
     * called when setting a new topic.
     *
     * @param topicSetTimestamp
     */
    public void setTopicSetTimestamp(long topicSetTimestamp)
    {
        this.topicSetTimestamp = topicSetTimestamp;
    }

    /**
     * Returns all modes set on the channel.
     *
     * @return A HashMap of all set modes. The keys are the mode characters. The
     * values are the mode parameters that go with them.
     */
    public HashMap<String, String> getModes()
    {
        return this.modes;
    }
}
