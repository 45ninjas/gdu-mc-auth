package com.those45ninjas.gduAuth.mixer;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.those45ninjas.gduAuth.mixer.responses.MixerError;

import okhttp3.Response;

public class BadHttpResponse extends Exception {
    private static final long serialVersionUID = 7273594706973949579L;
    private final Response response;

    public BadHttpResponse(Response response) {
        super();
        this.response = response;
    }

    public BadHttpResponse(String error, Response response) {
        super(error);
        this.response = response;
    }

    public BadHttpResponse(String error, Throwable cause, Response response) {
        super(error, cause);
        this.response = response;
    }

    public BadHttpResponse(Throwable err, Response response) {
        super(err);
        this.response = response;
    }

    public int getStatus() {
        return response.code();
    }

    public String getMixerError() throws IOException {
        MixerError error;
        try
        {
            error = Mixer.g.fromJson(response.body().string(), MixerError.class);
            return String.format("[%d] %s: %s", response.code(), error.error, error.message);
        }
        catch (JsonSyntaxException e)
        {
            return "Ubable to parse: " + response.body().string();
        }
        catch (IOException e)
        {
            throw e;
        }
    }
}