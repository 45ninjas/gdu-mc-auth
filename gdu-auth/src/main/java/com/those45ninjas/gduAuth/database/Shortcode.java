package com.those45ninjas.gduAuth.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import com.those45ninjas.gduAuth.mixer.responses.ShortcodeResponse;

public class Shortcode {

    // The minecraft UUID of the user. In the database this is stored as BINARY(16)
    // and must be converted using mysql's BIN_TO_UUID and UUID_TO_BIN functions.
    public UUID uuid;

    // The six digit code.
    public String code;

    // The handle.
    public String handle;

    // The time when the code will expire.
    public Timestamp expires;

    // The code used to autorize the auth token.
    // TODO: This does not need to be stored in the database. Remove it.
    public String authCode;

    public Shortcode()
    {
        handle = null;
        code = null;
        expires = null;
        uuid = null;
        authCode = null;
    }

    public Shortcode(UUID uuid, ShortcodeResponse shortcodeResponse)
    {
        this.uuid = uuid;
        code = shortcodeResponse.code;
        handle = shortcodeResponse.handle;
        expires = GetTime(shortcodeResponse.expires_in);
        authCode = null;
    }

    // Get a shortcode.
    public static Shortcode GetCode(UUID uuid, Connection connection) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement("SELECT BIN_TO_UUID(UUID) as UUID, shortcode, handle, expires, code from shortcodes where UUID = UUID_TO_BIN(?)");
        statement.setString(1, uuid.toString());

        ResultSet resultSet = statement.executeQuery();

        if(!resultSet.next())
            return null;
        
        Shortcode shortcode = new Shortcode();
        shortcode.uuid = UUID.fromString(resultSet.getString("UUID"));
        shortcode.code = resultSet.getString("shortcode");
        shortcode.handle = resultSet.getString("handle");
        shortcode.authCode = resultSet.getString("code");
        return shortcode;
    }

    public static Timestamp GetTime(long expires_in)
    {
        // Add expires_in secconds to the current time.
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, (int)expires_in);

        return new Timestamp(now.getTime().getTime());
    }
    
    // Insert a new shortcode.
    public static Shortcode InsertShortcode(UUID uuid, ShortcodeResponse shortcodeResponse, Connection connection) throws SQLException
    {
        Shortcode code = new Shortcode(uuid, shortcodeResponse);
        
        String sql = "INSERT INTO shortcodes" +
        "(UUID, shortcode, handle, expires, code)" +
        "VALUES" +
        "(UUID_TO_BIN(?),?,?,?,?)";
        
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, code.uuid.toString());
        ps.setString(2, code.code);
        ps.setString(3, code.handle);
        ps.setTimestamp(4, code.expires);
        ps.setString(5, code.authCode);

        ps.execute();

        return code;
    }
    public void Upddate(Connection connection) throws SQLException
    {
        String sql = "UPDATE shortcodes SET " +
        "shortcode = ?, " +
        "handle = ?, " +
        "expires = ?, " +
        "code = ? " +
        "WHERE UUID_TO_BIN(?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, code);
        ps.setString(2, handle);
        ps.setTimestamp(3, expires);
        ps.setString(4, authCode);
        ps.setString(5, uuid.toString());

        ps.executeUpdate();
    }

    // Clear out all shortcodes that have the specified UUID.
    public static void ClearShortcodesFor(UUID uuid, Connection connection) throws SQLException
    {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM shortcodes WHERE UUID=UUID_TO_BIN(?)");
        ps.setString(1, uuid.toString());

        ps.execute();
    }
}