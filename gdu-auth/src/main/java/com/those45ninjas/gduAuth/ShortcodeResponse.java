package com.those45ninjas.gduAuth;
import java.io.Serializable;

public class ShortcodeResponse implements Serializable
{
	private static final long serialVersionUID = 197199078543838375L;
	public String[] code;
	public String handle;
	public long expires_in;
}