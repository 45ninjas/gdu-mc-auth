<?php

include "vendor/autoload.php";
include "mixer.php";

class GduMinecraft
{
	public static $Title = "GDU Minecraft";
	public static $Dbc;
	public static $PageFile;
	public static $Args = array();
	public static $page;

	public static function Init()
	{
		self::$page = $_SERVER["PATH_INFO"];

		if(self::$page == "/" || empty(self::$page))
			self::$page = "landing";

		self::$Args['page'] = self::$page;
		self::$PageFile = "app/pages/".self::$page.".php";

		// If the page does not exist show the 404 page.
		if(!is_file(self::$PageFile))
		{
			self::Error("File not found", 404, "The requested page was not not found.");
			return;
		}

		self::Behavour();
	}

	public static function Behavour()
	{
		if(self::$page == "/login")
		{
			$mixer = new Mixer();
			$mixer->GetToken();
		}
	}

	public static function Error($title, $code, $message)
	{
		self::$Args['page'] = "error";

		self::$Args['error'] = [];

		self::$Args['error']['title'] = $title;
		self::$Args['error']['code'] = $code;
		self::$Args['error']['message'] = $message;
		self::$Args['error']['target'] = self::$PageFile;

		self::$PageFile = "app/pages/error.php";

		http_response_code($code);
	}

	public static function IncludePage()
	{
		include self::$PageFile;
	}
}

?>