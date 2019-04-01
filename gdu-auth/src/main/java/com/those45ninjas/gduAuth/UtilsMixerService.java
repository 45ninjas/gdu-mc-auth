package com.those45ninjas.gduAuth;

import org.bukkit.Bukkit;

import com.google.common.util.concurrent.ListenableFuture;
import com.mixer.api.MixerAPI;
import com.mixer.api.services.*;
import com.those45ninjas.gduAuth.OAuthClient;

public class UtilsMixerService extends AbstractHTTPService
{

	public UtilsMixerService(MixerAPI mixer)
	{
		super(mixer, "oauth");
//		if(mixer.register(this))
//		{
//			Bukkit.getLogger().warning("Failed to regiester UtilsMixerSerivce");
//		}
	}
	
	public ListenableFuture<OAuthClient> self(String id)
	{
		return this.get("clients/" + id, OAuthClient.class);
	}

}