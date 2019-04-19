package com.those45ninjas.gduAuth.mixer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.those45ninjas.gduAuth.AuthSession;
import com.those45ninjas.gduAuth.mixer.responses.MixerFollows;
import com.those45ninjas.gduAuth.mixer.responses.MixerUser;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

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

    public static MixerFollows[] GetFollows(AuthSession session, long[] toFollow) throws IOException, BadHttpResponse
    {
        // Convert the array of mixerId's into a string of arrays seperated by ;'s
        String usersString = StringUtils.join(ArrayUtils.toObject(toFollow), ";");

        Response rsp = session.mixer.Get("/users/" + session.user.mixerID + "/follows?fields=userId,token&where=token:in" + usersString);

        return Mixer.ToObject(MixerFollows[].class, rsp.body());
    }
}