<?php

class User
{
	// Local database ID
	public $id;
	// The Minecraft user-name they want to use.
	public $minecraftName;
	// mixer ID.
	public $mixerId;
	// mixer username.
	public $mixerName;
	// The list of mixer ID's they are following.
	public $following;

	// The last time they logged into the Minecraft server.
	public $lastLogin;
	// The time they signed up for the minecraft server.
	public $signupTime;

	// Discord ID (for future proofing.)
	public $discordId;

	// A comment for that user.
	public $comment;

	public static function WriteToDatabase($dbc)
	{
		
	}
}

?>