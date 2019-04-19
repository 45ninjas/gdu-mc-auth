package com.those45ninjas.gduAuth.mixer.responses;

import java.io.IOException;
import java.io.Serializable;

import com.those45ninjas.gduAuth.AuthSession;
import com.those45ninjas.gduAuth.Logging;
import com.those45ninjas.gduAuth.mixer.BadHttpResponse;
import com.those45ninjas.gduAuth.mixer.Mixer;

import okhttp3.Response;

public class MixerUser implements Serializable {
    private static final long serialVersionUID = -1357399010267624504L;
    public long id;
    public long level;
    public String username;
    public String email;
    public boolean verified;
    public long experience;
    public long sparks;
    public String avatarUrl;
    public String bio;
    public long primaryTeam;

    public class SocialInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String twitter;
        public String facebook;
        public String youtube;
        public String player;
        public String discord;
        public String[] verified;
    }

    public static void GetCurrentUser(AuthSession session) throws IOException, BadHttpResponse
    {
        Response rsp = session.mixer.Get("users/current");
        
        MixerUser mixUser = session.mixer.g.fromJson(rsp.body().charStream(), MixerUser.class);

        Logging.logger.info(mixUser.username);

        session.user.mixerName = mixUser.username;
        session.user.mixerID = mixUser.id;
	}
}