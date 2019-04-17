package com.those45ninjas.gduAuth.mixer;

import com.google.gson.JsonSyntaxException;
import com.those45ninjas.gduAuth.mixer.responses.MixerError;

import okhttp3.Response;

public class BadHttpResponse extends Exception
{
    private static final long serialVersionUID = 7273594706973949579L;
    private final Response response;

    public BadHttpResponse(Response response)
    {
        super();
        this.response = response;
    }

    public BadHttpResponse(String error, Response response)
    {
        super(error);
        this.response = response;
    }

    public BadHttpResponse(String error, Throwable cause, Response response)
    {
        super(error, cause);
        this.response = response;
    }

    public BadHttpResponse(Throwable err, Response response)
    {
        super(err);
        this.response = response;
    }

    public int getStatus()
    {
        return response.code();
    }

    public String getMixerError()
    {
        MixerError error;
        String body;

        // Read the body of the http request's response.
        try
        {
            body = response.body().string();
        }
        catch (Exception e)
        {
            return String.format("[%d] Error: %s", response.code(), response.message());
        }

        // Parse it and display details about the error.
        try
        {
            error = Mixer.g.fromJson(body, MixerError.class);
            return String.format("[%d] %s: %s", response.code(), error.error, error.message);
        }
        catch (JsonSyntaxException e)
        {
            return String.format("[$d] Error: Unable to parse error message. %s", response.code(), body);
        }
    }
}