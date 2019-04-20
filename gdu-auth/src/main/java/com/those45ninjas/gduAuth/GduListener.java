package com.those45ninjas.gduAuth;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import com.those45ninjas.gduAuth.mixer.responses.MixerFollows;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class GduListener implements Listener
{
	GduAuth plugin;

	Map<UUID, String> joinMessages;
	public GduListener(GduAuth gduAuth)
	{
		plugin = gduAuth;
		joinMessages = new HashMap<UUID,String>();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		// Set the join message if the user has a list of follows in the follows map.
		UUID uuid = event.getPlayer().getUniqueId();
		if(joinMessages.containsKey(uuid))
		{
			event.setJoinMessage(joinMessages.get(uuid));
			joinMessages.remove(uuid);
		}
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
		AuthSession session = plugin.auth.Check(player);

		// If the kick message is empty, set it to something.
		if(session.kickMessage == null || session.kickMessage.isEmpty())
			session.kickMessage = Messages.Fault(new Exception("Kick message not set"));

		// Is the player not allowed to join?
		if(!session.success)
		{
			// tell them why they can't join.
			player.disallow(Result.KICK_WHITELIST, session.kickMessage);
			return;
		}

		// Create a join message for this player.
		if(session.peopleFollowing != null)
		{
			String message = Messages.Join(session.user, session.peopleFollowing);
			joinMessages.put(player.getUniqueId(), message);
		}

		// Let the player in.
		player.allow();
	}
}