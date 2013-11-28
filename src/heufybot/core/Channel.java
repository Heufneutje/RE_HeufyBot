package heufybot.core;

import java.util.HashMap;
import java.util.Set;

public class Channel
{
	private String name;
	private String modes;
	private HashMap<User, String> usersInChannel;
	
	public Channel(String name)
	{
		this.name = name;
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
	
	public void addUser(User user, String modes)
	{
		usersInChannel.put(user, modes);
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
}