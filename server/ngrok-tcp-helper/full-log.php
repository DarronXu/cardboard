<pre>
<?php
$home = getenv("CARDBOARD_SERVER_HOME");
$file =fopen($home."/.ngrok-tcp","r") or die("Failed to open Log File.");
$log = fread($file, filesize($home."/.ngrok-tcp"));
$token = strtok($log,"\n");
while($token!==false) {
	if(preg_match("/debug/i",$token,$ans)) {
		$token=strtok("\n");
		continue;
	}
	if(preg_match("/tcp/i",$token,$ans))
		echo $token."\n";
	$token=strtok("\n");
}
fclose($file);
?>
</pre>
