package com.those45ninjas.gduAuth.mixer;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.those45ninjas.gduAuth.GduAuth;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Mixer {
    public static String id;
    public static String secret;
    
    public OkHttpClient client;
    private String oAuthString = null;

    public static Gson g;

    private static final String mixerApi = "https://mixer.com/api/v1/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf=8");
    public static final String scope = "";

    public Mixer(GduAuth plugin) {
        if(g == null)
            g = new Gson();
        
        client = new OkHttpClient();

        id = plugin.getConfig().getString("mixer.id");
        secret = plugin.getConfig().getString("mixer.secret");
    }
    public Mixer(Mixer mixer)
    {
        client = mixer.client;
    }
    public void SetToken(String token)
    {
        oAuthString = token;
    }

    public Response Get(String resource) throws IOException {
        Request.Builder builder = GetRequestBuilder(resource);
        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response;
        }
    }

    public Response Post(String resource, JsonElement post) throws JsonSyntaxException, IOException
    {
        RequestBody body = RequestBody.create(JSON,post.toString());
        Request.Builder builder = GetRequestBuilder(resource);       

        builder.post(body);

        Request request = builder.build();

        try(Response response = client.newCall(request).execute())
        {
            return response;
        }
    }

    private Request.Builder GetRequestBuilder(String resource)
    {
        Request.Builder builder = new Request.Builder().url(mixerApi + resource);

        if(oAuthString != null)
            builder.addHeader("Authorization", "Bearer " + oAuthString);

        return builder;
    }
}