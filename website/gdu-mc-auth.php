<?php
// Load the required functions and classes.
include_once "app/functions.php";
include_once "app/GduMinecraft.php";

// Initialize GduMinecraft.
GduMinecraft::Init();


// The last thing to do is to show the layout file.
include_once "app/layout.php";

?>