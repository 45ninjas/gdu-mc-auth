package com.those45ninjas.gduAuth.mixer.responses;
import java.io.Serializable;

public class ShortcodeCheck implements Serializable
{
    private static final long serialVersionUID = 1854569599424693963L;
    public String code;
    public int httpCode;
}