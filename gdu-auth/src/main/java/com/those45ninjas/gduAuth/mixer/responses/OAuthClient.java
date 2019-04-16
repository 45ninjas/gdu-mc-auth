package com.those45ninjas.gduAuth.mixer.responses;
import java.io.Serializable;

public class OAuthClient implements Serializable
{
	private static final long serialVersionUID = -971546751519119681L;
	public String[] hosts;
	public long id;
	public String clientId;
	public boolean internal;
	public String name;
	public String website;
	public String logo;
	public boolean hasValidAgreement;
}