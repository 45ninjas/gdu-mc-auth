<?php

// You have to be following at least one of these users to join the minecraft server.
$mixerUsers =
[
	# PhazorGDU
	45977218,
	# ItzJumble
	65863356,
];

// Load the required functions and classes.
include_once "app/functions.php";
include_once "app/GduMinecraft.php";

// Initialize GduMinecraft.
GduMinecraft::$Args['mixer-users'] = $mixerUsers;
GduMinecraft::Init();

// The last thing to do is to show the layout file.
include_once "app/layout.php";

?>