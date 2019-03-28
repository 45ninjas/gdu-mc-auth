<?php

$code = self::$Args['error']['code'];
$message = self::$Args['error']['message'];

$title = self::$Args['error']['title'];

$target = self::$Args['error']['target'];

?>
<h2><?=$code?> - <?=$title?></h2>
<p><?=$message?></p>
<pre><?=$target?></pre>