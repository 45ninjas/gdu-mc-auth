<?php
if(GduMinecraft::$User != null)
{
	if(GduMinecraft::$User->following != null && isset(GduMinecraft::$Args['following']))
	{
		echo "<div class=\"mixerUsers\">";
		echo "<h2>You are following</h2>";
		foreach (GduMinecraft::$Args['following'] as $following)
		{
			$profilePic = $following['user']['avatarUrl'];
			$username = $following['user']['username'];

			?>
			<div class="mixerUser">
				<img src="<?=$profilePic?>" alt="Avatar">
				<span><?=$username?></span>
			</div>
			<?php
		}
		echo "</div>";
	}
}
else
{
	echo "<h2>Unable to get the required data. Please try again later.</h2>";
}

?>

<a href="minecraft-name" class="button">Set-Up Minecraft</a>