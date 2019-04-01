package com.those45ninjas.gduAuth;

import org.bukkit.Bukkit;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.mixer.api.MixerAPI;
import com.mixer.api.http.MixerHttpClient;
import com.mixer.api.services.*;
import com.those45ninjas.gduAuth.OAuthClient;

public class UtilsMixerService extends AbstractHTTPService
{

	public UtilsMixerService(MixerAPI mixer)
	{
		super(mixer, "oauth");
	}
	
	public ListenableFuture<OAuthClient> self(String id)
	{
		return this.get("clients/" + id, OAuthClient.class);
	}
	

	public ListenableFuture<ShortcodeResponse> shortcode(String clientId, String clientSecret)
	{
        JsonObject data = new JsonObject();
        data.addProperty("client_id", clientId);
        data.addProperty("client_secret", clientSecret);
        data.addProperty("scope", MixerFunctions.SCOPE);

		return this.post("shortcode", ShortcodeResponse.class, data);
	}

}