<?php

// Get all the rules for a foreach loop.
function GetRules()
{
	// Open the file and make sure we successfully opened the file.
	$rulesFile = fopen("app/rules.txt", "r");

	if(!$rulesFile)
		die("unable to read the rules.txt file.");

	// Go over each line in the rule file.
	while (($line = fgets($rulesFile)) !== false)
	{
		yield $line;
	}

	// We are done, close the rule file.
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