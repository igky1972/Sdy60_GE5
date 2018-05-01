<?php

/**
 * Αρχείο που αποκρίνεται σε μια ερώτηση POST του client με tag storeReview για την αποθήκευση μιας κριτικής στην βάση δεδομένων
 * Γυρίζει αν έγινε η αποθήκευση μέσω μιας απάντησης JSON
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
    if ($tag == 'storeReview') {
		// Ο τύπος αιτήματος είναι εγγραφή νέας κριτικής
        $player_id = $_POST['player_id'];
        $path_id = $_POST['path_id'];
        $rated = $_POST['rated'];
        $rated_tags = $_POST['rated_tags'];
		
		// εγγραφή κριτικής
         $review = storeReview($player_id, $path_id, $rated,$rated_tags);
		 
		 if ($review != false){
			$response["success"] = 1;
			$response["message"] = "Review stored successfully.You gained 100 points.";
			echo json_encode($response,JSON_UNESCAPED_UNICODE);
		}else{
			$response["error"] = 1;
			$response["error_msg"] = "Oops! An error occurred.";
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