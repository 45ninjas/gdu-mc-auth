package com.those45ninjas.gduAuth.mixer;

import com.google.gson.JsonObject;

public class BadHttpResponse extends Exception
{
    private static final long serialVersionUID = 7273594706973949579L;
    private final JsonResponse response;

    public BadHttpResponse(JsonResponse response)
    {
        super();
        this.response = response;
    }

    public BadHttpResponse(String error, JsonResponse response)
    {
        super(error);
        this.response = response;
    }
    public BadHttpResponse(String error, Throwable cause, JsonResponse response)
    {
        super(error, cause);
        this.response = response;
    }
    public BadHttpResponse(Throwable err, JsonResponse response)
    {
        super(err);
        this.response = response;
    }

    public int getStatus()
    {
        return response.status;
    }
    public String getMixerError()
    {
        JsonObject object = response.json.getAsJsonObject();
        
        String error = object.get("error").getAsString();
        String message = object.get("message").getAsString();

        return String.format("[%d] %s: %s", response.status, error, message);
    }
}