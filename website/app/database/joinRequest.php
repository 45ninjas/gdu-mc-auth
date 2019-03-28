<?php

class JoinRequest
{
	// the ID of the user.
	public $userId;
	// the code they must enter to join the server.
	public $joinCode;
	// The IP address they signed up on.
	public $joinAddress;
	// When the join request will expire.
	public $joinExpire;
}

?>