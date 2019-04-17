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
    public static OAuthClient Self(Mixer mixer) throws IOException, BadHttpResponse
    {
        Response reponse = mixer.Get("oauth/clients/" + Mixer.id);
        return Mixer.g.fromJson(reponse.body().charStream(), OAuthClient.class);
    }

    // Get a new shortcode.
    public static ShortcodeResponse NewShortcode(Mixer mixer) throws IOException, BadHttpResponse
    {
        // Add the required parameters for the request.
        JsonObject data = new JsonObject();

        data.addProperty("client_id", Mixer.id);
        data.addProperty("client_secret", Mixer.secret);
        data.addProperty("scope", Mixer.scope);

        // Make the request.
        Response response = mixer.Post("oauth/shortcode", data);

        return Mixer.g.fromJson(response.body().charStream(), ShortcodeResponse.class);
    }

    // Check the status of a shortcode.
    public static ShortcodeCheck CheckShortcode(Mixer mixer, String handle) throws IOException, BadHttpResponse
    {
        Response response = mixer.GetUnsafe("oauth/shortcode/check/" + handle);

        if(response.code() == 200 || response.code() == 204 || response.code() == 403 || response.code() == 404)
        {
            ShortcodeCheck check = Mixer.g.fromJson(response.body().charStream(), ShortcodeCheck.class);
            check.httpCode = response.code();
            return check;
        }

        throw new BadHttpResponse(response);
    }

    // Authrorize a token.
    public static OAuthTokenResponse AuthToken(Mixer mixer, String code) throws IOException, BadHttpResponse
    {
		JsonObject data = new JsonObject();
		data.addProperty("grant_type", "authorization_code");
		data.addProperty("client_id", Mixer.id);
		data.addProperty("client_secret", Mixer.secret);
        data.addProperty("code", code);
        
        Response response = mixer.Post("oauth/token", data);
        return Mixer.g.fromJson(response.body().charStream(), OAuthTokenResponse.class);
    }

    // Refresh a token.
    public static OAuthTokenResponse RefreshToken(Mixer mixer, String refreshToken) throws IOException, BadHttpResponse
    {
        JsonObject data = new JsonObject();
		data.addProperty("grant_type", "refresh_token");
		data.addProperty("refresh_token", refreshToken);
		data.addProperty("client_id", Mixer.id);
        data.addProperty("client_secret", Mixer.secret);
        
        Response response = mixer.Post("oauth/token", data);
        return Mixer.g.fromJson(response.body().charStream(), OAuthTokenResponse.class);
    }
}