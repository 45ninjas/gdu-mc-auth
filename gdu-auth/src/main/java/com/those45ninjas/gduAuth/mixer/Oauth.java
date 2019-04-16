package com.those45ninjas.gduAuth.mixer;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.those45ninjas.gduAuth.mixer.responses.OAuthClient;
import com.those45ninjas.gduAuth.mixer.responses.OAuthTokenResponse;
import com.those45ninjas.gduAuth.mixer.responses.ShortcodeCheck;
import com.those45ninjas.gduAuth.mixer.responses.ShortcodeResponse;

import okhttp3.Response;

public class Oauth {

    // Get details about our client id.
    public static OAuthClient Self(Mixer mixer) throws IOException
    {
        Response reponse = mixer.Get("clients/" + Mixer.id);
        return Mixer.g.fromJson(reponse.body().charStream(), OAuthClient.class);
    }

    // Get a new shortcode.
    public static ShortcodeResponse NewShortcode(Mixer mixer) throws IOException, BadHttpResponse
    {
        JsonObject data = new JsonObject();

        data.addProperty("client_id", Mixer.id);
        data.addProperty("client_secret", Mixer.secret);
        data.addProperty("scope", Mixer.scope);

        Response response = mixer.Post("shortcode", data);

        if(response.code() != 200)
        {
            throw new BadHttpResponse(response);
        }

        return Mixer.g.fromJson(response.body().charStream(), ShortcodeResponse.class);
    }

    // Check the status of a shortcode.
    public static ShortcodeCheck CheckShortcode(Mixer mixer, String handle) throws IOException
    {
        Response response = mixer.Get("shortcode/check/" + handle);
        ShortcodeCheck check = Mixer.g.fromJson(response.body().charStream(), ShortcodeCheck.class);
        check.httpCode = response.code();

        return check;
    }

    // Authrorize a token.
    public static OAuthTokenResponse AuthToken(Mixer mixer, String code) throws IOException
    {
		JsonObject data = new JsonObject();
		data.addProperty("grant_type", "authorization_code");
		data.addProperty("client_id", Mixer.id);
		data.addProperty("client_secret", Mixer.secret);
        data.addProperty("code", code);
        
        Response response = mixer.Post("token", data);
        return Mixer.g.fromJson(response.body().charStream(), OAuthTokenResponse.class);
    }

    // Refresh a token.
    public static OAuthTokenResponse RefreshToken(Mixer mixer, String refreshToken) throws IOException
    {
        JsonObject data = new JsonObject();
		data.addProperty("grant_type", "refresh_token");
		data.addProperty("refresh_token", refreshToken);
		data.addProperty("client_id", Mixer.id);
        data.addProperty("client_secret", Mixer.secret);
        
        Response response = mixer.Post("token", data);
        return Mixer.g.fromJson(response.body().charStream(), OAuthTokenResponse.class);
    }
}