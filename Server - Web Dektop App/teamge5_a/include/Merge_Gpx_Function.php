<?php

//Συνάρτηση που ενσωματώνει δεδομένα σε ένα αρχείο. Τα νέα δεδομένα θα αρχίσουν από την θέση position
function injectData($file, $data, $position) 
{
    $temp = fopen('php://temp', "rw+");
    $fd = fopen($file, 'r+b');

    fseek($fd, $position);
    stream_copy_to_stream($fd, $temp); // αντιγράφει το τέλος του αρχείου στο προσωρινό

    fseek($fd, $position); // seek back - γυρίζει πίσω στην θέση που θα μπουν τα νέα δεδομένα
    fwrite($fd, $data); // γράφει τα δεδομένα στο αρχικό αρχείο

    rewind($temp);//γυρίζει τη θέση του προσωρινού αρχείου στην αρχή
    stream_copy_to_stream($temp, $fd); // ξαναγράφει το τέλος

    fclose($temp);
    fclose($fd);
}

/*
//Γυρίζει το string με τα waypoints του αρχείου ή false αν δεν υπάρχουν
function wayPointsSegmentOfGpx($gpxFile){
	$searchWaypoints = '<wpt';//Αν υπάρχουν waypoints θα υπάρχει αυτό το tag
	$stringOfFile = file_get_contents($gpxFile);
	$startPosOfWaypoints = strpos($stringOfFile, $searchWaypoints);
	
	
	if ($startPosOfWaypoints){
		$searchEndOfWayPoints = '<trk>';
		$endPosOfWaypoints = strpos($stringOfFile, $searchEndOfWayPoints);
		$lengthOfWaypoints = $endPosOfWaypoints - $startPosOfWaypoints;
		$data = substr($stringOfFile,$startPosOfWaypoints,$lengthOfWaypoints);
		$file = "test.txt";
		file_put_contents($file, $data);
		//echo "true";
		$wayPointsExistsInNewFile = true;
	}
	else{
		$wayPointsExistsInNewFile = false;
		echo "false";
	}
	
}*/

//Συνάρτηση που ενσωματώνει κάθε νέο gpx αρχείο (newfilename) στο mergeFileName αρχείο. Αν το δεύτερο δεν υπάρχει το δημιουργεί και απλά
//αντιγράφει σε αυτό το νέο gpx αρχείο
function mergeGpxFiles($mergeFileName,$newfilename){
	//Αν το merge αρχείο δεν υπάρχει είναι η πρώτη φορά που ανέβηκε ένα gpx άρα αντίγραψε το σε ένα αρχείο με όνομα mergePaths.gpx
	if(!file_exists($mergeFileName)){
		copy($newfilename,$mergeFileName);
	}
	else{//Αλλιώς ενσωμάτωσε το νέο αρχείο στο mergePaths.gpx

		//Για την ενσωμάτωση των trksegments
		$allNewFilestring = file_get_contents($newfilename); //Ολόκληρο το νέο αρχείο σε string
		$searchForStartOfTrkSeg = '<trkseg>';//Από αυτό το tag και κάτω θέλουμε να κρατήσουμε από το νέο αρχείο
		$startPosOfTrkSeg = strpos($allNewFilestring, $searchForStartOfTrkSeg);//Η θέση από την οποία και κάτω θέλουμε να κρατήσουμε
		$searchForEndOfTrkSeg = '</trk>';//Μέχρι αυτό το tag θέλουμε να κρατήσουμε
		$endPosOfTrkSeg = strpos($allNewFilestring, $searchForEndOfTrkSeg);//Η θέση μέχρι την οποία θέλουμε να κρατήσουμε
		$lengthOfStringOfTrkSeg = $endPosOfTrkSeg - $startPosOfTrkSeg;//Το μήκος του string που θέλουμε να κρατήσουμε
		$stringMaintanedFromNewFileForTrkSeg = substr($allNewFilestring, $startPosOfTrkSeg,$lengthOfStringOfTrkSeg );//To string που θέλουμε να διατηρήσουμε από το νέο αρχείο
		$stringMaintanedFromNewFileForTrkSeg = "<name>Tracking by MapMaker</name>".$stringMaintanedFromNewFileForTrkSeg;//Το name θέλουμε να το προσθέσουμε πριν το trseg
	
		//Για την ενσωμάτωση των waypoints 
		$searchWaypoints = '<wpt';//Αν υπάρχουν waypoints θα υπάρχει αυτό το tag
		$startPosOfWaypoints = strpos($allNewFilestring, $searchWaypoints);//Η θέση που αρχίζουν τα waypoints αν υπάρχουν
		$wayPointsExistsInNewFile = false; //Στην αρχή θεωρούμε ότι δεν υπάρχουν wayPoints
		if ($startPosOfWaypoints){ //Αν υπάρχουν waypoints θα βρούμε το string που τα περιέχει
			$searchEndOfWayPoints = '<trk>';//Όταν αρχίζει το tag: trk τελειώνουν τα waypoints
			$endPosOfWaypoints = strpos($allNewFilestring, $searchEndOfWayPoints);//Η θέση που τελειώνουν τα waypoints
			$lengthOfWaypoints = $endPosOfWaypoints - $startPosOfWaypoints;//Το μήκος του τμήματος των waypoints
			$stringMaintanedFromNewFileForWayPoints = substr($allNewFilestring,$startPosOfWaypoints,$lengthOfWaypoints);//Το string που διατηρούμε
			$wayPointsExistsInNewFile = true;//Υπάρχουν waypoints στο νέο αρχείο
		}
	
		unset($allNewFilestring);//Για να ελευθερώσουμε χώρο
	
		$searchforEndOfTrk = '</trk>';//Πριν από αυτό το tag θα μπει το καινούργιο trkseg
		$mergeString = file_get_contents($mergeFileName);//Φορτώνει το merge αρχείο στην μεταβλητή $mergeString
		$positionForTrkSeg = strpos($mergeString, $searchforEndOfTrk); //Η θέση στην οποία θα ενσωματωθεί το καινούργιο trkseg
	
		if($wayPointsExistsInNewFile){//Η θέση που θα μπουν τα waypoints στο merge αρχείο μας απασχολεί μόνο αν υπάρχουν wpt στο νέο 
			$searchforEndOfWpts = '<trk>';//Πριν από αυτό το tag θα μπουν τα καινούργια wpts
			$positionForWpts = strpos($mergeString,$searchforEndOfWpts);
		}
	
		unset($mergeString);//Για ελευθέρωση μνήμης
	
		if($positionForTrkSeg) //αν βρέθηκε η θέση τότε ενσωμάτωσε το string που θέλουμε
		{
			injectData($mergeFileName, $stringMaintanedFromNewFileForTrkSeg, $positionForTrkSeg); //Η ενσωμάτωση του trkseg στο merge αρχείο
		}
	
		if($wayPointsExistsInNewFile && $positionForWpts)//Αν υπάρχουν waypoints τα βάζει στο νέο αρχείο
		{
			injectData($mergeFileName, $stringMaintanedFromNewFileForWayPoints, $positionForWpts); //Η ενσωμάτωση των waypoints στο merge αρχείο
		}
	}
}

?>