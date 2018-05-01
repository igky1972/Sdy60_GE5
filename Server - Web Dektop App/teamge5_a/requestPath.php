<?php


/**
 * Αρχείο που αποκρίνεται σε μια ερώτηση POST του client με tag pathRequest.
 * Γυρίζει στον client το μονοπάτι που βρίσκεται μια κατάλληλη διαδρομή στον server για να το σχολιάσει ο χρήστης
 */
	 
	 
//Εάν υπάρχει ετικέτα (tag) - διάφορη του κενού που δείχνει τι λειτουργία θα πρέπει να ακολουθηθεί
//Εάν υπάρχει ετικέτα (tag) - διάφορη του κενού που δείχνει τι λειτουργία θα πρέπει να ακολουθηθεί
if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // Παίρνει την ετικέτα (tag)
    $tag = $_POST['tag'];

    // εισάγει τις συναρτήσεις χειρισμού της βάσης
	require_once 'include/DB_Functions.php';
	
    // response Array - το tag παίρνει την τιμή της αίτησης - στην αρχή δεν έχουμε ούτε επιτυχία, ούτε λάθος
    $response = array("tag" => $tag, "success" => 0, "error" => 0);
	
	// ελέγχει για τον τύπο του tag
    if ($tag == 'pathRequest') {
		
		// Request type is check Login
        $player_id = $_POST['playerID'];
		
		$result = randomPath($player_id);
	 	$path = $result[0];
		$path_id = $result[1];
		if ($path != false && $path_id != 0) {
			//  βρέθηκε μονοπάτι
            // echo json με success = 1
			$response["success"] = 1;
			$response["path"]["path"] = $path;
			$response["path"]["path_id"] = $path_id;
			
			echo json_encode($response,JSON_UNESCAPED_UNICODE);
		} else {
            // το μονοπάτι δεν βρέθηκε
            // echo json με error = 1
            $response["error"] = 1;
			if ($path_id == 0){
				$response["error_msg"] = "There are no paths for review!";
				}
			else{
            	$response["error_msg"] = "Incorrect playerid!";
			}
            echo json_encode($response,JSON_UNESCAPED_UNICODE);
        }
	}
	else {
        	echo "Invalid Request";
    }
} else {
    echo "Access Denied";
}

?>