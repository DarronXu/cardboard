<?php
$home = getenv("CARDBOARD_SERVER_HOME");
$file =fopen($home."/.ngrok-tcp","r") or die("Failed to open Log File.");
$log = fread($file, filesize($home."/.ngrok-tcp"));
$token = strtok($log,"\n");
$finalAns = "";
while($token!==false) {
	if(preg_match("/debug/i",$token,$ans)) {
		$token=strtok("\n");
		continue;
	}
	if(preg_match("/tcp/i",$token,$ans)&&preg_match("/\\[info\\]/i",$token,$ans))
		$finalAns=$token;
	$token=strtok("\n");
}
fclose($file);
if(preg_match("/ngrok\\.com:\\d+/i",$finalAns,$ans)) foreach($ans as $str) {
	echo preg_replace("/ngrok\\.com:(\\d+)/i","$1",$str);
	echo "\n";
}
?>
