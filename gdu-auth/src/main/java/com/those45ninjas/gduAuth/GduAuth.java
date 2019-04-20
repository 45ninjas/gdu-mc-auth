package com.those45ninjas.gduAuth;

import com.those45ninjas.gduAuth.Authorization;
import com.those45ninjas.gduAuth.mixer.Mixer;
import com.those45ninjas.gduAuth.mixer.Oauth;
import com.those45ninjas.gduAuth.mixer.Users;
import com.those45ninjas.gduAuth.mixer.responses.MixerFollows;
import com.those45ninjas.gduAuth.mixer.responses.OAuthClient;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GduAuth extends JavaPlugin
{
	public Mixer mixer;
	public Authorization auth;
	public MixerFollows[] streamers;

	@Override
	public void onEnable() {
		
		// get or save the default config file.
		this.saveDefaultConfig();
		
		// Register the Gdu Listener (it listens for players trying to join)
		getServer().getPluginManager().registerEvents(new GduListener(this), this);

		// Init. the logging logger.
		new Logging(this);
	
		try
		{
			// Create the default mixer so we can do some startup checks.
			mixer = new Mixer(this);

			// Get the client from mixer.
			OAuthClient client = Oauth.Self(mixer);

			// Was the clientID from mixer.com the same as the one provided in the config file?
			if(client.clientId.equals(Mixer.id))
			{
				// Output the client name from mixer.com to the console.
				getLogger().info("Mixer Startup: Client id check success.");
				getLogger().info("Mixer Startup: " + client.name);
			}
			else
			{
				// Have a cry that the client id is not correct.
				throw new Exception("Client ID is not valid. Get one from https://mixer.com/lab/oauth");
			}

			// Convert the list of mixer user id's into something more useable.
			streamers = Users.GetStreamers(mixer, getConfig().getLongList("follow-users"));

			// Startup the messages and auth classes.
			new Messages(this, streamers);
			auth = new Authorization(this);
		}
		catch (Exception e)
		{
			// Windge about what happened.
			Logging.LogException(e);

			// Disable the plugin.
			Bukkit.getPluginManager().disablePlugin(this);

			// TODO: Add an option to the config file to make a failure not let anyone in at all. (prison vip lockdown)
		}
	}
	@Override
	public void onDisable() {
		
	}
}
