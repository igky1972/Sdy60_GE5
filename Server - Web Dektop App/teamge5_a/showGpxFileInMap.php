<?php

    /**
    * Αρχείο που δέχεται την μονοπάτι μιας διαδρομής μέσα στον server και την εμφανίζει σε μια σελίδα HTML
    **/

	$path = $_GET['path'];
	
	//Για την περίπτωση που κάποιος παίκτης αιτηθεί όλες τις διαδρομές και δεν έχει ανέβει καμία
	if(!file_exists($path)){
		$path = "mergeFile/zero_gpx_files.gpx";
	}

?>

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">

        <title>Google Maps GPX Test - async</title>
        <style>
            html, body, #map {
                height: 100%;
                margin: 0px;
                padding: 0px
            }
        </style>
    
        <script type="text/javascript"
            src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js">
        </script>
        <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=false&key=AIzaSyCnu7GjQ6wqt1KX6Uv_3SdMfL8fkr5Dhkk"></script>

        <script src="gpxviewer-next/loadgpx.js" type="text/javascript"></script>
        <script type="text/javascript">
            //<![CDATA[

			
            function loadGPXFileIntoGoogleMap(map, filename) {
                $.ajax({url: filename,
                    dataType: "xml",
                    success: function(data) {
                      var parser = new GPXParser(data, map);
                      parser.setTrackColour("#ff0000");     // Set the track line colour - Ρυθμίζει το χρώμα της τροχιάς
                      parser.setTrackWidth(5);              // Set the track line width - Ρυθμίζει το πλάτος της τροχιάς
                      parser.setMinTrackPointDelta(0.0001); // Set the minimum distance between track points - Ρυθμίζει την ελάχιστη απόσταση μεταξύ δύο trackpoints
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
				var mapCanvas = document.getElementById("map");
				 mapCanvas.setAttribute( "style", "height:" + window.innerHeight + "px;" );
                var map = new google.maps.Map(mapCanvas,
                    mapOptions);
                loadGPXFileIntoGoogleMap(map, "<?php echo $path; ?>");
            });

        //]]>
</script>
</head>
<body>
    <div id="map"></div>
</body>
</html>
