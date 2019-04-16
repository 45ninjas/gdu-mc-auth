package com.those45ninjas.gduAuth;

import java.io.IOException;
import java.util.logging.Logger;

import com.those45ninjas.gduAuth.database.User;
import com.those45ninjas.gduAuth.mixer.BadHttpResponse;

public class Logging {
    static Logger logger;

    public Logging(GduAuth plugin) {
        logger = plugin.getLogger();
    }

    public static void LogUserState(User user, String state) {
        LogUserState(user.minecraftName, state);
    }

    public static void LogUserState(String name, String state) {
        logger.info(name + " STATE: " + state);
    }
    public static void LogException(Exception e) throws IOException
    {
        logger.severe(e.toString());
        e.printStackTrace();

        BadHttpResponse badResponse = (BadHttpResponse)e.getCause();
        if(badResponse == null)
            badResponse = (BadHttpResponse)e;
        
        if(badResponse != null)
            logger.severe(badResponse.getMixerError());
	}
}