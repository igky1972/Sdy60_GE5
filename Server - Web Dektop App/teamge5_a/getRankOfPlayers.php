<?php

/**
 * Αποκρίνεται σε μια ερώτηση του client με tag getRankOfPlayers.
 * Γυρίζει την κατάταξη όλων των παικτών
 */
if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // Παίρνει την ετικέτα (tag)
    $tag = $_POST['tag'];
	// εισάγει τις συναρτήσεις χειρισμού της βάσης
	require_once 'include/DB_Functions.php';


	// ελέγχει για τον τύπο του tag
    if ($tag == 'getRankOfPlayers') {
		$response = allPlayersInRank();
	
		echo json_encode($response,JSON_UNESCAPED_UNICODE);
	}
}
	
?>