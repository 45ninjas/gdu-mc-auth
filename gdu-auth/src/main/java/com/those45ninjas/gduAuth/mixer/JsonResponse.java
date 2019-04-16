package com.those45ninjas.gduAuth.mixer;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import okhttp3.Response;

public class JsonResponse {
    public JsonElement json;
    public int status;

    public JsonResponse(Response response) throws JsonSyntaxException, IOException
    {
        status = response.code();
        json = new JsonParser().parse(response.body().string());
	}
}