package heufybot.core.events;

import java.util.Date;

import heufybot.core.Channel;
import heufybot.core.HeufyBot;
import heufybot.core.Logger;
import heufybot.core.User;
import heufybot.core.events.types.*;
import heufybot.utils.StringUtils;
import heufybot.utils.WhoisBuilder;

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
		
		if(channel == null)
		{
			if(user.getNickname().equalsIgnoreCase("nickserv") && user.getNickname().equalsIgnoreCase("chanserv"))
			{
				return;
			}
			
			String sourceNick = user.getNickname();
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
		Logger.log("-- " + inviter.getNickname() + " (" + inviter.getLogin() + "@" + inviter.getHostname() + ") invites " + event.getInvitee() + " to join " + event.getChannel());
	}
	
	public void onJoin(JoinEvent event)
	{
		User user = event.getUser();
		Logger.log(">> " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostname() + ") has joined " + event.getChannel().getName(), event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
	}
	
	public void onKick(KickEvent event)
	{
		Logger.log("-- " + event.getRecipient().getNickname() + " was kicked from " + event.getChannel().getName() + " by " + event.getKicker().getNickname() + " (" + event.getMessage() + ")", event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
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
			Logger.log("-- " + event.getSetter() + " sets mode: " + event.getMode());
		}
		else
		{
			Logger.log("-- " + event.getSetter() + " sets mode: " + event.getMode(), event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
		}
	}
	
	public void onNickChange(NickChangeEvent event)
	{
		for(Channel channel : bot.getIRC().getChannels())
		{
			User user = channel.getUser(event.getUser().getNickname());
			if(user != null)
			{
				Logger.log("-- " + event.getOldNick() + " is now known as " + event.getNewNick(), channel.getName(), bot.getIRC().getServerInfo().getNetwork());
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
			Logger.log("<< " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostname() + ") has left " + channel, channel, bot.getIRC().getServerInfo().getNetwork());
		}
		else
		{
			Logger.log("<< " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostname() + ") has left " + channel + " (" + event.getMessage() + ")", channel, bot.getIRC().getServerInfo().getNetwork());
		}
	}
	
	public void onPMAction(PMActionEvent event)
	{
		if(bot.getConfig().getLogPMs())
		{
			Logger.log("* " + event.getUser().getNickname() + " " + event.getMessage(), event.getUser().getNickname(), bot.getIRC().getServerInfo().getNetwork());
		}
	}
	
	public void onPMMessage(PMMessageEvent event)
	{
		if(bot.getConfig().getLogPMs())
		{
			String sourceNick = event.getUser().getNickname();
			Logger.log("<" + sourceNick + "> " + event.getMessage(), sourceNick, bot.getIRC().getServerInfo().getNetwork());
		}
	}
	
	public void onQuit(QuitEvent event)
	{
		for(Channel channel : bot.getIRC().getChannels())
		{
			User user = channel.getUser(event.getUser().getNickname());
			if(user != null)
			{
				Logger.log("<< " + user.getNickname() + " (" + user.getLogin() + "@" + user.getHostname() + ") has quit IRC (" + event.getMessage() + ")", channel.getName(), bot.getIRC().getServerInfo().getNetwork());
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
		Logger.log("-- " + event.getSource() + " changes topic to \'" + event.getMessage() + "\'", event.getChannel().getName(), bot.getIRC().getServerInfo().getNetwork());
	}
	
	public void onWhois(WhoisEvent event)
	{
		WhoisBuilder builder = event.getWhoisBuilder();
		
		Logger.log("-- Start of /WHOIS");
		Logger.log(builder.getNickname() + " is " + builder.getNickname() + "!" + builder.getLogin() + "@" + builder.getHostname());
		Logger.log(builder.getNickname() + "'s real name is " + builder.getRealname());
		Logger.log(builder.getNickname() + "'s channels: " + StringUtils.join(builder.getChannels(), ", "));
		Logger.log(builder.getNickname() + "'s server is " + builder.getServer() + " - " + builder.getServerInfo());
		
		if(builder.getOperPrivs() != null)
		{
			Logger.log(builder.getNickname() + " " + builder.getOperPrivs());
		}
		
		Logger.log(builder.getNickname() + "'s idle time is " + builder.getIdleSeconds() + " seconds");
		
		if(builder.getSignOnTime() != -1)
		{
			Logger.log(builder.getNickname() + "'s sign on time is " + new Date(builder.getSignOnTime() * 1000));
		}
		
		Logger.log("-- End of /WHOIS");
	}
}
