<?php

/**
 * File to handle API requests
 * Accepts GET and POST
 * 
 * Each request will be identified by TAG
 * Response will be JSON data

 /**
 * Αρχείο που χειρίζεται αιτήματα POST
 *
 * Κάθε αίτηση προσδιορίζεται με βάση την ετικέτα
 *
 * Οι εικέτες που χειρίζεται είναι: η "login" για σύνδεση, η "register" για εγγραφή και
 * η "storageFile" για την αποθήκευση των αρχείων gpx,
 *
 * Η απόκριση θα είναι δεδομένα JSON
 */
	 
	 
//Εάν υπάρχει ετικέτα (tag) - διάφορη του κενού που δείχνει τι λειτουργία θα πρέπει να ακολουθηθεί
if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // Παίρνει την ετικέτα (tag)
    $tag = $_POST['tag'];

    // εισάγει τις συναρτήσεις χειρισμού της βάσης
	require_once 'include/DB_Functions.php';
	
    // response Array - το tag παίρνει την τιμή της αίτησης - στην αρχή δεν έχουμε ούτε επιτυχία, ούτε λάθος
    $response = array("tag" => $tag, "success" => 0, "error" => 0);

    // ελέγχει για τον τύπο του tag
    if ($tag == 'login') {
        // Request type is check Login - Ο τύπος αιτήματος είναι ο έλεγχος εισόδου
        $email = $_POST['email'];
        $password = $_POST['password'];

        // ελέγχει για τον παίκτη
        $player = getPlayerByEmailAndPassword($email, $password);
        if ($player != false) {
            // ο παίκτης βρέθηκε
            // echo json με success = 1
            $response["success"] = 1;
            $response["uid"] = $player["uid"];
            $response["player"]["name"] = $player["name"];
            $response["player"]["email"] = $player["email"];
            $response["player"]["created_at"] = $player["created_at"];
            $response["player"]["updated_at"] = $player["updated_at"];
            echo json_encode($response,JSON_UNESCAPED_UNICODE);
        } else {
            // ο παίκτης δεν βρέθηκε
            // echo json με error = 1
            $response["error"] = 1;
            $response["error_msg"] = "Incorrect email or password!";
            echo json_encode($response,JSON_UNESCAPED_UNICODE);
        }
    } else if ($tag == 'register') {
        // Ο τύπος αιτήματος είναι εγγραφή νέου παίκτη
        $name = $_POST['name'];
        $email = $_POST['email'];
        $password = $_POST['password'];

        // ελέγχει αν ο παίκτης υπάρχει ήδη
        if (isPlayerExisted($email)) {
            // ο παίκτης υπάρχει ήδη - error response
            $response["error"] = 2;
            $response["error_msg"] = "User already existed";
            echo json_encode($response,JSON_UNESCAPED_UNICODE);
        } else {
            // εγγραφή παίκτη
            $player = storePlayer($name, $email, $password);
            if ($player) {
                // επιτυχής εγγραφή χρήστη
                $response["success"] = 1;
                $response["uid"] = $player["uid"];
                $response["player"]["name"] = $player["name"];
                $response["player"]["email"] = $player["email"];
                $response["player"]["created_at"] = $player["created_at"];
                $response["player"]["updated_at"] = $player["updated_at"];
                echo json_encode($response,JSON_UNESCAPED_UNICODE);
            } else {
                // ο παίκτης απέτυχε να εγγραφεί
                $response["error"] = 1;
                $response["error_msg"] = "Error occured in Registartion";
                echo json_encode($response,JSON_UNESCAPED_UNICODE);
            }
        }
    } else if($tag == 'storageFile'){
		
		// εισάγει τις συναρτήσεις απόρριψης των trkpt

		//Βλέπει το uid του τελευταίου path μέχρι τώρα
		$uidOfLastPathUntilNow = uidOfLatsPath();
		//Το αρχείο που ανέβηκε από τον client	- από τον gps provider
		$file = $_FILES['file'];
    	//Η σχετική διαδρομή που θα αποθηκευτούν τα αρχεία	
		//$file_path = "uploads/";
		$file_path = "uploads/";
	 	$temp = explode(".", $_FILES["file"]["name"]);//Στο $temp[0] θα περιέχεται το όνομα και στο $temp[1] η επέκταση του gps αρχείου
      	$extension = end($temp);
		//Ο αριθμός της διαδρομής που ανέβηκε τώρα
		$number_of_path = $uidOfLastPathUntilNow + 1;
        $temp[0] = "path".$number_of_path; 
        $fileName = $temp[0].".".$temp[1]; //το όνομα του gps αρχείου θα είναι path#
		//Η σχετική διαδρομή που θα αποθηκευτεί το αρχείο gps (μαζί με το όνομα του)	
		$file_path = $file_path . $fileName;
		//Αποθήκευση του αρχείου gps στον φάκελο uploads
	 	move_uploaded_file($_FILES['file']['tmp_name'], $file_path);
		//--------------------------------------------------------------------------------------
		//Θα αποθηκεύσουμε και το δεύτερο αρχείο από τον fused provider
		//Το δεύτερο αρχείο που ανέβηκε από τον client	
		$file2 = $_FILES['fileGoogle'];
		//Η σχετική διαδρομή που θα αποθηκευτούν τα αρχεία	
		$file_path2 = "uploads/";
		$temp2 = explode(".", $_FILES["fileGoogle"]["name"]);//Στο $temp[0] θα περιέχεται το όνομα και στο $temp[1] η επέκταση του αρχείου fused
		$extension2 = end($temp2);
		$temp2[0] = "pathGoogle".$number_of_path; 
		$fileName2 = $temp2[0].".".$temp2[1]; //το όνομα του fused αρχείου θα είναι pathGoogle#
		//Η σχετική διαδρομή που θα αποθηκευτεί το αρχείο (μαζί με το όνομα του)	
		$file_path2 = $file_path2 . $fileName2;
		//Αποθήκευση του αρχείου fused στον φάκελο uploads
	 	move_uploaded_file($_FILES['fileGoogle']['tmp_name'], $file_path2);
		//--------------------------------------------------------------------------------------
		$player_id=$_POST['player_id'];
		$meters=$_POST['meters'];
		$tags=$_POST['tagsOfPath'];
		$path_raw_gpx=$file_path;
		$path_raw_gpx_google=$file_path2;

        //Η απόρριψη και το smoothing θα γίνει στα fused αρχεία
        //Εδώ θα απορριπτούν κάποια σημεία -σύμφωνα με δύο συναρτήσεις- και θα δημιουργηθεί το προκύπτον αρχείο με όνομα: ονομα_mod
        require_once 'include/InacAndDPSmooth.php';

        $gpx =  discardFaultPointsOfGpxFile($file_path2);//Αφού θέλουμε το fused αρχείο
        $file_name_without_extension=pathinfo($file_path2,PATHINFO_FILENAME);
        $modified_file_name = "$file_name_without_extension"."_mod".".gpx";
        $path_smooth_gpx = "uploads/" . $modified_file_name;
        DouglasPeuckerSmoothing($gpx,$path_smooth_gpx,7,12);

		$new_path=true;
		//Αποθήκευση της διαδρομής στη βάση δεδομένων
		$path=storePath($player_id, $path_raw_gpx_google,$path_smooth_gpx,$path_raw_gpx,$tags,$meters,$new_path);
		
		
		
		if ($path != false){
			$response["success"] = 1;
			$response["message"] = "Path uploaded successfully";
			//echo json_encode($response,JSON_UNESCAPED_UNICODE);

            //Για να μην περιμένει ο χρήστης όσο γίνεται η προσπάθεια snap αλλά και merge
			ignore_user_abort(true);
			set_time_limit(0);

			ob_start();
			// do initial processing here
			//echo $response; // send the response
			echo json_encode($response,JSON_UNESCAPED_UNICODE);
			header('Connection: close');
			header('Content-Length: '.ob_get_length());
			ob_end_flush();
			ob_flush();
			flush();

            //εισάγει την συνάρτηση για merge snapWaypoints
            require_once 'include/snapWpt.php';
            snapWayPoints($path_smooth_gpx);

			// εισάγει την συνάρτηση για merge gpx
			require_once 'include/Merge_Gpx_Function.php';
			$mergeFileName = 'mergeFile/merge_gpx.gpx'; //Το gpx αρχείο που θα περιέχει όλες τις διαδρομές
		
			mergeGpxFiles($mergeFileName,$path_smooth_gpx);//Το καινούργιο smooth αρχείο θα μπει στο merge
			
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