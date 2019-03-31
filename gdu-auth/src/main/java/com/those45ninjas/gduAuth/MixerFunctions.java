package com.those45ninjas.gduAuth;

import com.google.common.util.concurrent.ListenableFuture;
import com.mixer.api.MixerAPI;
import com.mixer.api.http.*;
import java.io.Serializable;
import org.bukkit.Bukkit;

public class MixerFunctions {
	
	GduAuth plugin;
	
	private String clientId;
	private String clientSecret;
	
	MixerAPI mixer;
	MixerHttpClient httpClient;
	
	public MixerFunctions(GduAuth plugin)
	{
		this.plugin = plugin;
		mixer = new MixerAPI();
		httpClient = new MixerHttpClient(mixer);
	}
	
	public class OAuthClient implements Serializable
	{
		public String[] hosts;
		public long id;
		public String clientId;
		public boolean internal;
		public String name;
		public String website;
		public String logo;
		public boolean hasValidAgreement;
	}
	
	public boolean ConfigValid()
	{
		String resource = "/oauth/clients/" + clientId;
		ListenableFuture<OAuthClient> clientFuture = httpClient.get(resource, OAuthClient.class, null);
		
		OAuthClient client;
		
		try
		{
			clientFuture.wait();
			client = clientFuture.get();
			
			Bukkit.getLogger().info(client.toString());
			
			return true;
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
