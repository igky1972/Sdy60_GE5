<?php
/**
 * php σελίδα για την είσοδο των διαχειριστών
 */
#admin/index.php

//σύνδεση στην βάση δεδομένων
$dbc = mysqli_connect('localhost','root','root') or 
           die('could not connect: '. mysqli_connect_error());

//επιλογή βάσης δεδομένων
mysqli_select_db($dbc, 'teamge5_a') or die('no db connection');

//check if the login form has been submitted -ελέγχει αν η φόρμα σύνδεσης έχει υποβληθεί
if(isset($_POST['go'])){
	#####form submitted, check data...- η φόρμα έχει υποβληθεί, έλεγξε τα δεδομένα...#####
	
        //step 1a: sanitise and store data into vars (storing encrypted password)
        //βήμα 1α: "αποστείρωση δεδομένων" και αποθήκευση τους σε μεταβλητές  (αποθήκευση κρυπτογραφημένου κωδικού πρόσβασης)
	$usr = mysqli_real_escape_string($dbc, htmlentities($_POST['u_name']));
	$psw = SHA1($_POST['u_pass']) ; //using SHA1() to encrypt passwords-χρησιμοποιεί την SHA1 () για την κρυπτογράφηση των κωδικών πρόσβασης
     
        //step2: create query to check if username and password match
        //Βήμα 2: Δημιουργεί το ερώτημα για να ελέγξει εάν το όνομα χρήστη και ο κωδικός πρόσβασης ταιριάζουν
	$q = "SELECT * FROM users WHERE username='$usr' AND password='$psw'  ";
	
	//step3: run the query and store result
    //Βήμα 3: Εκτελεί το ερώτημα και αποθηκεύει το αποτέλεσμα
	$res = mysqli_query($dbc, $q);

	//make sure we have a positive result
    //Βεβαιώνεται ότι έχουμε ένα θετικό αποτέλεσμα
	if(mysqli_num_rows($res) == 1){
		#########  LOGGING IN  ##########
		//starting a session
        //Έναρξη μιας συνόδου
                session_start();

                //creating a log SESSION VARIABLE that will persist through pages
                //Δημιουργία μιας μεταβλητής συνόδου log που θα διατηρηθεί στις σελίδες
		$_SESSION['log'] = 'in';

		//redirecting to restricted page
        //ανακατεύθυνση στην περιορισμένη σελίδα
		header('location:restricted.php');
	} else {
                //create an error message
                //δημιουργία ενός μηνύματος σφάλματος
		$error = 'Wrong details. Please try again';	
	}
}//end isset go
?> 
<!-- Η φόρμα HTML -->
<!-- LOGIN FORM in: admin/index.php - Φόρμα login-->
<form method="post" action="#">
    <p><label for="u_name">username:</label></p>
    <p><input type="text" name="u_name" value=""></p>
    
    <p><label for="u_pass">password:</label></p>
    <p><input type="password" name="u_pass" value=""></p>
    
    <p><button type="submit" name="go">log me in</button></p>
</form>
<!-- A paragraph to display eventual errors - Μια παράγραφο για να εμφανίσει τα ενδεχόμενα λάθη-->
<p><strong><?php if(isset($error)){echo $error;}  ?></strong></p> 