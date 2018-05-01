<?php

/**
 * Συνάρτηση για την απόρριψη ανακριβών σημείων ενός gpx αρχείου (discardFaultPointsOfGpxFile) καθώς και συνάρτηση
 * υλοποίησης του αλγόριθμου Douglas -Peucker (DouglasPeuckerSmoothing) - καθώς και βοηθητικές συναρτήσεις για την
 * υλοποίηση του αλγόριθμου.
 */

define("Re",6371000); // Earth radius in meters

//Παίρνει ως είσοδο ένα gpx αρχείο και γυρίζει ένα gpx xml αντικείμενο απαλλαγμένο από τα trkpt
//που έχουν HDOP > 47 και SAT < 4 (αν υπάρχουν)
function discardFaultPointsOfGpxFile($gpxfile){
    //Διώχνουμε τα σημεία που η ακρίβεια είναι χειρότρη από 5.5 και οι δορυφόροι είναι λιγότεροι από 4
    $gpx = simplexml_load_file($gpxfile);
    define("MAX_HDOP",47);//Το μεγαλύτερο HDOP που ανεχόμαστε
    define("MIN_SAT",4);//Τους λιγότερους δορυφόρους που ανεχόμαστε
    $j = 0;//ο δείκτης του πίνακα $discardsPoints που θα περιέχει τα σημεία που θα εξαλειφθούν
    $size = sizeof($gpx->trk->trkseg->trkpt);//πόσα trkpt έχει το gpx αρχείο
    $discardPoints = array();//Δηλώνουμε εδώ ότι το $discardPoints είναι πίνακας - ώστε να μην βγει μια εξαίρεση παρακάτω
    //Κοιτάει όλα τα trkpt και άμα η ακρίβεια δεν είναι τουλάχιστον  MAX_HDOP ή οι δορυφόροι είναι λιγότεροι από MIN_SAT
    //αποθηκεύει τον αριθμό του trkpt στον πίνακα $discardsPoints που περιέχει τα σημεία που θα εξαλειφθούν
    for ($i = 0; $i < $size; $i++){
        if(isset($gpx->trk->trkseg->trkpt[$i]->sat)){//σε περίπτωση που υπάρχει πληροφορία sat
            $sat=$gpx->trk->trkseg->trkpt[$i]->sat;
        }
        else{
            $sat =12;//μέγιστοι δορυφόροι στην θέα έτσι ώστε να μην μπορούν να σβήσει κάποιο trkpt για αυτόν τον λόγο
        }


        if($gpx->trk->trkseg->trkpt[$i]->hdop > MAX_HDOP || $sat < MIN_SAT){
            $discardPoints[$j]= $i;
            $j = $j + 1;
        }

    }

    if(sizeof($discardPoints) >= 1){//Απαραίτητο για να μην υπάρχει περίπτωση ατέρμονα βρόχου
        //Σβήνουμε τα σημεία που πρέπει να απορριπτούν. ΠΡΟΣΟΧΗ! Τα σβήνουμε από το τέλος προς την αρχή
        //γιατί ο πίνακας trkpt κάθε φορά που σβήνουμε ένα στοιχείο του αλλάζει μέγεθος!(Θα δημιουργόταν
        //σφάλμα αλλιώς
        for ($i = sizeof($discardPoints) - 1; $i >= 0 ; $i--){
            unset($gpx->trk->trkseg->trkpt[$discardPoints[$i]]);
        }
    }


    return $gpx;
}

//Extract the latitute and longitude as a tuple in radians from a <trkpt> element
function extract1($trkpt){

    $attrs = $trkpt ->attributes();

    $lat = (float)$attrs["lat"];
    $lon = (float)$attrs["lon"];

    $latInRad = pi() / 180 * $lat;

    $lonInRad= pi() / 180 * $lon;

    return array((float)$latInRad,(float)$lonInRad);

}

//Compute the great circle distance between two points given in polar
//coordinates and radians. The return value is in the same units as
//Re is defined.
function dist($p0, $p1){

    $a = pow(sin(($p1[0] - $p0[0])/2),2) + cos($p0[0]) * cos($p1[0]) * pow(sin(($p1[1] - $p0[1])/2),2);
    $c = 2 * atan2(sqrt($a), sqrt(1-$a));
    $di = Re * $c;

    return $di;
}

//Convert from polar to cartesian coordinates, radians to units of Re
function polcar($polarpt){

    $lat=$polarpt[0];
    $lon=$polarpt[1];

    $x = Re * cos($lat) * cos($lon);
    $y = Re * cos($lat) * sin($lon);
    $z = Re *sin($lat);
    $xyz = array($x,$y,$z);
    return $xyz;
}

//Convert from cartesian to polar coordinates, radians to units of Re
function carpol($xyz){

    $R = vectorL2Norm($xyz);

    $lat = asin((array_sum(array_map(function($a,$b) { return $a*$b; }, array(0,0,1), $xyz)))/$R);

    $xy = $xyz;
    $xy[2]=0;
    $xy_norm = vectorL2Norm($xy);

    $xy=vectorDevidedByValue($xy,$xy_norm);
    $lon = atan2($xy[1], $xy[0]);

    $pol[0] = $lat;
    $pol[1] = $lon;

    return $pol;
}

//Υλοποίηση της cross στην php
function crossProduct($v1, $v2) {
    $vR[0] =   ( ($v1[1] * $v2[2]) - ($v1[2] * $v2[1]) );
    $vR[1] = - ( ($v1[0] * $v2[2]) - ($v1[2] * $v2[0]) );
    $vR[2] =   ( ($v1[0] * $v2[1]) - ($v1[1] * $v2[0]) );

    return $vR;
}

//L2-norm of vector
function vectorL2Norm($v1){

    $norm =  sqrt( (pow($v1[0], 2)) + (pow($v1[1], 2)) + (pow($v1[2], 2)));

    return $norm;
}

function vectorDevidedByValue($v1,$value){
    $vP[0] = $v1[0] / $value;
    $vP[1] = $v1[1] / $value;
    $vP[2] = $v1[2] / $value;

    return $vP;
}

//take product of multipline one array with 3 indexes with a value
function valueTimesArray($v1,$value){

    $p[0]=$v1[0]*$value;
    $p[1]=$v1[1]*$value;
    $p[2]=$v1[2]*$value;

    return $p;
}

function array1RemoveArray2($v1,$v2){

    $v3[0]=$v1[0]-$v2[0];
    $v3[1]=$v1[1]-$v2[1];
    $v3[2]=$v1[2]-$v2[2];

    return $v3;
}

//Given a pair of polar coordinates and a third, find the shortest great circle
// distance from the third point to the great circle arc segment connecting
// the first two.
function greatcircle_point_distance($pair,$third){
    //Convert to cartesian coordinates for the vector math
    $cfirst = polcar(array($pair[0],$pair[1]));
    $clast = polcar(array($pair[2],$pair[3]));
    $cthird =polcar($third);

    //Project 'third' onto the great circle arc joining 'pair' along the
    //vector that is normal to the chord between 'pair'
    $crossFirstLast = crossProduct($cfirst,$clast);
    $vectorNorm = vectorL2Norm($crossFirstLast);
    $normal = vectorDevidedByValue($crossFirstLast,$vectorNorm);
    $dot_product_of_normal_ctird = array_sum(array_map(function($a,$b) { return $a*$b; }, $normal, $cthird));
    $intersect = array1RemoveArray2($cthird,valueTimesArray($normal,$dot_product_of_normal_ctird));
    $intersect_norm = vectorL2Norm($intersect);
    $RdividedByIntersectNorm = Re/$intersect_norm;
    $intersect = valueTimesArray($intersect,$RdividedByIntersectNorm);

    //Great circle distance from 'third' to its projection
    $d =dist( $third,carpol($intersect));

    //If the projection of 'third' is not between the shorter arc
    //connecting 'pair', we instead want the gc distance from 'third'
    //to the nearest of the two.
    $d0 = array_sum(array_map(function($a,$b) { return $a*$b; }, $intersect, $cfirst));
    $d1 = array_sum(array_map(function($a,$b) { return $a*$b; }, $intersect, $clast));
    $c = array_sum(array_map(function($a,$b) { return $a*$b; }, crossProduct($intersect,$cfirst), crossProduct($intersect,$clast)));

    if ($c < 0 and (($d0 >= 0 and $d1 >= 0) or ($d0 < 0 and $d1 < 0))){

        return $d;
    }
    else{
        return min(dist($third, array($pair[0],$pair[1])), dist($third, array($pair[2],$pair[3])));
    }
}

/*Examine a segment of gpx track and set 'keep' attributes on points
 as needed to stay within maxdistance and maxinterval.

 Given two kept trackpoints with no other kept points between them, the
 distance between the arc connecting these two points and any other
 trackpoints between them must be less than 'maxdistance'.

 Given two kept trackpoints, the distance between them should not be
 significantly greater than 'maxinterval'.(The $seg has passed as reference)*/
function process($seg,$maxdistance,$maxinterval){

    //$bnds is a pair of first and last trackpoint
    $first_key = key(array_slice( $seg, 0, 1, TRUE ));//The index of first object of $seg (first trkpt)
    $last_key = key(array_slice( $seg, -1, 1, TRUE ));//The index of last object of $seg (last trkpt)
    $firstpoint = extract1($seg[$first_key]);
    $lastpoint = extract1($seg[$last_key]);
    $bnds = array($firstpoint[0],$firstpoint[1],$lastpoint[0],$lastpoint[1]);

    //Find the point between this segment's endpoints that lies furthest
    //from the great circle arc between these endpoints
    $idx = null;
    $maxd = null;
    for($n=$first_key+1;$n<$last_key;$n++){
        $this_point = extract1($seg[$n]);
        $d = greatcircle_point_distance($bnds, $this_point);
        if ($maxd === null or $d > $maxd){
            $maxd = $d;
            $idx = $n;
        }
    }

    if($maxd > $maxdistance){
        //Keep this point if it is at least 'maxdistance' from the
        //connecting arc, and run 'process' on the two subsegments
        $seg[$idx]['keep'] = 'True';
        $firstSegmentLength = $idx - $first_key + 1;
        if ($firstSegmentLength>=3) {
            process((array_slice($seg, 0, $firstSegmentLength, TRUE)), $maxdistance, $maxinterval);
        }
        $secondSegmentLength = $last_key - $idx + 1;
        if($secondSegmentLength>=3) {
            process((array_slice($seg, -$secondSegmentLength, $secondSegmentLength, TRUE)), $maxdistance, $maxinterval);
        }
    }
    elseif($maxinterval > 0){
        //This segment is good enough in terms of direction of travel.
        //A maximum distance between points may also be desired, however,
        //so loop through all remaining discarded points and add as needed.
        $prev = array($bnds[0],$bnds[1]);
        $fin = array($bnds[2],$bnds[3]);
        for($i=$first_key;$i<=$last_key;$i++){
            $this_point = extract1($seg[$i]);
            if(dist($prev,$this_point) > $maxinterval and dist($fin,$this_point) > $maxinterval){
                //Note that this does not satisfy the 'maxinterval' limit,
                //but instead takes the next point just further than the
                //given limit.
                //FIX ME might be better to take the previous point, to make
                //the limit a guaranteed one.
                $seg[$i]['keep'] = 'True';
                $prev = $this_point;
            }
        }
    }

    return $seg;
}

function DouglasPeuckerSmoothing($gpx,$gpxfile,$maxdistance,$maxinterval){
    // $file_path_without_file = "uploads/";
    // $file = "pathGoogle8.gpx";
    // $file_path = $file_path_without_file . $file;
    //$gpx = simplexml_load_file($gpxfile);

    //<gpx> contains a <trk> element, which contains a <trkseg> element, which contains <trkpt> elements
    //Add a 'keep' attribute to each <trkptp> element in the <trkseg>
    $sizeOfTrkpts = sizeof($gpx->trk->trkseg->trkpt);
    for ($i = 0; $i <= $sizeOfTrkpts - 1; $i++) {
        $gpx->trk->trkseg->trkpt[$i]->addAttribute('keep', 'False');
    }

    //We assume we need the first and last point
    //TO Do make this configurable, to allow processing of only
    //part of a track
    $gpx->trk->trkseg->trkpt[0]['keep'] = 'True';
    $gpx->trk->trkseg->trkpt[$sizeOfTrkpts - 1]['keep'] = 'True';


    //make an array of objects trackpoints (for easy process).$gpx changed if $seq has changed
    for ($i = 0; $i <= $sizeOfTrkpts - 1; $i++) {
        $seq[$i] = $gpx->trk->trkseg->trkpt[$i];
    }

    //The process will change keep atributes in trackpoints in gpx.
    process($seq, $maxdistance, $maxinterval);

    //Store the indexes of trackpoints which must remove
    $j = 0;
    for ($i = 0; $i <= $sizeOfTrkpts - 1; $i++) {
        if ($gpx->trk->trkseg->trkpt[$i]['keep'] == 'False') {
            $discardTrackPoints[$j] = $i;
            $j = $j + 1;
        }
    }

    //Remove the unneeded trackpoints based on the 'keep' attribute
    if (sizeof($discardTrackPoints) >= 1) {//Απαραίτητο για να μην υπάρχει περίπτωση ατέρμονα βρόχου
        //Σβήνουμε τα σημεία που πρέπει να απορριπτούν. ΠΡΟΣΟΧΗ! Τα σβήνουμε από το τέλος προς την αρχή
        //γιατί ο πίνακας trkpt κάθε φορά που σβήνουμε ένα στοιχείο του αλλάζει μέγεθος!(Θα δημιουργόταν
        //σφάλμα αλλιώς
        for ($i = sizeof($discardTrackPoints) - 1; $i >= 0; $i--) {
            unset($gpx->trk->trkseg->trkpt[$discardTrackPoints[$i]]);
        }
    }

    //Remove the 'keep' attribute
    $newSizeOfTrkpts = sizeof($gpx->trk->trkseg->trkpt);
    if ($newSizeOfTrkpts >= 1) {//Απαραίτητο για να μην υπάρχει περίπτωση ατέρμονα βρόχου
        //Σβήνουμε τα σημεία που πρέπει να απορριπτούν. ΠΡΟΣΟΧΗ! Τα σβήνουμε από το τέλος προς την αρχή
        //γιατί ο πίνακας trkpt κάθε φορά που σβήνουμε ένα στοιχείο του αλλάζει μέγεθος!(Θα δημιουργόταν
        //σφάλμα αλλιώς
        for ($i = $newSizeOfTrkpts - 1; $i >= 0; $i--) {
            unset($gpx->trk->trkseg->trkpt[$i]['keep']);
        }
    }

    //Write out the modified GPX file
    //$fileName=pathinfo($gpxfile,PATHINFO_FILENAME);
    //$modifiedFileName = "$fileName"."_mod".".".pathinfo($gpxfile,PATHINFO_EXTENSION);
    //$file_path_without_file = pathinfo($gpxfile,PATHINFO_DIRNAME);
    //$modifiedFile_path = $file_path_without_file."/".$modifiedFileName;
    $gpx->asXml("$gpxfile");
    unset($gpx);
    unset($seq);
}

?>