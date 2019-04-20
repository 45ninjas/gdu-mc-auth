package com.those45ninjas.gduAuth.mixer.responses;

import java.io.Serializable;

public class MixerFollows implements Serializable
{
    private static final long serialVersionUID = 1436446331977956627L;
    public long userId;
    public String token;

    public static String[] GetNames(MixerFollows[] list)
    {
        // Create an array of strings.
        String[] names = new String[list.length];

        // Add each token in the list to each name.
        for (int i = 0; i < list.length; i++)
        {
            names[i] = list[i].token;    
        }

        return names;
    }

    public static String GetNamesList(MixerFollows[] list, String joiner)
    {
        StringBuilder sb = new StringBuilder();

        // Add the first name.
        sb.append(list[0].token);

        //makes Those45Ninjas

        // Is there only two?
        if(list.length == 2)
        {
            sb.append(" ");
            sb.append(joiner);
            sb.append(" ");
            sb.append(list[1].token);

            // makes Those45Ninjas and PhazorGdu
        }
        else
        {
            // Add every name after the 1st one.
            for (int i = 1; i < list.length; i++)
            {
                sb.append(", ");

                // Is it the last name?
                if(i == list.length - 1)
                {
                    // Add whatever joiner is (and/or) followed by a space.
                    sb.append(joiner);
                    sb.append(" ");
                }

                // Add the name.
                sb.append(list[i].token);
            }

            // makes Those45Ninjas, PhazorGdu, and ItzJumble
        }

        return sb.toString();
    }

    public static long[] GetIds(MixerFollows[] list)
    {
        long[] ids = new long[list.length];

        for (int i = 0; i < list.length; i++)
        {
            ids[i] = list[i].userId;
        }

        return ids;
    }
}