package com.those45ninjas.gduAuth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerEvent;

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
		AUTH_NEW
	}
	
	static Connection connection;
	
	public static void Connect(FileConfiguration config) throws SQLException, ClassNotFoundException
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
	
	public static void AddNewUser(PlayerEvent player) {
		// INSERT (UUID,minecraftName) INTO users
	}
	public static void GetUser(UUID user) {
		// SELECT * FROM users WHERE UUID=$user
	}
	
	public static Status GetStatus(UUID uniqueId) {
		// Get the status of the user.... maybe we should remove this one later.
		return Status.ALLOWED;
	}

}
