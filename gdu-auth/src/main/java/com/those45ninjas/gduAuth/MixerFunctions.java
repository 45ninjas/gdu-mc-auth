package com.those45ninjas.gduAuth;

import com.google.common.util.concurrent.Futures;
import com.mixer.api.MixerAPI;
import com.mixer.api.http.*;
import com.mixer.api.util.ResponseHandler;
import com.those45ninjas.gduAuth.MixerAPIExtension.OAuthClient;
import com.those45ninjas.gduAuth.MixerAPIExtension.ShortcodeResponse;
import com.those45ninjas.gduAuth.MixerAPIExtension.UtilsMixerService;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class MixerFunctions {
	
	final GduAuth plugin;
	
	private String clientId;
	private String clientSecret;
	private String serviceName;
	
	// The mixer oauth scopes for this application (permissions you agree to by joining server)
	public static final String SCOPE = "user:details:self channel:follow:self";
	
	MixerAPI mixer;
	MixerHttpClient httpClient;
	
	public MixerFunctions(final GduAuth plugin)
	{
		this.plugin = plugin;
		
		// Set the clientID and client secret values from the config file.
		clientId = plugin.getConfig().getString("mixer.id");
		clientSecret = plugin.getConfig().getString("mixer.secret");
		
		// Init. the mixer and mixer http clients.
		mixer = new MixerAPI(clientId);
		httpClient = new MixerHttpClient(mixer, clientId);
		
		// Register out 'custom' mixer service (shortcodes) because the official API does not support it.
		mixer.register(new UtilsMixerService(mixer));
	
		// Run a test to see if the clientID is set correctly.
		ValidateMixerID();
		
		//TODO: remove this test.
		ShortcodeTest();
	}
	// Validate that the mixer ID is correct.
	public void ValidateMixerID()
	{
		// Get the logger so we can splurt to the console.
		final Logger logger = Bukkit.getLogger();
		
		Futures.addCallback(mixer.use(UtilsMixerService.class).self(clientId),new ResponseHandler<OAuthClient>() {
					
			@Override
			public void onSuccess(OAuthClient client)
			{
				// Wooh, looks like the clientID exists. Print a message and save the client name.
				logger.info("Mixer Check is sucessfull, name: " + client.name);
				serviceName = client.name;
			}
			@Override
		    public void onFailure(Throwable throwable) {
				// Looks like it failed, we better disable the plugin.
				logger.warning("Mixer Check failed, make sure your mixer-client-id is correct. Get one from https://mixer.com/lab/oauth");
				logger.warning(((HttpBadResponseException)throwable).response.body());
				
				// Disable the plugin.
				Bukkit.getPluginManager().disablePlugin(plugin);
		    }
			
		});
	}
	
	// This will be removed one day.
	public void ShortcodeTest()
	{
		final Logger logger = Bukkit.getLogger();
		Futures.addCallback(mixer.use(UtilsMixerService.class).shortcode(clientId, clientSecret), new ResponseHandler<ShortcodeResponse>()
		{
			@Override
			public void onSuccess(ShortcodeResponse response) {
				logger.info("Shortcode: " + response.code);
				logger.info("Expires (seconds): " + response.expires_in);
				logger.info("Handle: " + response.handle);
			}
			@Override
		    public void onFailure(Throwable throwable) {
				logger.warning("Failed to get shortcode.");
				logger.warning(throwable.getMessage());
		    }
		});
	}
}
