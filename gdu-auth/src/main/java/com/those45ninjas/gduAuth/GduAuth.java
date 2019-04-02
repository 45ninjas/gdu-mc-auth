package com.those45ninjas.gduAuth;
import java.sql.SQLException;

import com.those45ninjas.gduAuth.Authorization;
import org.bukkit.plugin.java.JavaPlugin;

public class GduAuth extends JavaPlugin
{
	MixerFunctions mixer;
	@Override
	public void onEnable() {
		
		// get or save the default config file.
		this.saveDefaultConfig();
		
		// Register the Gdu Listener (it listens for players trying to join)
		getServer().getPluginManager().registerEvents(new GduListener(this), this);
		
		// Attempt to create a connection to the database.
		try
		{
			Authorization.Connect(getConfig());
		}
		catch (ClassNotFoundException | SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create the mixer functions instance.
		// TODO: Change this to static?
		mixer = new MixerFunctions(this);
	}
	@Override
	public void onDisable() {
		
	}
}
