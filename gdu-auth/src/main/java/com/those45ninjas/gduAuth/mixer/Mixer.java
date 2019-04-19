package com.those45ninjas.gduAuth.mixer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.those45ninjas.gduAuth.GduAuth;
import com.those45ninjas.gduAuth.Logging;
import com.those45ninjas.gduAuth.mixer.http.MixerCookieJar;
import com.those45ninjas.gduAuth.mixer.responses.MixerFollows;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Mixer {
    public static String id;
    public static String secret;
    
    public OkHttpClient client;
    private String oAuthString = null;

    public static Gson g;

    private static final String mixerApi = "https://mixer.com/api/v1/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf=8");
    public static final String CSRF_TOKEN_HEADER = "x-csrf-token";
    public static final int CSRF_STATUS_CODE = 461;
    public static final String scope = "";

    private String csrfToken;

    private static MixerCookieJar cookieJar = new MixerCookieJar();

    public Mixer(GduAuth plugin) {
        if(g == null)
            g = new Gson();

        client = makeClient();

        id = plugin.getConfig().getString("mixer.id");
        secret = plugin.getConfig().getString("mixer.secret");
    }

    public Mixer(Mixer mixer)
    {
        client = mixer.client;
    }

    private OkHttpClient makeClient()
    {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(cookieJar);
        return builder.build();
    }

    public void SetToken(String token)
    {
        oAuthString = token;
    }

    public Response Get(String resource) throws IOException, BadHttpResponse
    {
        Response rsp = GetUnsafe(resource);

        if(!rsp.isSuccessful())
        {
            throw new BadHttpResponse(rsp);
        }

        return rsp;
    }

    public Response GetUnsafe(String resource) throws IOException
    {
        Request.Builder builder = getRequestBuilder(resource);
        return makeRequest(builder.build());
    }

    public Response Post(String resource, JsonElement post) throws IOException, BadHttpResponse
    {
        // Do the post request.
        Response rsp = PostUnsafe(resource, post);

        // If it was not sucessful... throw the error.
        if(!rsp.isSuccessful())
        {
            throw new BadHttpResponse(rsp);
        }

        return rsp;
    }

    public Response PostUnsafe(String resource, JsonElement post) throws IOException
    {
        Request.Builder builder = getRequestBuilder(resource);

        // Make the body of the post request from the json.
        RequestBody body = RequestBody.create(JSON,post.toString());
        builder.post(body);

        Response response = makeRequest(builder.build());

        return response;

    }

    private Response makeRequest(Request request) throws IOException
    {
        try
        {
            Response response = client.newCall(request).execute();
            // Get or update the csrf token.
            doCSRF(response);

            // Did we get a response with a bad csrf token?
            if(response.code() == CSRF_STATUS_CODE)
            {
                // Do the request again.
                Logging.BadCSRF();
                return client.newCall(request).execute();
            }
            
            return response;
        }
        catch(IOException e)
        {
            throw e;
        }
    }

    private void doCSRF(Response response)
    {
        // Get the csrfToken from the header of the response (if any)
        String csrfTokenHeader = response.headers().get(CSRF_TOKEN_HEADER);

        // Update the new csrfToken.
        if(csrfTokenHeader != null)
            csrfToken = csrfTokenHeader;
    }

    private Request.Builder getRequestBuilder(String resource)
    {
        Request.Builder builder = new Request.Builder().url(mixerApi + resource);

        // Add the CSRF token to the header if we have one.
        if(csrfToken != null)
            builder.addHeader(CSRF_TOKEN_HEADER, csrfToken);

        // Add the OAuth2 bearer/auth code if we have one.
        if(oAuthString != null)
            builder.addHeader("Authorization", "Bearer " + oAuthString);

        return builder;
    }

    public static <T> T ToObject(Class<T> type, ResponseBody body)
    {
        T obj = g.fromJson(body.charStream(), type);

        body.close();

        return obj;
    }
}