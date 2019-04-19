package com.those45ninjas.gduAuth.mixer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.those45ninjas.gduAuth.AuthSession;
import com.those45ninjas.gduAuth.Logging;
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
        Response rsp = session.mixer.Get("users/current");
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

        Response rsp = session.mixer.Get("users/" + session.user.mixerID + "/follows?fields=userId,token&where=token:in" + usersString);

        return Mixer.ToObject(MixerFollows[].class, rsp.body());
    }

    public static MixerFollows[] GetStreamers(Mixer mixer, List<Long> ids) throws IOException, BadHttpResponse
    {
        // Create a list that we will fill up with 'streamers'
        List<MixerFollows> streamers = new LinkedList<MixerFollows>();

        for (long id : ids)
        {
            Response rsp = mixer.GetUnsafe(String.format("users/%d", id));
            if(rsp.code() == 200)
            {
                // Get the mixer user that matches the user id.
                MixerUser user = Mixer.ToObject(MixerUser.class, rsp.body());

                // Convert the user from a Mixer user to a MixerFollows.
                MixerFollows fUser = new MixerFollows();
                fUser.token = user.username;
                fUser.userId = user.id;

                // Add the fUser to the list of streamers.
                streamers.add(fUser);
                Logging.logger.info("Mixer Startup: Added " + user.username + " to the list of streamers");
            }
            else
            {
                // Let the server admin know that a mixer user was not found.
                Logging.logger.warning("Mixer Startup: Unable to get user details for " + id + " [" + rsp.code() + "]");
            }

            // We are done with the response, close it.
            rsp.close();
        }

        // Convert the list of streamers to an array.
        MixerFollows[] follows = new MixerFollows[streamers.size()];
        return streamers.toArray(follows);
    }
}