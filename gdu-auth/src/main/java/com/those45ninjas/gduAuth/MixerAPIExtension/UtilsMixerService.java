package com.those45ninjas.gduAuth.MixerAPIExtension;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.mixer.api.MixerAPI;
import com.mixer.api.services.*;
import com.those45ninjas.gduAuth.MixerFunctions;

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

	public ListenableFuture<ShortcodeCheck> checkShortcode(String handle, String clientId, String clientSecret)
	{
		return this.get("shortcode/check/" + handle, ShortcodeCheck.class);
	}

	public ListenableFuture<OAuthTokenResponse> authToken(String code, String clientId, String clientSecret)
	{
		JsonObject data = new JsonObject();
		data.addProperty("grant_type", "authorization_code");
		data.addProperty("client_id", clientId);
		data.addProperty("client_secret", clientSecret);
		data.addProperty("code", code);
		return this.post("authrorize", OAuthTokenResponse.class, data);
	}
	public ListenableFuture<OAuthTokenResponse> refreshToken(String refreshToken, String clientId, String clientSecret)
	{
		JsonObject data = new JsonObject();
		data.addProperty("grant_type", "refresh_token");
		data.addProperty("refresh_token", refreshToken);
		data.addProperty("client_id", clientId);
		data.addProperty("client_secret", clientSecret);
		return this.post("authrorize", OAuthTokenResponse.class, data);
	}

}