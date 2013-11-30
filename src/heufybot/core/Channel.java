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
	private String key;
	private int limit;
	
	public Channel(String name)
	{
		this.name = name;
		this.modes = "";
		this.usersInChannel = new HashMap<User, String>();
		this.topic = "";
		this.topicSetter = "";
		this.key = "";
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getModes()
	{
		return modes;
	}
	
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
				modes = modes + curChar;
			}
			else
			{
				String parsedString = Character.toString(curChar);
				modes = modes.replace(parsedString, "");
			}
		}
	}
	
	public void parseModeChangeOnUser(User user, String modeChange)
	{
		String modesOnUser = usersInChannel.get(user);
		
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
				modesOnUser += curChar;
			}
			else
			{
				String parsedString = Character.toString(curChar);
				modesOnUser = modesOnUser.replace(parsedString, "");
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

	public String getKey() 
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public int getLimit()
	{
		return limit;
	}

	public void setLimit(int limit)
	{
		this.limit = limit;
	}
}