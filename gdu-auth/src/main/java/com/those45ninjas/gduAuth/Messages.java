package com.those45ninjas.gduAuth;

import java.util.regex.Matcher;

import com.those45ninjas.gduAuth.database.Shortcode;
import com.those45ninjas.gduAuth.database.User;
import com.those45ninjas.gduAuth.mixer.responses.MixerFollows;

public class Messages
{
    public Messages(GduAuth plugin, MixerFollows[] usersToFollow)
    {
        Messages.plugin = plugin;
        Messages.follows = Matcher.quoteReplacement(MixerFollows.GetNamesList(usersToFollow, "or"));
    }

    private static String follows = "[users to follow on mixer]";
    private static GduAuth plugin;

    public static String Start(Shortcode code, User user)
    {
        String msg = plugin.getConfig().getString("messages.start",
        "Welcome ::user::, Please enter this six digit code into https://mixer.com/go\n::code::");
        
        msg = ReplaceCode(msg, code);        
        msg = msg.replaceAll("::user::", Matcher.quoteReplacement(user.minecraftName));
        msg = msg.replaceAll("::follows::", follows);
        
        return msg;
    }

    public static String Expired(Shortcode code, User user)
    {
        String msg = plugin.getConfig().getString("messages.code-expired",
        "Your previous code has expired. Here's your new one.\n::code::");

        msg = ReplaceCode(msg, code);
        msg = msg.replaceAll("::user::", Matcher.quoteReplacement(user.minecraftName));
        msg = msg.replaceAll("::follows::", follows);
        
        return msg;
    }

    public static String Unused(Shortcode code, User user)
    {
        String msg = plugin.getConfig().getString("messages.code-un-used",
        "please enter your six digit code into https://mixer.com/go\n::code::");

        msg = ReplaceCode(msg, code);
        msg = msg.replaceAll("::user::", Matcher.quoteReplacement(user.minecraftName));
        msg = msg.replaceAll("::follows::", follows);
        
        return msg;
    }

    public static String NotFollowing(User user)
    {
        String msg = plugin.getConfig().getString("messages.not-following",
        "You need to be following ::follows:: on mixer.com");

        msg = msg.replaceAll("::user::", Matcher.quoteReplacement(user.minecraftName));
        msg = msg.replaceAll("::follows::", follows);
        msg = msg.replaceAll("::mixer-user::", Matcher.quoteReplacement(user.minecraftName));
        
        return msg;
    }

    public static String Fault(Exception e)
    {
        String msg = plugin.getConfig().getString("messages.fault",
        "There was an error. Details: ::exception::");

        msg = msg.replaceAll("::exception::", e.getClass().toString());

        return msg;
    }
    public static String Forbidden(User user)
    {
        String msg = plugin.getConfig().getString("messages.code-forbidden",
        "You have denied our server access to see who you are folloing on mixer.\nRe-join to try again.");

        msg = msg.replaceAll("::user::", Matcher.quoteReplacement(user.minecraftName));
        msg = msg.replaceAll("::follows::", follows);
        
        return msg;
    }
    public static String Join(User user, MixerFollows[] follows)
    {
        String msg = plugin.getConfig().getString("messages.join-message",
        "::user:: (mixer ::mixer-user::) Joined the server. They are following ::follows::");

        msg = msg.replaceAll("::user::", Matcher.quoteReplacement(user.minecraftName));
        msg = msg.replaceAll("::mixer-user::", Matcher.quoteReplacement(user.mixerName));
        msg = msg.replaceAll("::follows::",  Matcher.quoteReplacement(MixerFollows.GetNamesList(follows, "and")));

        return msg;
    }

    private static String ReplaceCode(String msg, Shortcode code)
    {
        msg = msg.replaceAll("::code::", FormatCode(code.code));
        // TODO:: format this like in the config.yml file says it should be.
        msg = msg.replaceAll("::expires::", code.expires.toString());
        return msg;
    }

    private static String FormatCode(String mixerCode)
    {
        return mixerCode.replaceAll(".(?=.)", "$0 ");
    }
}