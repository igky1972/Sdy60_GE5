# PathsOnMap-Corfu-server

Το PathsOnMap Corfu είναι μια εφαρμογή Android που σκοπό έχει να προτρέψει τους χρήστες να ανεβάζουν τις διαδρομές που κάνουν με τα πόδια, στο Ρέθυμνο.
Αυτό πετυχαίνεται με την παιχνιδοποίηση της εφαρμογής. Ο τελικός στόχος είναι να δημιουργηθεί ένας χάρτης πεζών για το Ρέθυμνο. 
Εδώ παρουσιάζεται ο server της εφαρμογής.
Αυτός αποθηκεύει τις διαδρομές σε ένα φάκελο με όνομα upload.
Επίσης αποθηκεύει τα προφίλ των χρηστών που συνδέονται στην εφαρμογή, τα χαρακτηριστικά των διαδρομών που ανεβάζουν και τις κριτικές που κάνουν στις διαδρομές σε μία βάση δεδομένων.
Επίσης, εξομαλύνει της διαδρομές που ανεβάζουν οι χρήστες:
![enter image description here](https://cloud.githubusercontent.com/assets/3535061/8783749/75ae2142-2f26-11e5-94ee-0a592a9f80ba.gif)  
 Επιπλέον, δίνει τη δυνατότητα να εμφανίζεται ο χάρτης πεζών που έχει δημιουργηθεί, από μια σελίδα HTML:  
 ![enter image description here](https://cloud.githubusercontent.com/assets/3535061/8783756/855c0bfe-2f26-11e5-9ee1-b3ac5172f528.png)
Τέλος, δίνεται η δυνατότητα στους διαχειριστές να δουν και να σχολιάσουν τις διαδρομές που ανεβάζουν οι χρήστες, ως νέες ή παλιές, μέσα από μια σελίδα HTML:
![enter image description here](https://cloud.githubusercontent.com/assets/3535061/8782735/1ade4356-2f20-11e5-9903-cdacd197e8ef.png)

##Τεχνολογίες και εργαλεία

 - Apache
 - PHP
 - MySQL
 - HTML 5
 - CSS 3
 - JavaScript
 - JQuery 
 - Google Maps JavaScript API 

Ο server εκτελείται αυτή την στιγμή στο: http://83.212.111.234/teamge5_a/ :
![enter image description here](https://cloud.githubusercontent.com/assets/3535061/8783759/8d23c20a-2f26-11e5-96f6-0c268f817e3e.png)

##Κατασκευή του κώδικα τοπικά στον υπολογιστή σας

 1. Εγκατάσταση [WampServer](http://www.wampserver.com/en/)
 2. Τρέξτε τον WampServer και επιλέξτε Put Online
 3. Καταβάστε (download zip) τον κώδικα από: https://github.com/ippokratis1/PathsOnMap-Corfu-server.git
 4. Αποσυμπιέστε τον φάκελο
 5. Αλλάξτε το όνομα του root directory (PathsOnMap-Corfu-server-master) σε mapmaker_local
 6. Kάντε αποκοπή επικόληση του φακέλου στον φάκελο www του WampServer που βρίσκεται στην διαδρομή c:/wamp/www
 7. Ανοίξτε ένα browser και πηγαίνετε στην διεύθυνση: localhost. Από την σελίδα που θα εμφανιστεί επιλέξτε phpmyadmin
 8. Δημιουργήστε μια νέα βάση δεδομένων επιλέγοντας Νέα και στην συνέχεια γράψτε για όνομα της βάσης δεδομένων: pa277408_mapmaker2 και για σύνθεση: utf8_general_ci και κάντε κλικ στο κουμπί *Δημιουργία*
 9. Επιλέξτε την βάση που δημιουργήσατε και πατήστε στην ετικέτα *Δικαιώματα*. Στην συνέχεια επιλέξτε *Προσθήκη χρήστη*. Από εκεί γράψτε για όνομα χρήστη: pa277408_ippokra, για φιλοξενητή: localhost και για κωδικό πρόσβασης: !lissos78. Επιλέξτε από Βάση δεδομένων για χρήστη: πλήρη διακιώματα και από Γενικά δικαιώματα: Επιλογή όλων. Μετά πατήστε στο κουμπί: *Εκτέλεση*
 10. Επιλέξτε την ετικέτα Κώδικας SQL και τρέξτε το ερώτημα SQL: 

>-- Δομή πίνακα για τον πίνακα `paths`
>--
>
>CREATE TABLE IF NOT EXISTS `paths` (
>  `uid` int(11) NOT NULL AUTO_INCREMENT,
>  `player_id` int(11) NOT NULL,
>  `path_raw_google_gpx` varchar(100) NOT NULL,
>  `path_smooth_google_gpx` varchar(100) NOT NULL,
>  `path_raw_gps_gpx` varchar(100) NOT NULL,
>  `tags` int(11) NOT NULL,
>  `meters` int(11) NOT NULL,
>   `new_path` tinyint(1) DEFAULT '1',
>  `created_at` datetime DEFAULT NULL,
>  `updated_at` datetime DEFAULT NULL,
>  PRIMARY KEY (`uid`)
>) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;
>
>-- --------------------------------------------------------
>
>--
>-- Δομή πίνακα για τον πίνακα `players`
>--
>
>CREATE TABLE IF NOT EXISTS `players` (
>  `uid` int(11) NOT NULL AUTO_INCREMENT,
>  `name` varchar(50) NOT NULL,
>  `email` varchar(100) NOT NULL,
>  `encrypted_password` varchar(80) NOT NULL,
>  `salt` varchar(10) NOT NULL,
>  `created_at` datetime DEFAULT NULL,
>  `updated_at` datetime DEFAULT NULL,
>  PRIMARY KEY (`uid`),
>  UNIQUE KEY `email` (`email`)
>) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;
>
>-- --------------------------------------------------------
>
>--
>-- Δομή πίνακα για τον πίνακα `reviews`
>--
>
>CREATE TABLE IF NOT EXISTS `reviews` (
>  `uid` int(11) NOT NULL AUTO_INCREMENT,
>  `player_id` int(11) NOT NULL,
>  `path_id` int(11) NOT NULL,
>  `rated` int(1) NOT NULL,
>  `rated_tags` int(1) NOT NULL,
>  `created_at` datetime DEFAULT NULL,
>  `updated_at` datetime DEFAULT NULL,
>  PRIMARY KEY (`uid`)
>) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;
>
>-- --------------------------------------------------------
>
>--
>-- Δομή πίνακα για τον πίνακα `users`
>--
>
>CREATE TABLE IF NOT EXISTS `users` (
> `id` int(5) unsigned NOT NULL AUTO_INCREMENT,
> `username` varchar(50) NOT NULL,
>  `password` varchar(50) NOT NULL,
>  PRIMARY KEY (`id`)
>) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

11.**Δημιουργία διαχειριστών**: Επιλέξτε την βάση *pa277408_mapmaker2* και πατήστε στην καρτέλα *Κώδικας SQL*. Στη συνέχεια τρέξτε π.χ. το ερώτημα SQL:
 

> INSERT  INTO `users` 
>       (`username` , `password` )
>
>VALUES ('john',  SHA1('johnPsw' ) ), 
>       ('james', SHA1('jamesPsw') ),
>       ('jim',   SHA1('jimPsw'  ) );

12.Η διεύθυνση που τρέχει ο server μπορεί να βρεθεί (στα Windows 7) επιλέγοντας Έναρξη-->Πίνακας Ελέγχου-->Κέντρο δικτύου και κοινής χρήσης-->Σύνδεση (ασύρματου) δικτύου-->Λεπτομέρεις... και από εκεί είναι η Διεύθυνση IPv4 και μετά mapmaker_local, π.χ.: 192.168.1.65
 
##Κατασκευή του κώδικα απομακρυσμένα σε πακέτο web hosting

 1. Αγορά domain name και πακέτου Web Hosting με υποστήριξη Control Panel, PHP, MySQL και εκτέλεσης ενός αιτήματος HTTP post, όχι μόνο μέσα από ένα browser, αλλά και από μία εφαρμογή Android. Σύνδεση του Domain Name με το πακέτο
 2. Από το cpanel επιλέξτε MySql Databases. Δημιουργήστε μια καινούργια βάση.  Αυτό κάντε το πηγαίνοντας στο Create a New Database, δίνοντας για όνομα: xxx_mapmaker2 και πατώντας το κουμπί *Create Database*
 3. Στη συνέχεια (μέσα από το MySql Databases) δημιουργήστε έναν νέο χρήστη για την βάση δεδομένων. Αυτό κάντε το πηγαίνοντας στο Add a New User, δίνοντας για Username: xxx_ippokra και για password: !lissos78. Στη συνέχεια πατήστε το κουμπί *Create a User*
 4. Στη συνέχεια (μέσα από το MySql Databases) προσθέστε τον χρήστη στην βάση δεδομένων. Για να το κάνετε αυτό πηγαίνετε στο Add a User to a Database, επιλέγετε για User: xxx_ippokra και για Database: xxx_mapmaker2 και πιέζετε το κουμπί Add. Στη συνέχεια επιλέγετε ALL PRIVILAGES και πατάτε στο κουμπί *Make Changes*
 5. Από το cpanel επιλέξτε phpMyAdmin. Επιλέξτε την βάση που δημιουργήσατε και πατήστε στην ετικέτα Κώδικας SQL και τρέξτε το ερώτημα SQL: 
>-- Δομή πίνακα για τον πίνακα `paths`
>--
>
>CREATE TABLE IF NOT EXISTS `paths` (
>  `uid` int(11) NOT NULL AUTO_INCREMENT,
>  `player_id` int(11) NOT NULL,
>  `path_raw_google_gpx` varchar(100) NOT NULL,
>  `path_smooth_google_gpx` varchar(100) NOT NULL,
>  `path_raw_gps_gpx` varchar(100) NOT NULL,
>  `tags` int(11) NOT NULL,
>  `meters` int(11) NOT NULL,
>   `new_path` tinyint(1) DEFAULT '1',
>  `created_at` datetime DEFAULT NULL,
>  `updated_at` datetime DEFAULT NULL,
>  PRIMARY KEY (`uid`)
>) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;
>
>-- --------------------------------------------------------
>
>--
>-- Δομή πίνακα για τον πίνακα `players`
>--
>
>CREATE TABLE IF NOT EXISTS `players` (
>  `uid` int(11) NOT NULL AUTO_INCREMENT,
>  `name` varchar(50) NOT NULL,
>  `email` varchar(100) NOT NULL,
>  `encrypted_password` varchar(80) NOT NULL,
>  `salt` varchar(10) NOT NULL,
>  `created_at` datetime DEFAULT NULL,
>  `updated_at` datetime DEFAULT NULL,
>  PRIMARY KEY (`uid`),
>  UNIQUE KEY `email` (`email`)
>) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;
>
>-- --------------------------------------------------------
>
>--
>-- Δομή πίνακα για τον πίνακα `reviews`
>--
>
>CREATE TABLE IF NOT EXISTS `reviews` (
>  `uid` int(11) NOT NULL AUTO_INCREMENT,
>  `player_id` int(11) NOT NULL,
>  `path_id` int(11) NOT NULL,
>  `rated` int(1) NOT NULL,
>  `rated_tags` int(1) NOT NULL,
>  `created_at` datetime DEFAULT NULL,
>  `updated_at` datetime DEFAULT NULL,
>  PRIMARY KEY (`uid`)
>) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;
>
>-- --------------------------------------------------------
>
>--
>-- Δομή πίνακα για τον πίνακα `users`
>--
>
>CREATE TABLE IF NOT EXISTS `users` (
> `id` int(5) unsigned NOT NULL AUTO_INCREMENT,
> `username` varchar(50) NOT NULL,
>  `password` varchar(50) NOT NULL,
>  PRIMARY KEY (`id`)
>) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

 6. **Δημιουργία διαχειριστών**: Επιλέξτε την βάση *pa277408_mapmaker2* και πατήστε στην καρτέλα *Κώδικας SQL*. Στη συνέχεια τρέξτε π.χ. το ερώτημα SQL:
> INSERT  INTO `users` 
>       (`username` , `password` )
>
>VALUES ('john',  SHA1('johnPsw' ) ), 
>       ('james', SHA1('jamesPsw') ),
>       ('jim',   SHA1('jimPsw'  ) );

 7. Εγκαταστήστε το [FileZilla](https://filezilla-project.org/)
 8. Καταβάστε (download zip) τον κώδικα από: https://github.com/ippokratis1/PathsOnMap-Corfu-server.git στον υπολογιστή σας
 9. Αποσυμπιέστε τον φάκελο
 10. Στο αρχείο config.php αλλάξτε την γραμμή 7 σε `define("DB_USER", "xxx_ippokra");` και την γραμμή 9 σε `define("DB_DATABASE", "xxx_mapmaker2");
 11. Στο αρχείο admin.php αλλάξτε την γραμμή 8 σε $dbc = `mysqli_connect('localhost','xxx_ippokra','!lissos78')` or  και την γραμμή 12 σε `mysqli_select_db($dbc, 'xxx_mapmaker2') or die('no db connection');`
 12. Δημιουργήστε μια σύνδεση FTP με τον server χρησιμοποιώντας τον FileZilla
 13. Ανεβάστε (upload) μέσα στον φάκελο public_html το περιεχόμενο του φακέλου που αποσυμπιέσατε
 14. Ζητήστε από τον πάροχο του Web Hosting να μπορεί να εκτελεστεί η εντολή [fopen()](https://smyl.es/enable-fopen-only-for-a-specific-cpanel-or-linux-user-with-suphp/) η οποία είναι συνήθως κλειδωμένη για λόγους ασφαλείας.

 
###Έτοιμος κώδικας που χρησιμοποιήθηκε: 

 - https://github.com/peplin/gpxviewer : Προβολή GPX αρχείων





