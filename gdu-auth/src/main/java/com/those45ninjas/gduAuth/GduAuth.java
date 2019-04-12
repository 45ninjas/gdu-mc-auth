package com.those45ninjas.gduAuth;

import com.those45ninjas.gduAuth.Authorization;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GduAuth extends JavaPlugin
{
	public MixerFunctions mixer;
	public Authorization auth;

	@Override
	public void onEnable() {
		
		// get or save the default config file.
		this.saveDefaultConfig();
		
		// Register the Gdu Listener (it listens for players trying to join)
		getServer().getPluginManager().registerEvents(new GduListener(this), this);
		
		// Attempt to create a connection to the database.
		try
		{
			auth = new Authorization(this);			
			mixer = new MixerFunctions(this);
		}
		catch (Exception e)
		{
			getLogger().severe("GduAuth failed to initalize. Disabling plugin.");
			getLogger().severe(e.getMessage());
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	@Override
	public void onDisable() {
		
	}
}
