<?php header('Content-type: text/html; charset=utf-8'); ?>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="loglook.css">
<script src="eventsscript.js"></script>
<title>
<?php
//First and foremost, some input validation
$channel = (isset($_GET['channel']) ? htmlspecialchars($_GET['channel']) : FALSE) ;
$date = (preg_match('/\A\d{4}-\d{2}-\d{2}\z/', $_GET['date']) === 1 ? htmlspecialchars($_GET['date']) : FALSE);
$hideEvents = FALSE;
if ($_GET['hideevents'] === 'true') $hideEvents = TRUE;

if ($channel !== FALSE and $date !== FALSE) {
	echo 'Log for #'.$channel.' on '.$date;
	if ($hideEvents) echo ' with events hidden';
}
else echo 'Log Prettifier';
?>
</title>
</head>
<body>
<?php
if ($channel === FALSE) echo 'No channel name provided!';
elseif($date === FALSE) echo 'No date provided!';
else {
	$filename = '/home/stefan/logs/DesertBusForHope/#'.$channel.'/'.$date.'.log';
	$log = file_get_contents($filename);
	if ($log === FALSE) echo 'Error while trying to open log file.';
	else {
		unset($filename);
		$lines = explode("\n", htmlspecialchars($log));
		unset($log);
		
		$eventLinkUrl = $_SERVER['REQUEST_URI'];
		//If there's already a hideevents setting, replace that
		if (strpos($eventLinkUrl, 'hideevents') !== FALSE) {
			$eventLinkUrl = preg_replace('/hideevents=[^&\z]+/', 'hideevents='.($hideEvents?'false':'true'), $eventLinkUrl);
		}
		//otherwise, add it on
		else $eventLinkUrl .= '&hideevents='.($hideEvents?'false':'true');
		echo '<p><span><a href="'.$eventLinkUrl.'">'.($hideEvents?'Show':'Hide').' events</a></span></p>'."\r\n";
		
		echo '<table class="log" id="log"><tr class="message"> <th class="time">TIME</th> <th class="user">NICK</th> <th class="text">MESSAGE</th></tr>'."\r\n";
		$timestampLength = 7;
		$suffixCharactersToRemove = array(')', '.');
		foreach ($lines as $line) {
			if (strlen($line) > 0) {
				$lineSections = explode(' ', $line);
				$messageType = 'message';
				$nickType = 'user';
				//if there isn't both a < and a >, it's not a nick but an action or join/quit message. Change how that looks
				if (strpos($lineSections[1], '&lt;') === FALSE or strpos($lineSections[1], '&gt;') === FALSE) {
					if ($lineSections[1] === '*') $messageType = 'action';
					else $messageType = 'other';
					$nickType = 'spacer';
				}
				//If we should hide events, just skip the echo-ing when it's a join or quit
				if ($hideEvents and $messageType === 'other') {
					continue;
				}
				$message = substr($line, $timestampLength + strlen($lineSections[1])+2);
				//Turn URLs into hyperlinks
				if (strpos($message, 'http') !== FALSE or strpos($message, 'www') !== FALSE) {
					preg_match_all("/(https?:\/\/\S+|www\.\S+\.\S+)/", $message, $regexResults);
					foreach ($regexResults[0] as $urlText) {
						//Remove some trailing characters that can mess up the url, like periods or parentheses						
						while (in_array(mb_substr($urlText, -1, 1), $suffixCharactersToRemove)) {
							$urlText = mb_substr($urlText, 0, -1);
						}
						
						//The display text can be different from the actual link it should be
						$url = $urlText;	
						//Make sure the url actually starts with 'http' if there's no protocol specified
						if (strpos($url, 'http://') !== 0 and strpos($url, 'https://') !== 0 and strpos($url, 'ftp://') !== 0) {
							$url = 'http://'.$url;
						}
						
						//Finally, actually change the text to a hyperlink
						$message = str_replace($urlText, '<a href="'.$url.'" target="_blank">'.$urlText.'</a>', $message);
					}
				}
				echo '<tr class="'.$messageType.'"><td class="time">'.$lineSections[0].'</td><td class="'.$nickType.'">'.$lineSections[1].'</td> <td class="text">'.$message.'</td></tr>'."\r\n";
			}
		}
		echo "</table>\r\n";
	}
}
?>
</body>
</html>
