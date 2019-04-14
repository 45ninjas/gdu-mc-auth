package com.those45ninjas.gduAuth;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mixer.api.MixerAPI;
import com.mixer.api.http.*;
import com.mixer.api.resource.MixerUser;
import com.mixer.api.services.impl.UsersService;
import com.mixer.api.util.ResponseHandler;
import com.those45ninjas.gduAuth.MixerAPIExtension.OAuthClient;
import com.those45ninjas.gduAuth.MixerAPIExtension.OAuthTokenResponse;
import com.those45ninjas.gduAuth.MixerAPIExtension.ShortcodeCheck;
import com.those45ninjas.gduAuth.MixerAPIExtension.ShortcodeResponse;
import com.those45ninjas.gduAuth.MixerAPIExtension.UtilsMixerService;
import com.those45ninjas.gduAuth.database.Shortcode;
import com.those45ninjas.gduAuth.database.Token;
import com.those45ninjas.gduAuth.database.User;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class MixerFunctions {

    final GduAuth plugin;

    public String clientId;
    private String clientSecret;
    private String serviceName;

    // The mixer oauth scopes for this application (permissions you agree to by
    // joining server)
    public static final String SCOPE = "user:details:self";

    public MixerAPI mixer;

    public MixerFunctions(final GduAuth plugin) {
        this.plugin = plugin;

        // Set the clientID and client secret values from the config file.
        clientId = plugin.getConfig().getString("mixer.id");
        clientSecret = plugin.getConfig().getString("mixer.secret");

        // Init. the mixer and mixer http clients.
        mixer = new MixerAPI(clientId);

        // Register out 'custom' mixer service (shortcodes) because the official API
        // does not support it.
        mixer.register(new UtilsMixerService(mixer));

        // Run a test to see if the clientID is set correctly.
        ValidateMixerID();
    }

    // Validate that the mixer ID is correct.
    public void ValidateMixerID() {
        // Get the logger so we can splurt to the console.
        final Logger logger = Bukkit.getLogger();

        Futures.addCallback(mixer.use(UtilsMixerService.class).self(clientId), new ResponseHandler<OAuthClient>() {

            @Override
            public void onSuccess(OAuthClient client) {
                // Wooh, looks like the clientID exists. Print a message and save the client
                // name.
                logger.info("Mixer Check is sucessfull, name: " + client.name);
                serviceName = client.name;
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Looks like it failed, we better disable the plugin.
                logger.warning(
                        "Mixer Check failed, make sure your mixer-client-id is correct. Get one from https://mixer.com/lab/oauth");
                logger.warning(((HttpBadResponseException) throwable).response.body());

                // Disable the plugin.
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        });
    }

    public void SetUserDetails(User user) throws Exception {
        ListenableFuture<MixerUser> future = user.client.use(UsersService.class).getCurrent();
        try {
            MixerUser mixUser = future.get(10, TimeUnit.SECONDS);
            user.mixerName = mixUser.username;
            user.mixerID = mixUser.id;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            HttpBadResponseException badHttp = (HttpBadResponseException) e.getCause();

            if (badHttp == null)
                throw new Exception("The server was unable to get your user details from mixer.");

            plugin.getLogger().info("Set User Details response: " + badHttp.response.status());
            plugin.getLogger().info(badHttp.response.body());
        }
    }

    public ShortcodeResponse GetNewShortcode() throws Exception {
        ListenableFuture<ShortcodeResponse> future = mixer.use(UtilsMixerService.class).shortcode(clientId,
                clientSecret);
        try {
            ShortcodeResponse resp = future.get(10, TimeUnit.SECONDS);
            return resp;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            HttpBadResponseException badHttp = (HttpBadResponseException) e.getCause();

            if (badHttp == null)
                throw new Exception("The server was unable to contact mixer's servers.");

            plugin.getLogger().info("Get New Shortcode Response: " + badHttp.response.status());
        }
        return null;
    }

    public ShortcodeCheck CheckShortcode(String handle) throws Exception {
        ListenableFuture<ShortcodeCheck> future = mixer.use(UtilsMixerService.class).checkShortcode(handle, clientId,
                clientSecret);

        ShortcodeCheck check = new ShortcodeCheck();
        try {
            check = future.get(10, TimeUnit.SECONDS);
            check.httpCode = 200;
            return check;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            HttpBadResponseException badHttp = (HttpBadResponseException) e.getCause();

            if (badHttp == null)
                throw new Exception("The server was unable to contact mixer's servers.");

            check.httpCode = badHttp.response.status();
        }
        return check;
    }


    // Authorize a token.
    public Token AuthorizeToken(Token token, String code) throws Exception
    {
        ListenableFuture<OAuthTokenResponse> future = mixer.use(UtilsMixerService.class).authToken(code, clientId,
                clientSecret);
        return GetToken(future, token);
    }
    // Refresh a token.
    public Token RefreshToken(Token token) throws Exception
    {
        ListenableFuture<OAuthTokenResponse> future = mixer.use(UtilsMixerService.class).refreshToken(token.refreshToken, clientId,
                clientSecret);
        return GetToken(future, token);
    }
    // Gets a token from the 'future'.
    private Token GetToken(ListenableFuture<OAuthTokenResponse> future, Token token) throws Exception
    {
        try
        {
            OAuthTokenResponse response = future.get(10, TimeUnit.SECONDS);
            
            token.accessToken = response.access_token;
            token.refreshToken = response.refresh_token;
            token.type = response.token_type;
            
            // Add expires_in secconds to the current time.
            Calendar now = Calendar.getInstance();
            now.add(Calendar.SECOND, (int)response.expires_in);

            token.expires = new Timestamp(now.getTime().getTime());
        }
        catch (InterruptedException | ExecutionException | TimeoutException e)
        {
            HttpBadResponseException badHttp = (HttpBadResponseException) e.getCause();

            //throw new Exception("The server was unable to authorize or refresh your token from mixer.com");

            plugin.getLogger().info("GetToken: " + badHttp.response.status());
            plugin.getLogger().info(badHttp.response.body());
            throw e;
        }

        return token;
    }
}