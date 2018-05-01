<?php

/**
 * Παίρνει το id του παίκτη (που έχει ζητήσει μια διαδρομή για να κριτικάρει) μέσω μιας ερώτησης
 * http του client και γυρίζει στον client το μονοπάτι μιας κατάλληλης διαδρομής μέσα στον server
 */
 	// εισάγει τις συναρτήσεις χειρισμού της βάσης
	require_once 'include/DB_Functions.php';
	
	 $player_id = $_GET['player_id'];
	 
	 $path = randomPath($player_id);
	 echo $path;
	
?>