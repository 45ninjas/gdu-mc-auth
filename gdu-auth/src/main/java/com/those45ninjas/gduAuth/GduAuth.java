package com.those45ninjas.gduAuth;

import com.those45ninjas.gduAuth.Authorization;
import com.those45ninjas.gduAuth.mixer.Mixer;
import com.those45ninjas.gduAuth.mixer.Oauth;
import com.those45ninjas.gduAuth.mixer.responses.OAuthClient;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GduAuth extends JavaPlugin
{
	public Mixer mixer;
	public Authorization auth;

	@Override
	public void onEnable() {
		
		// get or save the default config file.
		this.saveDefaultConfig();
		
		// Register the Gdu Listener (it listens for players trying to join)
		getServer().getPluginManager().registerEvents(new GduListener(this), this);

		new Logging(this);
		
		// Attempt to create a connection to the database.
		try
		{
			auth = new Authorization(this);			
			mixer = new Mixer(this);
			new Messages(this);

			// Verify the plugin's id.
			OAuthClient client = Oauth.Self(mixer);
			
			if(client.clientId.equals(Mixer.id))
			{
				// Looks like we have the same client ID.
				getLogger().info("Mixer: Client id check success.");
				getLogger().info("Mixer: " + client.name);
			}
			else
			{
				throw new Exception("Client ID is not valid. Get one from https://mixer.com/lab/oauth");
			}
		}
		catch (Exception e)
		{
			Logging.LogException(e);
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	@Override
	public void onDisable() {
		
	}
}
