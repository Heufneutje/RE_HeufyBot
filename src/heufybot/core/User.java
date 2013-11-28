package heufybot.core;

public class User 
{
	private String nickname;
	private String login;
	private String hostmask;
	
	public User(String nickname, String login, String hostmask)
	{
		this.nickname = nickname;
		this.login = login;
		this.hostmask = hostmask;
	}
	
	public String getNickname()
	{
		return nickname;
	}
	
	public String getLogin()
	{
		return login;
	}
	
	public String getHostmask()
	{
		return hostmask;
	}
}