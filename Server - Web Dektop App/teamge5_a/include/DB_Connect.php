<?php
	
	//Δημιουργία συνάρτησης για να συνδεθούμε στην βάση που θέλουμε
    function connect() {
        require_once 'include/config.php';
        // connecting to mysql
        $mysqli = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD,DB_DATABASE);
      
        // γυρίζει τον χειριστή της βάσης
        return $mysqli;
    }

    
?>
