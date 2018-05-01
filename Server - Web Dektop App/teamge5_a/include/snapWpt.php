<?php

/**
 * Συνάρτηση που δέχεται ένα gpx αρχείο και διορθώνει τα waypoints βάζοντας τα στο δρόμο σύμφωνα με το
 * Directions API της Google
 */

function snapWayPoints($gpxfile){
    $gpx = simplexml_load_file($gpxfile);
    $size = sizeof($gpx->wpt);

    for ($i = 0; $i <= $size - 1; $i++) {

        $point1 = $gpx->wpt[$i];
        $attrs1 = $point1->attributes();
        $lat1 = (float)$attrs1["lat"];
        $lon1 = (float)$attrs1["lon"];
        $stringRequest = 'https://maps.googleapis.com/maps/api/directions/json?origin=' . $lat1 . ',' . $lon1 . '&destination=' . $lat1 . ',' . $lon1 . '&mode=walking&key=AIzaSyCnu7GjQ6wqt1KX6Uv_3SdMfL8fkr5Dhkk';

        $response = file_get_contents($stringRequest);

        $obj = json_decode($response);

        if($obj->status == "OK" ) {
            $snapLatitude = $obj->routes[0]->legs[0]->start_location->lat;
            $snapLongitude = $obj->routes[0]->legs[0]->start_location->lng;
            
            $gpx->wpt[$i]["lat"] = $snapLatitude;
            $gpx->wpt[$i]["lon"] = $snapLongitude;
        }
        sleep(0.51); //περιμένει 0.51 δευτερόλπτα αφού το Direction API έχει τον περιορισμό δύο ερωτήσων ανά δευτερόλεπτο

    }
    $gpx->asXml($gpxfile);
}
?>