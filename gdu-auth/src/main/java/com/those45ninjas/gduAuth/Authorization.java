package com.those45ninjas.gduAuth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mixer.api.MixerAPI;
import com.mixer.api.http.MixerHttpClient;
import com.those45ninjas.gduAuth.MixerAPIExtension.ShortcodeCheck;
import com.those45ninjas.gduAuth.MixerAPIExtension.ShortcodeResponse;
import com.those45ninjas.gduAuth.database.Shortcode;
import com.those45ninjas.gduAuth.database.Token;
import com.those45ninjas.gduAuth.database.User;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class Authorization
{
	
	public enum Status
	{		
		// Error state.*it all will work when
		ERROR(0),
		// The player is allowed to join.
		ALLOWED(1),
		// The player is not following any or all of the channels.
		NOT_FOLLOWING(2),
		// Code is valid, the user has not granted it.
		MIXER_CODE_204(4),
		// User denied access to account. They can't join, have a new code just in-case.
		MIXER_CODE_403(8),
		// Auth code has expired, They need a new code.
		MIXER_CODE_404(16);

		private int id;
		private static Map map = new HashMap<>();

		Status(int val) {
			id = val;
		}

		static {
			for (Status status : Status.values()) {
				map.put(status.id, status);
			}
		}

		public int GetVal()
		{
			return id;
		}
		public static Status SetVal(int val)
		{
			return (Status) map.get(val);
		}
	}
	
	public Connection connection;
	private GduAuth plugin;

	public Authorization(GduAuth plugin) throws ClassNotFoundException, SQLException
	{
		this.plugin = plugin;
		Connect(plugin.getConfig());
	}
	
	void Connect(FileConfiguration config) throws SQLException, ClassNotFoundException
	{
		// We won't create a new connection if one already exists.
		if(connection != null && !connection.isClosed())
		{
			return;
		}
		
		// Get the username, password and jdbc (Java DataBase Connection) string form the config. 
		String user = config.getString("database.user");
		String password = config.getString("database.password");
		String jdbc = config.getString("database.jdbc");
		
		//TODO: Figure out what this does.
		Class.forName("com.mysql.jdbc.Driver");
		
		// Get the connection.
		connection = DriverManager.getConnection(jdbc,user,password);
	}
	public AuthSession Check(AsyncPlayerPreLoginEvent player) throws Exception
	{
		AuthSession as = new AuthSession(player);
		Logging.LogUserState(player.getName(), "Checking");
		as.client = plugin.mixer.mixer;
		try
		{
			Start(as, player.getName());
			if(!DoToken(as))
			{
				// The player has not sucessfully gotten a token from mixer.
				as.success = false;
				return as;
			}
			CheckUserDetails(as);
			CheckUser(as);
			as.user.Update(connection);
			return as;
		}
		catch( Exception e)
		{
			Logging.LogException(e);
			as.kickMessage = Messages.Fault(e);
			as.success = false;
			return as;
		}
	}
	private void Start(AuthSession session, String minecraftUsername) throws Exception
	{
		session.user = User.GetUser(session.uuid, connection);
		
		// Add a new user if one does not exisit.
		if(session.user == null)
		{
			Logging.LogUserState(minecraftUsername, "Adding new user to database.");
			User.AddNewUser(minecraftUsername, session.uuid, connection);
			session.user = User.GetUser(session.uuid, connection);
		}
	}
	private boolean DoToken(AuthSession session) throws Exception
	{
		session.token = Token.GetToken(session.uuid, connection);

		boolean freshToken = false;
		
		// The user does not have a token. Go through the shortcode process.
		if(session.token == null)
		{
			Logging.LogUserState(session.user, "User has no OAuth token.");
			if(DoShortcode(session))
			{
				// The user has finished with the shortcode. Let's Auorize the new token.
				session.token = new Token(session.uuid);
				session.token = plugin.mixer.AuthorizeToken(session.token, session.shortcode.authCode);
				session.token.InsertToken(connection);
				freshToken = true;
			}
			// The user has to complete the shortcode and come back.
			else
			{
				return false;
			}
		}

		session.client = new MixerAPI(plugin.mixer.clientId, session.token.accessToken);

		// Only check the token it's not brand spanking new.

		return true;
	}
	private boolean DoShortcode(AuthSession session) throws Exception
	{
		session.shortcode = Shortcode.GetCode(session.uuid, connection);

		// Does the user have a shortcode assoicated with them?
		if(session.shortcode == null)
		{
			Logging.LogUserState(session.user, "Creating new shortcode for user.");
			// Ask mixer for a new shortcode.
			ShortcodeResponse shRsp = plugin.mixer.GetNewShortcode();

			// Add the shortcode into the database.
			session.shortcode = Shortcode.InsertShortcode(session.uuid, shRsp, connection);
			// Kick the player so they can enter the new shortcode.
			session.kickMessage = Messages.Start(session.shortcode, session.user);

			Logging.LogUserState(session.user, "Shortcode for " + session.user.minecraftName + " is " + shRsp.code + " handle: " + shRsp.handle);
			return false;
		}

		// What's the status of the user's shortcode?
		ShortcodeCheck check = plugin.mixer.CheckShortcode(session.shortcode.handle);

		// Looks like the user has pressed 'Allow' on the mixer.com/go page.
		if(check.httpCode == 200)
		{
			Logging.LogUserState(session.user, "Shortcode authorized by user.");
			session.shortcode.authCode = check.code;
			session.user.status = Status.NOT_FOLLOWING;
			return true;
		}

		if(check.httpCode == 204)
		{
			Logging.LogUserState(session.user, "Shortcode has not been used.");
			session.user.status = Status.MIXER_CODE_204;
			session.kickMessage = Messages.Unused(session.shortcode, session.user);
		}

		if(check.httpCode == 404)
		{
			Logging.LogUserState(session.user, "Shortcode expired, creating a new one.");
			session.user.status = Status.MIXER_CODE_404;

			// Update the user's existing shortcode with this new one.
			ShortcodeResponse shRsp = plugin.mixer.GetNewShortcode();
			session.shortcode = new Shortcode(session.uuid, shRsp);
			session.shortcode.Upddate(connection);

			session.kickMessage = Messages.Expired(session.shortcode, session.user);
		}

		if(check.httpCode == 403)
		{
			Logging.LogUserState(session.user, "User has denied shortcode access.");
			session.kickMessage = Messages.Forbidden(session.user);
			session.user.status = Status.MIXER_CODE_403;

			// Remove the user's shortcode so they can get a new one when the join.
			Shortcode.ClearShortcodesFor(session.uuid, connection);
			session.shortcode = null;
		}
		return false;
	}

	private void CheckUserDetails(AuthSession session) throws Exception
	{
		// If the user's mixer details are not set, get them from mixer and update the profile.
		if(session.user.mixerID <= 0 || session.user.mixerName == null || session.user.mixerName.isEmpty())
		{
			Logging.LogUserState(session.user, "Getting user details.");
			plugin.mixer.SetUserDetails(session);
		}
	}
	private boolean CheckUser(AuthSession session)
	{
		Logging.LogUserState(session.user, "Checking if user is following mixer users.");
		return true;
	}
	private class ShortcodeStatus
	{
		public Shortcode shortcode;
		public Status status;
	}

	private Shortcode MakeCode(UUID uuid) throws Exception
	{
		ShortcodeResponse response = plugin.mixer.GetNewShortcode();		
		return Shortcode.InsertShortcode(uuid, response, connection);
	}
}
