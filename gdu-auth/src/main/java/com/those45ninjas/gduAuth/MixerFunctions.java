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
import java.net.URI;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import com.those45ninjas.gduAuth.*;

public class MixerFunctions {
	
	final GduAuth plugin;
	
	private String clientId;
	private String clientSecret;
	private String serviceName;
	
	static final String SCOPE = "user:details:self channel:follow:self";
	
	MixerAPI mixer;
	MixerHttpClient httpClient;
	
	public MixerFunctions(final GduAuth plugin)
	{

		this.plugin = plugin;
		clientId = plugin.getConfig().getString("mixer-client-id");
		clientSecret = plugin.getConfig().getString("mixer-client-secret");
		
		URI uri = URI.create("http://mixer.com/api/v1/");
		String str = null;
		//mixer = new MixerAPI(clientId);
		mixer = new MixerAPI(clientId, uri, str);
		httpClient = new MixerHttpClient(mixer, clientId);
		
		mixer.register(new UtilsMixerService(mixer));
		
		final Logger logger = Bukkit.getLogger();
		
//		Futures.addCallback(mixer.use(UtilsMixerService.class).self(clientId),new ResponseHandler<OAuthClient>() {
//					
//			@Override
//			public void onSuccess(OAuthClient client)
//			{
//				logger.info("Mixer Check is sucessfull");
//				logger.info("OAuth name: " + client.name);
//				serviceName = client.name;
//			}
//			@Override
//		    public void onFailure(Throwable throwable) {
//				logger.warning("Mixer Check failed, make sure your mixer-client-id is correct. Get one from https://mixer.com/lab/oauth");
//				logger.warning(((HttpBadResponseException)throwable).response.body());
//				Bukkit.getPluginManager().disablePlugin(plugin);
//		    }
//			
//		});

		ShortcodeTest();
	}
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
				logger.warning(((HttpBadResponseException)throwable).response.body());
		    }
		});
	}
	public void ShorcodeResponse()
	{
		
	}
}
