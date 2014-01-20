<?php header('Content-type: text/html; charset=utf-8'); ?>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="loglook.css">
<title>
<?php
if (isset($_GET['channel']) and isset($_GET['date'])) {
	echo 'Log for #'.$_GET['channel'].' of '.$_GET['date'];
}
else echo 'Log Prettifier';
?>
</title>
</head>
<body>
<?php
if (!isset($_GET['channel'])) echo 'No channel name provided!';
elseif(!isset($_GET['date'])) echo 'No date provided!';
else {
	$filename = '%23'.$_GET['channel'].'/'.$_GET['date'].'.log';
	$log = file_get_contents($filename);
	unset($filename);
	if ($log === FALSE) echo 'Error while trying to open file "'.$filename.'"';
	else {
		$lines = explode("\n", htmlspecialchars($log));
		unset($log);
		echo 'Log for #'.$_GET['channel'].' of '.$_GET['date']."\r\n";
		echo '<table class="log"><tr class="message"> <th class="time">TIME</th> <th class="user">NICK</th> <th class="text">MESSAGE</th></tr>'."\r\n";
		$timestampLength = 7;
		foreach ($lines as $line) {
			if (strlen($line) > 0) {
				$lineSections = explode(' ', $line);
				$messageType = 'message';
				$nickType = 'user';
				//if there isn't both a < and a >, it's not a nick but an action or join/quit message. Change how that looks
				if (strpos($lineSections[1], '&lt;') === FALSE or strpos($lineSections[1], '&gt;') === FALSE) {
					$messageType = 'other';
					$nickType = 'spacer';
				}
				echo '<tr class="'.$messageType.'"><td class="time">'.$lineSections[0].'</td><td class="'.$nickType.'">'.$lineSections[1].'</td> <td class="text">'.substr($line, $timestampLength + strlen($lineSections[1])+2).'</td></tr>'."\r\n";
			}
		}
		echo "</table>\r\n";
	}
}
?>
</body>
</html>
