package com.those45ninjas.gduAuth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
	public Status Check(AsyncPlayerPreLoginEvent player) throws Exception
	{
		try
		{
			// See if the user is in the databae. If not, add them.
			User user = User.GetUser(player.getUniqueId(), connection);
			if(user == null)
			{				
				User.AddNewUser(player.getName(), player.getUniqueId(), connection);
				user = User.GetUser(player.getUniqueId(), connection);
			}

			// Does the user not have an OAuth Token?
			Token token = Token.GetToken(player.getUniqueId(), connection);
			if(token == null)
			{
				// Create or check the shortcode.
				ShortcodeStatus check = DoShortcode(user);
				
				// Tell the user to enter a code into mixer.
				if(check.status == Status.MIXER_CODE_204 || check.status == Status.MIXER_CODE_403 || check.status == Status.MIXER_CODE_404)
				{
					user.ChangeStatus(check.status, connection);
					player.disallow(Result.KICK_OTHER, Messages.Shortcode(check.status, user, check.shortcode.code));
					return check.status;
				}

				// Authorize and isnert the new token.
				token = new Token(player.getUniqueId());
				token = plugin.mixer.AuthorizeToken(token, check.shortcode.authCode);
				token.InsertToken(connection);

				// Remove the now un-used shortcode.
				Shortcode.ClearShortcodesFor(player.getUniqueId(), connection);

				user.InitClient(token, plugin.mixer);

				// Get the user's mixer details.
				plugin.mixer.SetUserDetails(user);
			}
			// The player already has a token, refresh it if needed.
			else
			{
				//TODO: Remove this debug line.
				plugin.getLogger().info(token.uuid.toString());

				user.InitClient(token, plugin.mixer);
				// If the token is out of-date, get a new one.
				Calendar now = Calendar.getInstance();
				if(token.expires.before(now.getTime()))
				{
					// Refresh the token and add it to the database.
					token = plugin.mixer.RefreshToken(token);
					token.UpdateToken(connection);
				}
			}

			return Status.ERROR;
		}
		catch (Exception e) 
		{
			throw e;
		}
	}
	
	private ShortcodeStatus DoShortcode(User user) throws Exception
	{
		ShortcodeStatus ss = new ShortcodeStatus();
		ss.shortcode = Shortcode.GetCode(user.uuid, connection);

		// Does the user have a shortcode assoicated with them?
		if(ss.shortcode == null)
		{
			// Ask mixer for a shortcode.users
			ShortcodeResponse shRsp = plugin.mixer.GetNewShortcode();

			// Add the shortcode to the database.
			ss.shortcode = Shortcode.InsertShortcode(user.uuid, shRsp, connection);

			ss.status = Status.MIXER_CODE_204;
			// return we are wating for the user to enter the shortcode.
			return ss;
		}

		// What's the status of this shortcode?
		ShortcodeCheck check = plugin.mixer.CheckShortcode(ss.shortcode.handle);

		if(check.httpCode == 200)
		{
			ss.shortcode.authCode = check.code;
			ss.status = Status.NOT_FOLLOWING;
			return ss;
		}

		ss.status = Status.ERROR;

		plugin.getLogger().info(ss.status.toString());

		if(check.httpCode == 204)
			ss.status = Status.MIXER_CODE_204;

		if(check.httpCode == 403)
			ss.status = Status.MIXER_CODE_403;

		if(check.httpCode == 404)
			ss.status = Status.MIXER_CODE_404;
		return ss;
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
