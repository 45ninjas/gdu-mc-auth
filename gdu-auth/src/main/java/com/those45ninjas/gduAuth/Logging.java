package com.those45ninjas.gduAuth;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mixer.api.http.HttpBadResponseException;
import com.those45ninjas.gduAuth.database.User;

public class Logging {
    static Logger logger;
    public Logging(GduAuth plugin)
    {
        logger = plugin.getLogger();
    }

    public static void LogUserState(User user, String state)
    {
        LogUserState(user.minecraftName, state);
    }
    public static void LogUserState(String name, String state)
    {
        logger.info(name + " STATE: " + state);
    }

    public static void LogFutureFail(Exception e, String defaultMessage)
    {
        logger.severe(e.getClass().toString());
        logger.severe(defaultMessage);

        {
            HttpBadResponseException badResponse = (HttpBadResponseException)e.getCause();
            if(badResponse != null)
            {
                LogHttpError((HttpBadResponseException)e.getCause());                
            }
        }
    }

    public static void LogHttpError(HttpBadResponseException badResponse)
    {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(badResponse.response.body());

        if(json.isJsonObject())
        {
            JsonObject error = json.getAsJsonObject();

            int status = error.get("statusCode").getAsInt();
            String errorTitle = error.get("error").getAsString();
            String message = error.get("message").getAsString();

            logger.severe(String.format("[%d] %s: %s",status, errorTitle, message));
        }
        else
        {
            logger.info(badResponse.response.body());
        }
    }

    public static void LogException(Exception e)
    {
        logger.severe(e.toString());
        e.printStackTrace();
	}
}