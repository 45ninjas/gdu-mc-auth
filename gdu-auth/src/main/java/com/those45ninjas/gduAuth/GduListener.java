package com.those45ninjas.gduAuth;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

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
		
		// Check the in-coming player.
		AuthSession auth = plugin.auth.Check(player);

		// If the kick message is empty, set it to something.
		// TODO: Put this message in the config file.
		if(auth.kickMessage == null || auth.kickMessage.isEmpty())
			auth.kickMessage = "GDU-AUTH ERROR: Something went so wrong that we don't know what happened.";

		// Are they allowed?
		if(auth.success)
			player.allow();
		else
			player.disallow(Result.KICK_WHITELIST, auth.kickMessage);
	}
}