package com.those45ninjas.gduAuth.mixer.http;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MixerCookieJar implements CookieJar
{
    private List<Cookie> storage = new ArrayList<>();


    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        storage.addAll(cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url)
    {
        // Get a stack of cookies that have expired.
        Stack<Cookie> removeCookies = new Stack<Cookie>();
        for (Cookie cookie : storage)
        {
            if(cookie.expiresAt() < System.currentTimeMillis())
            {
                removeCookies.add(cookie);
            }
        }

        // Remove all the cookies that have expired from the stack.
        while(!removeCookies.isEmpty())
        {
            storage.remove(removeCookies.pop());
        }

        // Get all the cookies that match the url and return.
        List<Cookie> cookies = new LinkedList<Cookie>();
        for (Cookie cookie : storage)
        {
            if(cookie.matches(url))
                cookies.add(cookie);
        }
        return cookies;
    }
    
}