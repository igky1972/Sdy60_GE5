<?php

/**
 * Συναρτήσεις για την επικοινωνία του server με την βάση δεδομέων
 */

        //Χρησιμοποιείται για την σύνδεση στην βάση
       require_once 'DB_Connect.php';
      
		
    
    /**
     * Αποθηκεύει έναν νέο παίκτη
     * επιστρέφει τα στοιχεία του παίκτη
     */
    function storePlayer($name, $email, $password) {
		
		$mysqli = connect();//σύνδεση με την βάση δεδομένων
		//Γυρίζει ένα μοναδικό id βασιζόμενο στο microtime (τρέχουσα ώρα σε μικροδευτερόλεπτα)
       // $uuid = uniqid('', true);//Η πρώτη παράμετρος βάζει πρόθεμα (εδώ τίποτα) ενώ η δεύτερη κάνει το αποτέλεσμα πιο μαναδικό (23 χαρακτήρων)
        $hash = hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // κρυπτογραφιμένο password
        $salt = $hash["salt"]; // salt
        $result = mysqli_query($mysqli,"INSERT INTO players( name, email, encrypted_password, salt, created_at) VALUES( '$name', '$email', '$encrypted_password', '$salt', NOW())");//Η NOW () επιστρέφει την τρέχουσα ημερομηνία και ώρα.
        // ελέγχει για επιτυχή εγγραφή
        if ($result) {
            // παίρνει τα στοιχεία του παίκτη 
            $uid = mysqli_insert_id($mysqli); // τελευταίο εισαγόμενο id
            $result = mysqli_query($mysqli,"SELECT * FROM players WHERE uid = $uid");
            // επιστρέφει τα στοιχεία του παίκτη
			$result = mysqli_fetch_array($result);
			mysqli_close($mysqli);
            return $result;
        } else {
			mysqli_close($mysqli);
            return false;
        }
    }

    /**
     * Λαμβάνει τον παίκτη μέσω του email και του password
     */
    function getPlayerByEmailAndPassword($email, $password) {
		$mysqli = connect();//σύνδεση με την βάση δεδομένων
		
        $result = mysqli_query($mysqli,"SELECT * FROM players WHERE email = '$email'") or die(mysqli_error());
        // ελέγχει για αποτέλεσμα 
        $no_of_rows = mysqli_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysqli_fetch_array($result);
            $salt = $result['salt'];
            $encrypted_password = $result['encrypted_password'];
            $hash = checkhashSSHA($salt, $password);
			mysqli_close($mysqli);
            // ελέγχει αν ο κωδικός είναι ο ίδιος
            if ($encrypted_password == $hash) {
                // τα στοιχεία ταυτότητας του παίκτη είναι σωστά
                return $result;
            }
        } else {
            //ο παίκτης δεν βρέθηκε
			mysqli_close($mysqli);
            return false;
        }
    }

    /**
     * Ελέγχει αν ο παίκτης υπάρχει ή όχι
     */
   function isPlayerExisted($email) {
	   
	   	$mysqli = connect();//σύνδεση με την βάση δεδομένων
	   
        $result = mysqli_query($mysqli,"SELECT email from players WHERE email = '$email'");
        $no_of_rows = mysqli_num_rows($result);
		mysqli_close($mysqli);
        if ($no_of_rows > 0) {
            // ο παίκτης υπάρχει 
            return true;
        } else {
            //ο παίκτης δεν υπάρχει
            return false;
        }
    }

    /**
     * Κρυπτογράφηση κωδικού πρόσβασης
     * @param password
     * γυρίζει salt και κρυπτογραφημένο password
     */
     function hashSSHA($password) {

		//Η συνάρτηση SHA1() υπολογίζει τον κατακερματισμό (hash) SHA-1 μιας συμβολοσειράς (string).Εδώ του ακέραιου που γυρίζει η rand()
        $salt = sha1(rand());//Η συνάρτηση rand () δημιουργεί ένα τυχαίο ακέραιο
        $salt = substr($salt, 0, 10);//Γυρίζει τους 10 πρώτους χαρακτήρες.
		
		//Κωδικοποιεί τα δεδομένα με base64. Επειδή στην sha1 βάλαμε true θα γυρίσει 20 ακατέργαστους (raw) χαρακτήρες σε δυαδική μορφή 
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);//πίνακας που στο salt περιέχει το salt και στο encryped τον κρυπτογραφημένο κωδ.
        return $hash;//Γυρίζει τον πίνακα
    }

    /**
     * Αποκρυπτογράφηση κωδικού πρόσβασης
     * @param salt, password
     * γυρίζει hash string
     */
    function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }

	/**
     * Αποθηκεύει έναν νέο μονοπάτι
     * επιστρέφει τα στοιχεία του μονοπατιού
     */
    function storePath($player_id, $path_raw_gpx_google,$path_smooth_gpx,$path_raw_gpx,$tags,$meters,$new_path) {
		
		$mysqli = connect();//σύνδεση με την βάση δεδομένων
		
        $result = mysqli_query($mysqli,"INSERT INTO paths( player_id, path_raw_google_gpx, path_smooth_google_gpx,path_raw_gps_gpx,tags,meters, new_path, created_at) VALUES(	'$player_id', '$path_raw_gpx_google', '$path_smooth_gpx','$path_raw_gpx', '$tags', '$meters', '$new_path', NOW())");//Η NOW () επιστρέφει την τρέχουσα ημερομηνία και ώρα.
        // ελέγχει για επιτυχή εγγραφή
        if ($result) {
			mysqli_close($mysqli);
            return $result;
        } else {
			mysqli_close($mysqli);
            return false;
        }
    }
	
	 /**
     * Γυρίζει πόσα μονοπάτια έχουν αποθηκευτεί μέχρι τώρα (τελικά δεν χρησιμοποιήθηκε)
     */
   function numberOfPaths() {
	   
	   $mysqli = connect();//σύνδεση με την βάση δεδομένων
	   $result = mysqli_query($mysqli,"SELECT COUNT(uid) AS total FROM paths");
		
		if ($result) {
			$data= mysqli_fetch_assoc($result);
			$no_of_rows = $data['total'];
			mysqli_close($mysqli);
            return $no_of_rows;;
        } else {
			mysqli_close($mysqli);
            return false;
        }
    }

    /**
    * Γυρίζει το uid του τελευταίου μονοπατιού που έχει αποθηκευτεί μέχρι τώρα (Θα χρησιμοποιηθεί για το όνομα του νέου μονοπατιού)
    */

function uidOfLatsPath() {

    $mysqli = connect();//σύνδεση με την βάση δεδομένων
    $result = mysqli_query($mysqli,"SELECT uid AS lastUid FROM paths ORDER BY uid DESC LIMIT 1 ");

    if ($result) {
        $data= mysqli_fetch_assoc($result);
        $last_uid = $data['lastUid'];
        mysqli_close($mysqli);
        return $last_uid;
    } else {
        mysqli_close($mysqli);
        return false;
    }
}
	
	/**
     * Γυρίζει την διαδρομή ενός τυχαίου μονοπατιού που δεν το έχει δημιουργήσει ο χρήστης αλλά και 
	 * δεν το έχει σχολιάσει. Επίσης γυρίζει το uid της διαδρομής. Δηλαδή γυρίζει έναν πίνακα που στο στοιχείο 0
	 * βρίσκεται η διαδρομή του τυχαίου μονοπατιού και στο στοιχεί 1 βρίκσεται το uid αυτής της διαδρομής
     */
   function randomPath($player_id) {
	   
	   $mysqli = connect();//σύνδεση με την βάση δεδομένων
	   /*$result = mysqli_query($mysqli,"SELECT path_raw_gpx AS path,paths.uid AS id FROM paths,reviews WHERE paths.player_id != '$player_id' AND reviews.player_id != '$player_id' AND paths.uid = reviews.path_id ORDER BY RAND() LIMIT 1");*/
		$result = mysqli_query($mysqli,"SELECT pathsNotFromUserTable.path_smooth_google_gpx AS path,pathsNotFromUserTable.uid AS id FROM(SELECT * FROM paths WHERE paths.player_id != '$player_id')pathsNotFromUserTable WHERE pathsNotFromUserTable.uid NOT IN (SELECT path_id from reviews where player_id = '$player_id') ORDER BY RAND() LIMIT 1");
		if ($result) {
			$data= mysqli_fetch_assoc($result);
			if (is_null($data['path'])){
				$id = 0;
				$path = "does not exist";
				}
				
			$path = $data['path'];
			$id = $data['id'];
			
			mysqli_close($mysqli);
            //return $path;
			return array($path,$id);
        } else {
			mysqli_close($mysqli);
            return false;
        }
    }
	
	/**
     * Αποθηκεύει μία νέα κριτική
     * επιστρέφει τα στοιχεία της κριτικής
     */
    function storeReview($player_id, $path_id,$rated,$rated_tags) {
		
		$mysqli = connect();//σύνδεση με την βάση δεδομένων
		
        $result = mysqli_query($mysqli,"INSERT INTO reviews( player_id, path_id, rated,rated_tags, created_at) VALUES( 							'$player_id', '$path_id', '$rated','$rated_tags', NOW())");//Η NOW () επιστρέφει την τρέχουσα ημερομηνία και ώρα.
        // ελέγχει για επιτυχή εγγραφή
        if ($result) {
			mysqli_close($mysqli);
            return $result;
        } else {
			mysqli_close($mysqli);
            return false;
        }
    }

	/**
     * Γυρίζει τους παίκτες ταξινομημένους κατά
     * πόντους, μέσα σε τον πίνακα response. Αν έχουν βρεθεί
	 * παίκτες, το $response["success"] = 1, και το response["players"]
	 * περιέχει τους παίκτες, αλλιώς το response["success"] = 0
     */
    function allPlayersInRank() {
		
		$mysqli = connect();//σύνδεση με την βάση δεδομένων
		
        $result = mysqli_query($mysqli,"SELECT id_of_player, name, sum(points) as total_points

FROM (

  (SELECT players.uid as id_of_player, name, sum( meters ) * 0.05 AS points

     FROM paths, players

     WHERE players.uid = paths.player_id

     GROUP BY id_of_player

   )

  UNION ALL

 

  (SELECT players.uid as id_of_player, name, sum((tags)*20 + (CASE WHEN new_path=1 THEN 50 ELSE 0 END)) AS points

     FROM paths, players

     WHERE players.uid = paths.player_id

     GROUP BY id_of_player

  )

 

  UNION ALL

 

   (SELECT players.uid as id_of_player, name, sum((CASE WHEN rated=1 THEN -30 WHEN rated=2 THEN -15 WHEN rated=3  THEN 0

        WHEN rated=4 THEN 30 WHEN rated=5 THEN 60 ELSE 0 END)) AS points

        FROM reviews, players, paths

        WHERE players.uid = paths.player_id AND paths.uid = reviews.path_id

       GROUP BY id_of_player

     )

 

    UNION ALL

      (SELECT  players.uid as id_of_player,  name, count(player_id)*150 AS points

      FROM reviews, players

      WHERE players.uid = reviews.player_id

      GROUP BY id_of_player

       )


	UNION ALL

	(SELECT players.uid AS id_of_player, name, sum( (

        CASE WHEN rated_tags =1 THEN -40
            WHEN rated_tags =2  THEN -20
            WHEN rated_tags =3 THEN 0
            WHEN rated_tags =4 THEN 40
            WHEN rated_tags =5 THEN 80
            ELSE 0
            END) ) AS points
            FROM reviews, players, paths
            WHERE players.uid = paths.player_id
            AND paths.uid = reviews.path_id
            AND paths.tags <>0
          GROUP BY id_of_player
          )

	UNION ALL

	  (SELECT players.uid AS id_of_player, name, 0 AS points
	   
	  FROM players 
	  
	  GROUP BY id_of_player
	  
	  )
 
) AS table_union

GROUP BY id_of_player

ORDER BY total_points DESC, name ASC");

	$no_of_rows = mysqli_num_rows($result);
	
	
        // ελέγχει για επιτυχή εγγραφή
        if ($no_of_rows > 0) {
			
			// looping through all results
    		// players node - βρόχος όλων των αποτελεσμάτων των απικτών
    		$response["players"] = array();
			
			 while ($row = mysqli_fetch_array($result)) {
       			 // temp player array - προσωρινός πίνακας παικτών
        		$player = array();
        		$player["id_of_player"] = $row["id_of_player"];
        		$player["name"] = $row["name"];
        		$player["total_points"] = $row["total_points"];
        		
 
        		// push single player into final response array-βάζει έναν παίκτη στον τελικό πίνακα απόκρισης
        		array_push($response["players"], $player);
    		}
			// success- σε περίπτωση επιτυχίας
    		$response["success"] = 1;
			mysqli_close($mysqli);
            return $response;
        } else {
			// no players found- αν δεν βρέθηκαν παίκτες
    		$response["success"] = 0;
    		$response["message"] = "No players found";
			
			mysqli_close($mysqli);
            return $response;
        }
		return $result;
    }


?>
