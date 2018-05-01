<?php #admin/restricted.php 
           #####[make sure you put this code before any html output]#####

//starting the session-έναρξη συνόδου
session_start();

//checking if a log SESSION VARIABLE has been set
//ελέγχει άμαι μια μεταβλητή συνόδου log έχει οριστεί
if( !isset($_SESSION['log']) || ($_SESSION['log'] != 'in') ){
        //if the user is not allowed, display a message and a link to go back to login page
        //αν δεν επιτρέπεται η είσοδος του χρήστη, εμφανίζει ένα μήνυμα και ένα σύνδεσμο για να γυρίσει στην σελίδα σύνδεσης
	echo "You are not allowed. <a href='admin.php'>back to login page</a>";
        
        //then abort the script-τότε ματαιώνει το script
	exit();
}

####  CODE FOR LOG OUT - Κώδικας για αποσύνδεση####
if(isset($_GET['log']) && ($_GET['log']=='out')){
        //if the user logged out, delete any SESSION variables
	session_destroy();
	
        //then redirect to login page - ανακατεύθυνση στην σελίδα σύνδεσης
	header('location:admin.php');
}//end log out
?> 
<!-- RESTRICTED PAGE HTML-Περιορισμένη σύνδεση HTML  -->
<!-- add a LOGOUT link before the form - προσθέτει ένα σύνδεσμο αποσύνδεσης -->
<p>{ <a href="?log=out">log out</a> }</p>

 <!-- RESTRICTED PAGE HTML- Περιορισμένη σύνδεση HTML -->
 <?php

	// εισάγει τις συναρτήσεις χειρισμού της βάσης
	require_once 'include/DB_Functions.php';
	
	$mysqli = connect();//σύνδεση με την βάση δεδομένων
	$result = mysqli_query($mysqli,"SELECT path_smooth_google_gpx AS path,uid AS id_of_path FROM paths WHERE created_at = (SELECT MAX(created_at)FROM paths)");
	//$path = $_GET['path'];
	if($result->num_rows === 0)
    {
        echo '<p style = "text-align:center" ><em> <strong> There is no path </strong> </em></p>';
    }
	$data= mysqli_fetch_assoc($result);
	$path = $data['path'];
	$id_of_path = $data['id_of_path'];
	//echo $path;
	if(!isset($_POST['go']) && !isset($_POST['submitState'])){
		$selected = $id_of_path;
	}elseif (!isset($_POST['go']))
	{
		$selected = $_POST['pathNumber'];
	}
	
	//$path ="uploads/pathGoogle1_maintained_mod.gpx";
	if(isset($_POST['go'])){
		$selected =$_POST['number_of_path'];
		$path_to_show=$_POST['number_of_path'];
		$result = mysqli_query($mysqli,"SELECT path_smooth_google_gpx AS path FROM paths WHERE uid = '$path_to_show'");
		$data= mysqli_fetch_assoc($result);
		$path = $data['path'];
		//echo $path;
	}
	

?>
<?php
 if(isset($_POST['submitState'])){
	 $stateOfPath=$_POST['state_of_path'];
	 //echo "yttytrty".$stateOfPath."<br>";
	 mysqli_query($mysqli,"UPDATE paths SET new_path='$stateOfPath',updated_at=NOW() WHERE uid='$selected'");
 }
?>



<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
        <title>Google Maps GPX Test - async</title>
        <style type="text/css">
            v\:* {
                behavior:url(#default#VML);
            }
        </style>

        <style type="text/css">
            html, body {width: 100%; height: 100%}
            body {margin-top: 0px; margin-right: 0px; margin-left: 0px; margin-bottom: 0px}
        </style>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
        <script src="gpxviewer-next/loadgpx.js" type="text/javascript"></script>
        <script type="text/javascript">
            //<![CDATA[

			
            function loadGPXFileIntoGoogleMap(map, filename) {
                $.ajax({url: filename,
                    dataType: "xml",
                    success: function(data) {
                      var parser = new GPXParser(data, map);
                      parser.setTrackColour("#ff0000");     //Set the track line colour - Ρυθμίζει το χρώμα της τροχιάς
                      parser.setTrackWidth(5);          //Set the track line width - Ρυθμίζει το πλάτος της τροχιάς
                      parser.setMinTrackPointDelta(0.0001);//Set the minimum distance between trackpoints - Ρυθμίζει την ελάχιστη απόσταση μεταξύ δύο trackpoints
                      parser.centerAndZoom(data);
                      parser.addTrackpointsToMap();         // Add the trackpoints - προσθέτει τα teackpoints
                      parser.addWaypointsToMap();           // Add the waypoints - προσθέτει τα waypoints
                    }
                });
            }

            $(document).ready(function() {
                var mapOptions = {
                  zoom: 8,
                  mapTypeId: google.maps.MapTypeId.ROADMAP
                };
                var map = new google.maps.Map(document.getElementById("map"),
                    mapOptions);
                loadGPXFileIntoGoogleMap(map, "<?php echo $path; ?>");
            });

        //]]>
</script>
</head>
<body>
	<div id="forms" style="width: 100%; height: 17%;">
    <table style="width:100%">
  <tr>
    <td><em>Select path Form</em><br>
    <form method="post" action="#">
    	Number of path<br>(ordered by date):
	<select id="path_id" name="number_of_path">
    	<?php
			$pathIDs = mysqli_query($mysqli,"SELECT uid FROM paths ORDER BY created_at DESC");
			while ($pathrow=mysqli_fetch_array($pathIDs)) {
            	$theNumberOfPath=$pathrow["uid"];
                //echo "<option>
                 //   $theNumberOfPath
                //</option>";
				echo "<option";
				if($selected == $theNumberOfPath){
					echo " selected>";
				}
				else{
					echo ">"; 
				}
				echo " $theNumberOfPath
                </option>";
				
            
            }
		?>
    </select>
    <p><button type="submit" name="go">select path</button></p>
</form></td>
<td>
<?php
	$result= mysqli_query($mysqli,"SELECT created_at AS dateOfPath, new_path FROM paths WHERE uid = '$selected' ");
	$data= mysqli_fetch_assoc($result);
	$dateOfSelectedPath=$data['dateOfPath'];
	$new_path=$data['new_path'];
	echo "<em>Selected path state</em><br>";
	echo "Number of path:".$selected."<br>";
	echo "Date and time of path:".$dateOfSelectedPath."<br>";
	if($new_path==1){
		echo "New path: true";
	}
	else{
		echo "New path: false";
		}
?>
</td>
    <td><em>Change state of selected path</em><br>
    	<?php echo "Selected path: ".$selected."<br>"; ?>
    <form method="post" action="#">
		
    <input type="hidden" name="pathNumber" value=<?php echo $selected; ?>>
    New path:
	<select id="path_state" name="state_of_path">
    	<option value="1" <?php if($new_path){echo "selected";} ?>>true</option>
        <option value="0"<?php if(!$new_path){echo "selected";} ?> >false</option>
    </select>
    <p><button type="submit" name="submitState">submit state</button></p>
</form></td>		
   
  </tr>
</table>
    </div>
    <div id="title" style="width: 100%; height: 3%; text-align:center;"><h4>Visualization of path:<?php echo " ".$selected; ?> (selected path). </h4></div>
    <div id="map" style="width: 100%; height: 80%;"></div>
</body>
</html>
