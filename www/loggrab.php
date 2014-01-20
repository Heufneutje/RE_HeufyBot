<?php header('Content-type: text/html; charset=utf-8'); ?>
<!DOCTYPE html>
<html>
<head>
<title>Log Prettifier</title>
</head>
<body>
<?php
if (!isset($_GET['channel'])) echo "No channel name found!";
elseif(!isset($_GET['date'])) echo "No date provided!";
else {
        //echo "Channel name: " . $_GET['channel']."<br />";
        //echo "Requested date: ".$_GET['date']."<br />";
        $log = file_get_contents("#".$_GET['channel']."/".$_GET['date'].".log");
        if ($log == FALSE) echo "Error while trying to open file";
        else {
                $log = str_replace("<", "&lt;", $log); //Prevent nicknames from being interpreted as (bogus) HTML tags
                $lines = explode("\n", $log);
                echo "<table><tr> <td>TIME</td> <td>NICK</td> <td>MESSAGE</td></tr>";
                $timestampLength = 7;
                foreach ($lines as $line) {
                        $lineSections = explode(' ', $line);
                        echo "<tr><td>".$lineSections[0]."</td><td>".$lineSections[1]."</td>";
                        echo "<td>".substr($line, $timestampLength + strlen($lineSections[1])+2)."</td></tr>";
                }
                echo "</table>";
        }
}
?>
</body>
</html>

