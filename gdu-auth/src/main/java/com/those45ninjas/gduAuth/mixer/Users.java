package com.those45ninjas.gduAuth.mixer;

import java.io.IOException;

import com.those45ninjas.gduAuth.AuthSession;
import com.those45ninjas.gduAuth.mixer.responses.MixerFollows;
import com.those45ninjas.gduAuth.mixer.responses.MixerUser;

import okhttp3.Response;

public class Users
{
    public static void SetMixerDetails(AuthSession session) throws IOException, BadHttpResponse
    {
        // Get the mixer user.
        Response rsp = session.mixer.Get("/users/current");
        MixerUser user = Mixer.g.fromJson(rsp.body().charStream(), MixerUser.class);

        // Set the user details.
        session.user.mixerID = user.id;
        session.user.mixerName = user.username;

        // TODO: Get email and discord (if any).
    }

    public static MixerFollows[] GetFollows(AuthSession session, String[] toFollow) throws IOException, BadHttpResponse
    {
        String usersString = String.join(";", toFollow);
        Response rsp = session.mixer.Get("/users/" + session.user.mixerID + "/follows?fields=userId,token&where=token:in" + usersString);

        return Mixer.ToObject(MixerFollows[].class, rsp.body());
    }
    public static MixerFollows[] GetStreamers(Mixer mixer, String[] streamers) throws IOException, BadHttpResponse
    {
        String usersString = String.join(";", streamers);
        Response rsp = mixer.Get("/users/search?fields=userId,token&where=token:in" + usersString);

        return Mixer.ToObject(MixerFollows[].class, rsp.body());
    }
}