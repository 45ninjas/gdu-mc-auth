package com.those45ninjas.gduAuth;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
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

			player.disallow(Result.KICK_OTHER, "There was an unknown error!");
		}
		catch (Exception e)
		{
			String msg = plugin.getConfig().getString("fault-message", "there was an error. Details: ::exception::");
			msg = msg.replaceAll("::exception::", e.getMessage());
			player.disallow(Result.KICK_OTHER, msg);
			throw e;
		}
	}
	
	// Create's a message to help users link their minecraft UUID with mixer.
	String CreateMessage(String username, String mixerCode)
	{
		String message = plugin.getConfig().getString("link-message");
		
		message = message.replaceAll("::user::", username);
		message = message.replaceAll("::code::", mixerCode.replaceAll(".(?=.)", "$0 "));
		
		return message;
	}
}