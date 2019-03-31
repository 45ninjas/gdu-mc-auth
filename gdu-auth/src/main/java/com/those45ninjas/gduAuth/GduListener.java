package com.those45ninjas.gduAuth;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
//import org.bukkit.event.Event.Result;
import org.bukkit.event.player.*;

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
	public void onAsyncPlayerPrelogin(AsyncPlayerPreLoginEvent event)
	{
		String kickMessage = CreateMessage(event.getName(), "E22A97");
		event.setKickMessage(kickMessage);
		event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
	}
	
	
	String CreateMessage(String username, String mixerCode)
	{
		String message = plugin.getConfig().getString("link-message");
		
		message = message.replaceAll("::user::", username);
		message = message.replaceAll("::code::", mixerCode.replaceAll(".(?=.)", "$0 "));
		
		return message;
	}
}