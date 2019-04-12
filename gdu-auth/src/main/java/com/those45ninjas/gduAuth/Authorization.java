package com.those45ninjas.gduAuth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.those45ninjas.gduAuth.MixerAPIExtension.ShortcodeResponse;
import com.those45ninjas.gduAuth.database.Shortcode;
import com.those45ninjas.gduAuth.database.User;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class Authorization {
	
	public enum Status {
		ALLOWED,
		// The player is not following any or all of the channels.
		NOT_FOLLOWING,
		// Wating for the player to enter the code, send them the old code.
		MIXER_AUTH_WAITING,
		// Code is valid, the user has not granted it.
		MIXER_AUTH_204,
		// User denied access to account. They can't join, have a new code just in-case.
		MIXER_AUTH_403,
		// Auth code has expired, They need a new code.
		MIXER_AUTH_404,
		
		// The user is new, this is only set by the database.
		AUTH_NEW,

		// Error state.
		ERROR
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
			User user = User.GetUser(player.getUniqueId(), connection);
			if(user == null)
			{
				// Looks like the user is not in the database. Go through the process of
				// adding them and generating a mixer key.
				User.AddNewUser(player.getName(), player.getUniqueId(), connection);
				user = User.GetUser(player.getUniqueId(), connection);
			}

			// Just make sure the user actually exists since we have created a new one.
			if(user == null)
			{
				throw new Exception("SQL user was null after creating a new user");
			}

			// The player is new, let's give them a mixer code.
			if(user.status == Status.AUTH_NEW)
			{
				MakeCode(user.uuid);
			}

			return user.status;

		}
		catch (Exception e) 
		{
			throw e;
		}
	}
	private void MakeCode(UUID uuid) throws SQLException
	{
		ShortcodeResponse response = plugin.mixer.GetNewShortcode();
		plugin.getLogger().info("Mixer Shortcode for " + uuid + " is " + response.code);
		plugin.getLogger().info(response.handle);

		Shortcode.InsertShortcode(uuid, response, connection);
	}
}
