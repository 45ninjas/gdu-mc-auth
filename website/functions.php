<?php
function GetRules()
{
	$rulesFile = fopen("private/rules.txt", "r");

	if(!$rulesFile)
		die("unable to read the rules.txt file.");

	while (($line = fgets($rulesFile)) !== false)
	{
		yield $line;
	}

	fclose($rulesFile);
}

function ListRules()
{
	echo "<ol class=\"rules\">";
	foreach (GetRules() as $index => $rule)
	{
		echo "<li><input type=\"checkbox\" name=\"rules[]\" value=\"$index\" id=\"rule-$index\"><label for=\"rule-$index\">$rule</label></li>";
	}
	echo "</ol>";
}
?>