package com.those45ninjas.gduAuth;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.util.regex.Matcher;

import com.those45ninjas.gduAuth.Authorization;
import com.those45ninjas.gduAuth.Authorization.Status;
import com.those45ninjas.gduAuth.database.User;

public class GduListener implements Listener
{
	GduAuth plugin;
	public GduListener(GduAuth gduAuth)
	{
		plugin = gduAuth;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Bukkit.broadcastMessage("Hello World!");
	}
	
	@EventHandler
	public void onAsyncPlayerPrelogin(AsyncPlayerPreLoginEvent player) throws Exception
	{
		// A player is trying to join.
		
		// First, check to see if the server has a whitelist and if the player is the white-list.
		if(Bukkit.hasWhitelist() && Bukkit.getWhitelistedPlayers().contains(player))
		{
			// Let the player join.
			player.allow();
			return;
		}
		try
		{
			// Get the status of the player.
			Status state = plugin.auth.Check(player);
			plugin.getLogger().info("User state: " + state);

			if(state == Status.ALLOWED)
			{
				// Wooh, the player is allowed in!
				player.allow();
				return;
			}

			throw new Exception("Something went terribly wrong.");
		}
		catch (Exception e)
		{
			player.disallow(Result.KICK_OTHER, CreateFaultMessage(e));
			throw e;
		}
	}

	String CreateStartMessage(String username, String mixerCode)
	{
		String msg = plugin.getConfig().getString("messages.start", "Welcome ::user::, Please enter this six digit code into https://mixer.com/go\n::code::");
		
		msg = msg.replaceAll("::user::", Matcher.quoteReplacement(username));
		msg = msg.replaceAll("::code::", mixerCode.replaceAll(".(?=.)", "$0 "));
		
		return msg;
	}

	String CreateExpiredMessage(String mixerCode)
	{
		String msg = plugin.getConfig().getString("messages.code-expired", "Your previous code has expired. Here's your new one.\n::code::");
		msg = msg.replaceAll("::code::", mixerCode.replaceAll(".(?=.)", "$0 "));
		
		return msg;
	}

	String CreateUnusedMessage(String mixerCode)
	{
		String msg = plugin.getConfig().getString("messages.code-un-used", "please enter your six digit code into https://mixer.com/go\n::code::");
		msg = msg.replaceAll("::code::", mixerCode.replaceAll(".(?=.)", "$0 "));
		
		return msg;
	}

	String CreateNotFollowingMessage(String mixerCode)
	{
		String msg = plugin.getConfig().getString("messages.not-following", "You are not following the mixer user.\n::code::");
		msg = msg.replaceAll("::code::", mixerCode.replaceAll(".(?=.)", "$0 "));
		
		return msg;
	}
	
	String CreateFaultMessage(Exception e)
	{
		String msg = plugin.getConfig().getString("messages.fault", "There was an error. Details: ::exception::");
		msg = msg.replaceAll("::exception::", Matcher.quoteReplacement(e.getMessage()));

		return msg;
	}
}