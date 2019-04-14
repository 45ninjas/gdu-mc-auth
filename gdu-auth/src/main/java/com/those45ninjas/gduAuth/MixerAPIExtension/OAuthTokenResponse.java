package com.those45ninjas.gduAuth.MixerAPIExtension;
import java.io.Serializable;

public class OAuthTokenResponse implements Serializable
{
	private static final long serialVersionUID = 5123719434192288393L;
	public String access_token;
	public String token_type;
	public long expires_in;
	public String refresh_token;
}