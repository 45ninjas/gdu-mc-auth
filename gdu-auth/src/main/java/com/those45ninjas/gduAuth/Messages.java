package com.those45ninjas.gduAuth;

import java.util.regex.Matcher;

import javax.management.InvalidAttributeValueException;

import com.those45ninjas.gduAuth.Authorization.Status;
import com.those45ninjas.gduAuth.database.User;

public class Messages
{
    public Messages(GduAuth plugin)
    {
        Messages.plugin = plugin;
        
    }

    private static String follows;
    private static GduAuth plugin;

    public static String StartMessage(String mixerCode, User user)
    {
        String msg = plugin.getConfig().getString("messages.start", "Welcome ::user::, Please enter this six digit code into https://mixer.com/go\n::code::");
        
        msg = msg.replaceAll("::user::", Matcher.quoteReplacement(user.minecraftName));
        msg = msg.replaceAll("::code::", FormatCode(mixerCode));
        
        return msg;
    }

    public static String ExpiredMessage(String mixerCode, User user)
    {
        String msg = plugin.getConfig().getString("messages.code-expired", "Your previous code has expired. Here's your new one.\n::code::");
        msg = msg.replaceAll("::code::", FormatCode(mixerCode));
        msg = msg.replaceAll("::user::", Matcher.quoteReplacement(user.minecraftName));
        
        return msg;
    }

    public static String UnusedMessage(String mixerCode, User user)
    {
        String msg = plugin.getConfig().getString("messages.code-un-used", "please enter your six digit code into https://mixer.com/go\n::code::");
        msg = msg.replaceAll("::code::", FormatCode(mixerCode));
        
        return msg;
    }

    public static String NotFollowingMessage(User user)
    {
        String msg = plugin.getConfig().getString("messages.not-following", "You are not following the mixer user.\n::code::");
        msg = msg.replaceAll("::user::", Matcher.quoteReplacement(user.minecraftName));        
        
        return msg;
    }

    public static String FaultMessage(Exception e)
    {
        String msg = plugin.getConfig().getString("messages.fault", "There was an error. Details: ::exception::");
        if(e.getMessage() != null)
            msg = msg.replaceAll("::exception::", Matcher.quoteReplacement(e.getMessage()));
        else
            msg = msg.replaceAll("::exception::", e.getClass().toString());

        return msg;
    }

    private static String FormatCode(String mixerCode)
    {
        return mixerCode.replaceAll(".(?=.)", "$0 ");
    }

    public static String Shortcode(Status status, User user, String code) throws InvalidAttributeValueException
    {
        if(status == Status.MIXER_CODE_204)
            return UnusedMessage(code, user);
        if(status == Status.MIXER_CODE_403)
            return UnusedMessage(code, user);
        if(status == Status.MIXER_CODE_404)
            return ExpiredMessage(code, user);

        throw new InvalidAttributeValueException("Shortcode status was " + status.toString());
	}
}