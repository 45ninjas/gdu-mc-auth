package com.those45ninjas.gduAuth;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mixer.api.MixerAPI;
import com.mixer.api.http.*;
import com.mixer.api.response.users.*;
import com.mixer.api.resource.MixerUser;
import com.mixer.api.services.impl.UsersService;
import com.mixer.api.util.ResponseHandler;

import java.io.Serializable;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import com.those45ninjas.gduAuth.*;

public class MixerFunctions {
	
	final GduAuth plugin;
	
	private String clientId;
	private String clientSecret;
	private String serviceName;
	
	MixerAPI mixer;
	MixerHttpClient httpClient;
	
	public MixerFunctions(final GduAuth plugin)
	{

		this.plugin = plugin;
		clientId = plugin.getConfig().getString("mixer-client-id");
		clientSecret = plugin.getConfig().getString("mixer-client-secret");
		
		mixer = new MixerAPI(clientId);
		httpClient = new MixerHttpClient(mixer);
		
		mixer.register(new UtilsMixerService(mixer));
		
		final Logger logger = Bukkit.getLogger();
		
		Futures.addCallback(
				mixer.use(UtilsMixerService.class).self(clientId),
				new ResponseHandler<OAuthClient>() {
					
			@Override
			public void onSuccess(OAuthClient client)
			{
				logger.info("Mixer Check is sucessfull");
				logger.info("OAuth name: " + client.name);
				serviceName = client.name;
			}
			@Override
		    public void onFailure(Throwable throwable) {
				logger.warning("Mixer Check failed, make sure your mixer-client-id is correct. Get one from https://mixer.com/lab/oauth");
				logger.warning(throwable.getMessage());
				Bukkit.getPluginManager().disablePlugin(plugin);
		    }
			
		});
		
		
		Futures.addCallback(mixer.use(UsersService.class).search("those45"), new ResponseHandler<UserSearchResponse>() {
			@Override
			public void onSuccess(UserSearchResponse response) {
				for (MixerUser user : response) {
					Bukkit.getLogger().info(user.username);
				}
			}
		});
	}
}
