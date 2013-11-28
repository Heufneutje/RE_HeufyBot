package heufybot.core;

import java.util.HashMap;
import java.util.Set;

public class Channel
{
	private String name;
	private String modes;
	private HashMap<User, String> usersInChannel;
	private String topic;
	private String topicSetter;
	private long topicSetTimestamp;
	
	public Channel(String name)
	{
		this.name = name;
		this.usersInChannel = new HashMap<User, String>();
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getModes()
	{
		return modes;
	}
	
	public void setModes(String modes)
	{
		this.modes = modes;
	}
	
	public void parseModeChange(String modeChange)
	{
		if(modeChange.startsWith("+"))
		{
			modes += modeChange.substring(1);
		}
		else
		{
			char[] changedModes = modeChange.substring(1).toCharArray();
			for(int i = 0; i < changedModes.length; i++)
			{
				modes.replaceFirst("" + changedModes[i], "");
			}
		}
	}
	
	public void parseModeChangeOnUser(User user, String modeChange)
	{
		String modesOnUser = usersInChannel.get(user);
		
		if(modeChange.startsWith("+"))
		{
			modesOnUser += modeChange.substring(1);
		}
		else
		{	
			char[] changedModes = modeChange.substring(1).toCharArray();
			for(int i = 0; i < changedModes.length; i++)
			{
				modesOnUser.replaceFirst("" + changedModes[i], "");
			}
		}
		
		usersInChannel.put(user, modesOnUser);
	}
	
	public void addUser(User user)
	{
		usersInChannel.put(user, "");
	}
	
	public void removeUser(User user)
	{
		usersInChannel.remove(user);
	}
	
	public String getModesOnUser(User user)
	{
		return usersInChannel.get(user);
	}
	
	public User[] getUsers()
	{
		Set<User> userSet = usersInChannel.keySet();
		User[] userArray = new User[userSet.size()];
		return userSet.toArray(userArray);
	}
	
	public User getUser(String nickname)
	{
		for(User user : usersInChannel.keySet())
		{
			if(user.getNickname().equalsIgnoreCase(nickname))
			{
				return user;
			}
		}
		return null;
	}

	public String getTopic()
	{
		return topic;
	}

	public void setTopic(String topic) 
	{
		this.topic = topic;
	}

	public String getTopicSetter() 
	{
		return topicSetter;
	}

	public void setTopicSetter(String topicSetter) 
	{
		this.topicSetter = topicSetter;
	}

	public long getTopicSetTimestamp() 
	{
		return topicSetTimestamp;
	}

	public void setTopicSetTimestamp(long topicSetTimestamp) 
	{
		this.topicSetTimestamp = topicSetTimestamp;
	}
}