package com.those45ninjas.gduAuth.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import com.those45ninjas.gduAuth.mixer.responses.OAuthTokenResponse;

public class Token {

    // The minecraft UUID of the user. In the database this is stored as BINARY(16)
    // and must be converted using mysql's BIN_TO_UUID and UUID_TO_BIN functions.
    public UUID uuid;

    // The actual token.
    public String accessToken;

    // The type of token.
    public String type;

    // The time when this token expires
    public Timestamp expires;

    // The token we use to refresh this token.
    public String refreshToken;

    public Token(UUID uuid)
    {
        this.uuid = uuid;
    }

    // Get a token for a user.
    public static Token GetToken(UUID uuid, Connection connection) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement("SELECT access_token, type, expires, refresh_token from tokens where UUID = UUID_TO_BIN(?)");
        statement.setString(1, uuid.toString());

        ResultSet resultSet = statement.executeQuery();

        if(!resultSet.next())
            return null;
        
        Token token = new Token(uuid);
        token.accessToken = resultSet.getString("access_token");
        token.refreshToken = resultSet.getString("refresh_token");
        token.type = resultSet.getString("type");
        token.expires = resultSet.getTimestamp("expires");

        return token;
    }

    public void InsertToken(Connection connection) throws SQLException
    {
        String sql = "INSERT INTO tokens " +
        "(UUID, access_token, type, expires, refresh_token) " +
        "VALUES " +
        "(UUID_TO_BIN(?),?,?,?,?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, uuid.toString());
        ps.setString(2, accessToken);
        ps.setString(3, type);
        ps.setTimestamp(4, expires);
        ps.setString(5, refreshToken);

        ps.execute();
    }

    public void UpdateToken(Connection connection) throws SQLException
    {     
        String sql = "UPDATE tokens SET " +
        "access_token = ?, " +
        "type = ?, " +
        "expires = ?, " +
        "refresh_token = ? " +
        "WHERE UUID = UUID_TO_BIN(?)";
        
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, accessToken);
        ps.setString(2, type);
        ps.setTimestamp(3, expires);
        ps.setString(4, refreshToken);
        ps.setString(5, uuid.toString());

        ps.execute();
    }

    // Clear out all the tokens that have the specified UUID.
    public static void ClearTokensFor(UUID uuid, Connection connection) throws SQLException
    {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM tokens WHERE UUID=UUID_TO_BIN(?)");
        ps.setString(1, uuid.toString());

        ps.execute();
    }

    public void set(OAuthTokenResponse authToken)
    {
        this.accessToken = authToken.access_token;
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, (int)authToken.expires_in);

        this.expires = new Timestamp(now.getTime().getTime());
        this.refreshToken = authToken.refresh_token;
        this.type = authToken.token_type;
	}
}