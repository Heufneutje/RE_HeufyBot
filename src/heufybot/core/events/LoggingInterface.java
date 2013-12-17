package heufybot.core.events;

import heufybot.core.Channel;
import heufybot.core.HeufyBot;
import heufybot.core.Logger;
import heufybot.core.User;
import heufybot.core.events.types.*;

public class LoggingInterface extends EventListenerAdapter
{
	private HeufyBot bot;
	
	public LoggingInterface(HeufyBot bot)
	{
		this.bot = bot;
	}
	
	public void onAction(ActionEvent event)
	{
		Logger.log("* " + event.getUser().getNickname() + " " + event.getMessage(), event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
	}
	
	public void onBotMessage(BotMessageEvent event) 
	{
		User user = event.getUser();
		Channel channel = bot.getIRC().getChannel(event.getTarget());
		
		if(channel == null && event.getUser() == null)
		{
			//Don't log this. It was probably a message to an authentication service
			return;
		}
		else if(channel == null)
		{
			String sourceNick = event.getUser().getNickname();
			Logger.log("<" + sourceNick + "> " + event.getMessage(), event.getTarget(), bot.getIRC().getServerInfo().getNetwork());
			return;
		}
				
		String modes = channel.getModesOnUser(user);
		if(!modes.equals(""))
		{
			Logger.log("<" + bot.getIRC().getServerInfo().getUserPrefixes().get(bot.getIRC().getAccessLevelOnUser(channel, user)) + user.getNickname() + "> " + event.getMessage(), channel.getName(), bot.getIRC().getServerInfo().getNetwork());
		}
		else
		{
			Logger.log("<" + user.getNickname() + "> " + event.getMessage(), channel.getName(), bot.getIRC().getServerInfo().getNetwork());
		}
	}
	
	public void onChannelNotice(ChannelNoticeEvent event)
	{
		Logger.log("[Notice] --" + event.getSource() + "-- [" + event.getChannel().getName() + "] " + event.getMessage(), event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
	}
	
	public void onCTCPRequest(CTCPRequestEvent event)
	{
		Logger.log("[" + event.getUser().getNickname() + " " + event.getType() + "]");
	}
	
	public void onError(ErrorEvent event)
	{
		Logger.log(event.getMessage());
	}
	
	public void onInvite(InviteEvent event)
	{
		User inviter = event.getInviter();
		Logger.log(inviter.getNickname() + " (" + inviter.getLogin() + "@" + inviter.getHostmask() + ") invites " + event.getInvitee() + " to join " + event.getChannel());
	}
	
	public void onJoin(JoinEvent event)
	{
		User user = event.getUser();
		Logger.log(">> " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostmask() + ") has joined " + event.getChannel().getName(), event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
	}
	
	public void onKick(KickEvent event)
	{
		Logger.log(event.getRecipient().getNickname() + " was kicked from " + event.getChannel().getName() + " by " + event.getKicker().getNickname() + " (" + event.getMessage() + ")", event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
	}
	
	public void onMessage(MessageEvent event)
	{
		User user = event.getUser();
		Channel channel = event.getChannel();
		
		String modes = channel.getModesOnUser(user);
		if(!modes.equals(""))
		{
			Logger.log("<" + bot.getIRC().getServerInfo().getUserPrefixes().get(bot.getIRC().getAccessLevelOnUser(channel, user)) + user.getNickname() + "> " + event.getMessage(), channel.getName(), bot.getIRC().getServerInfo().getNetwork());
		}
		else
		{
			Logger.log("<" + user.getNickname() + "> " + event.getMessage(), channel.getName(), bot.getIRC().getServerInfo().getNetwork());
		}
	}
	
	public void onModeChange(ModeEvent event)
	{
		if(event.getChannel() == null)
		{
			Logger.log(event.getSetter() + " sets mode: " + event.getMode());
		}
		else
		{
			Logger.log(event.getSetter() + " sets mode: " + event.getMode(), event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
		}
	}
	
	public void onNickChange(NickChangeEvent event)
	{
		for(Channel channel : bot.getIRC().getChannels())
		{
			User user = channel.getUser(event.getUser().getNickname());
			if(user != null)
			{
				Logger.log(event.getOldNick() + " is now known as " + event.getNewNick(), channel.getName(), bot.getIRC().getServerInfo().getNetwork());
			}
		}
	}
	
	public void onNotice(NoticeEvent event)
	{
		Logger.log("[Notice] --" + event.getSource() + "-- " + event.getMessage());
	}
	
	public void onPart(PartEvent event)
	{
		User user = event.getUser();
		String channel = event.getChannel().getName();
		
		if(event.getMessage().equals(""))
		{
			Logger.log("<< " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostmask() + ") has left " + channel, channel, bot.getIRC().getServerInfo().getNetwork());
		}
		else
		{
			Logger.log("<< " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostmask() + ") has left " + channel + " (" + event.getMessage() + ")", channel, bot.getIRC().getServerInfo().getNetwork());
		}
	}
	
	public void onPMAction(PMActionEvent event)
	{
		Logger.log("* " + event.getUser().getNickname() + " " + event.getMessage(), event.getUser().getNickname(), bot.getIRC().getServerInfo().getNetwork());
	}
	
	public void onPMMessage(PMMessageEvent event)
	{
		String sourceNick = event.getUser().getNickname();
		Logger.log("<" + sourceNick + "> " + event.getMessage(), sourceNick, bot.getIRC().getServerInfo().getNetwork());
	}
	
	public void onQuit(QuitEvent event)
	{
		for(Channel channel : bot.getIRC().getChannels())
		{
			User user = channel.getUser(event.getUser().getNickname());
			if(user != null)
			{
				Logger.log("<< " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostmask() + ") Quit (" + event.getMessage() + ")", channel.getName(), bot.getIRC().getServerInfo().getNetwork());
			}
		}
	}
	
	public void onServerChannelResponse(ServerResponseChannelEvent event)
	{
		Logger.log(event.getLine(), event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
	}
	
	public void onServerResponse(ServerResponseEvent event)
	{
		Logger.log(event.getLine());
	}
	
	public void onTopicChange(TopicEvent event)
	{
		Logger.log(event.getSource() + " changes topic to \'" + event.getMessage() + "\'", event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
	}
}
