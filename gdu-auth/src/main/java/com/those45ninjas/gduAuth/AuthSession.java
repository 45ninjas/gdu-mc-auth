package com.those45ninjas.gduAuth;

import java.util.UUID;

import com.those45ninjas.gduAuth.database.*;
import com.those45ninjas.gduAuth.mixer.Mixer;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class AuthSession
{
    public UUID uuid;
    public User user;
    public Token token;
    public Shortcode shortcode;
    public Mixer mixer;
    public String kickMessage;
    public boolean success;

    public AuthSession(AsyncPlayerPreLoginEvent playerEvent)
    {
        uuid = playerEvent.getUniqueId();        
        success = false;
    }
}