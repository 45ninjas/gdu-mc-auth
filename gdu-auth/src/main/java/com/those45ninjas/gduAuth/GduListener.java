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
		Bukkit.broadcastMessage("Hello World2");
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

			if(state == Status.MIXER_CODE_204)
			{
				return;
			}


			throw new Exception("Something went terribly wrong.");
		}
		catch (Exception e)
		{
			player.disallow(Result.KICK_OTHER, Messages.FaultMessage(e));
			throw e;
		}
	}
}