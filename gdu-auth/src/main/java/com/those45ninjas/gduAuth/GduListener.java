package com.those45ninjas.gduAuth;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import com.those45ninjas.gduAuth.Authorization;
import com.those45ninjas.gduAuth.Authorization.Status;

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
	public void onAsyncPlayerPrelogin(AsyncPlayerPreLoginEvent player)
	{
		// A player is trying to join.
		
		// First, check to see if the server has a whitelist and if the player is the white-list.
		if(Bukkit.hasWhitelist() && Bukkit.getWhitelistedPlayers().contains(player)) {
			// Let the player join.
			player.allow();
			return;
		}
		
		// Not on the white-list? Let's check the database.
		Status status = Authorization.GetStatus(player.getUniqueId());
		
		// Is the player allowed to join?
		if(status == Status.ALLOWED)
		{
			player.allow();
			return;
		}
		
		// The player is not following enough users, kick'em.
		if(status == Status.NOT_FOLLOWING)
		{
			player.disallow(Result.KICK_OTHER, "You are not following PhazorGDU on mixer.com");
		}
		
		// The player has not been allowed in, let's give them a code.
		player.disallow(Result.KICK_WHITELIST, CreateMessage(player.getName(), "E22A97"));
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