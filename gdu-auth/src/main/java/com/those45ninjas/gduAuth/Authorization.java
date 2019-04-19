package com.those45ninjas.gduAuth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.those45ninjas.gduAuth.database.Shortcode;
import com.those45ninjas.gduAuth.database.Token;
import com.those45ninjas.gduAuth.database.User;
import com.those45ninjas.gduAuth.mixer.Mixer;
import com.those45ninjas.gduAuth.mixer.Oauth;
import com.those45ninjas.gduAuth.mixer.responses.MixerUser;
import com.those45ninjas.gduAuth.mixer.responses.ShortcodeCheck;
import com.those45ninjas.gduAuth.mixer.responses.ShortcodeResponse;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class Authorization
{
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
		as.mixer = new Mixer(plugin.mixer);
		try
		{
			Start(as, player.getName());
			if(!DoToken(as))
			{
				// The player has not sucessfully gotten a token from mixer.
				as.success = false;
				return as;
			}
			UpdateMixerDetails(as);
			if(CheckUser(as))
			{
				// Update the last login and let them in.
				as.user.UpdateLastLogin(connection);
				as.success = true;
			}
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
			Logging.LogUserState(session.user, "Has no OAuth token.");
			if(DoShortcode(session))
			{
				// The user has finished with the shortcode. Let's Auorize the new token.
				session.token = new Token(session.uuid);
				session.token.set(Oauth.AuthToken(session.mixer, session.shortcode.authCode));

				// Insert the new token so we have it for later.
				session.token.InsertToken(connection);
				freshToken = true;

				// Remove the now redundant shortcode.
				Shortcode.ClearShortcodesFor(session.uuid, connection);
			}
			// The user has to complete the shortcode and come back.
			else
			{
				return false;
			}
		}

		// Set the token for this user's session.
		session.mixer.SetToken(session.token.accessToken);

		// Only check the token it's not brand spanking new.
		// TODO: Verify the token is still valid. Maybe this should be intergrated into the mixer http calls?

		return true;
	}
	private boolean DoShortcode(AuthSession session) throws Exception
	{
		session.shortcode = Shortcode.GetCode(session.uuid, connection);

		// Does the user have a shortcode assoicated with them?
		if(session.shortcode == null)
		{
			Logging.LogUserState(session.user, "Creating new shortcode.");
			// Ask mixer for a new shortcode.
			ShortcodeResponse shRsp = Oauth.NewShortcode(session.mixer);

			// Add the shortcode into the database.
			session.shortcode = Shortcode.InsertShortcode(session.uuid, shRsp, connection);
			// Kick the player so they can enter the new shortcode.
			session.kickMessage = Messages.Start(session.shortcode, session.user);

			Logging.LogUserState(session.user, "Shortcode for " + session.user.minecraftName + " is " + shRsp.code + " handle: " + shRsp.handle);
			return false;
		}

		// What's the status of the user's shortcode?
		Logging.LogUserState(session.user, "Checking shortcode.");
		ShortcodeCheck check = Oauth.CheckShortcode(session.mixer, session.shortcode.handle);

		// Looks like the user has pressed 'Allow' on the mixer.com/go page.
		if(check.httpCode == 200)
		{
			Logging.LogUserState(session.user, "Shortcode authorized.");
			session.shortcode.authCode = check.code;
			return true;
		}

		if(check.httpCode == 204)
		{
			Logging.LogUserState(session.user, "Shortcode has not been used.");
			session.kickMessage = Messages.Unused(session.shortcode, session.user);
		}

		if(check.httpCode == 404)
		{
			Logging.LogUserState(session.user, "Shortcode expired, creating a new one.");

			// Update the user's existing shortcode with this new one.
			ShortcodeResponse shRsp = Oauth.NewShortcode(session.mixer);
			session.shortcode = new Shortcode(session.uuid, shRsp);
			session.shortcode.Upddate(connection);

			session.kickMessage = Messages.Expired(session.shortcode, session.user);
		}

		if(check.httpCode == 403)
		{
			Logging.LogUserState(session.user, "User has denied shortcode access.");
			session.kickMessage = Messages.Forbidden(session.user);
			
			// Remove the user's shortcode so they can get a new one when the join.
			Shortcode.ClearShortcodesFor(session.uuid, connection);
			session.shortcode = null;
		}
		return false;
	}

	private void UpdateMixerDetails(AuthSession session) throws Exception
	{
		// If the user's mixer details are not set, get them from mixer and update the profile.
		if(session.user.mixerID <= 0 || session.user.mixerName == null || session.user.mixerName.isEmpty())
		{
			Logging.LogUserState(session.user, "Getting user details.");
			//plugin.mixer.SetUserDetails(session);
			MixerUser.GetCurrentUser(session);

			// TODO: Move this thuther down the chan?
			session.user.Update(connection);
		}
	}
	private boolean CheckUser(AuthSession session)
	{
		Logging.LogUserState(session.user, "Checking if " + session.user.mixerName + " is following mixer users.");
		return true;
	}
}
