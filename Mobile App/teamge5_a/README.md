# PathsOnMap-Corfu-client
 Το PathsOnMap Corfu είναι μια εφαρμογή Android που σκοπό έχει να προτρέψει τους χρήστες να ανεβάζουν τις διαδρομές που κάνουν με τα πόδια, στην Κέρκυρα. Αυτό πετυχαίνεται με την παιχνιδοποίηση της εφαρμογής. Ο τελικός στόχος είναι να δημιουργηθεί ένας χάρτης πεζών για την Κέρκυρα.
 
 Εδώ παρουσιάζεται ο client της εφαρμογής. Αυτός δείχνει όλες τις διαδρομές που έχουν κάνει οι χρήστες έως τώρα, καθώς δίνει και την δυνατότητα να καταγράψει ο χρήστης μια καινούργια διαδρομή: <br />
 ![enter image description here](https://cloud.githubusercontent.com/assets/3535061/9041406/85138ccc-3a10-11e5-8614-b445d5d11a02.PNG)
  <br />
Επίσης, δείχνει στον χρήστη τους πόντους που θα πάρει αν ανεβάσει μια διαδρομή: <br />
![enter image description here](https://cloud.githubusercontent.com/assets/3535061/9041411/8961c1e0-3a10-11e5-813e-3f525a0cbf2b.PNG)
<br />
Επιπλέον, δείχνει την κατάταξη των χρηστών: <br />
![enter image description here](https://cloud.githubusercontent.com/assets/3535061/9041565/4c7edb7c-3a11-11e5-9a93-524511dba38a.PNG)
 <br />
Επίσης, εμφανίζει μία μία της διαδρομές που έχουν κάνει οι χρήστες, ώστε να μπορεί ο τρέχων χρήστης να τις κριτικάρει: <br />
![enter image description here](https://cloud.githubusercontent.com/assets/3535061/9041417/9109c064-3a10-11e5-8119-6e104442bb25.PNG)
<br />
Ακόμα, δίνει τη δυνατότητα να εμφανίζεται ο χάρτης πεζών που έχει δημιουργηθεί: <br />
![enter image description here](https://cloud.githubusercontent.com/assets/3535061/9041420/9628e192-3a10-11e5-804c-84c0c350340c.PNG)
 <br />
Τέλος, δίνει την δυνατότητα ο χρήστης να ανεβάσει την διαδρομή του στον DropBox: <br />
![enter image description here](https://cloud.githubusercontent.com/assets/3535061/9041569/50fae664-3a11-11e5-87ac-95681b1b57cb.PNG)
 <br />

## Λήψη ##
Μπορείτε να κατεβάσετε την εφαρμογή απευθείας από το: http://corfu.pathsonmap.eu/ :<br />
![enter image description here](https://cloud.githubusercontent.com/assets/3535061/9041426/9ee53ea2-3a10-11e5-9855-ea7bfc4f00b0.png)
 <br />

##Τεχνολογίες και εργαλεία 

Αυτός ο client ενσωματώνει αυτές τις τεχνολογίες και τα εργαλεία:

 - Eclipse
 - Java
 - Android SDK
 - XML
 - Google Maps Android API
 - FusedLocationProviderAPI
 - Dropbox API

## Κατασκευή κώδικα ##

 1. Εγκατάσταση [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
 2. Λήψη του [Eclipse IDE for Eclipse Committers](http://www.eclipse.org/downloads/packages/eclipse-ide-eclipse-committers-450/marsr). Στη συνέχεια αποσυμπιέστε τον φάκελο που κατεβάσατε και τοποθετήστε τον στην θέση που θέλετε. Τρέξτε το αρχείο eclipse.exe.
 3. Επισκεφθείτε την διεύθυνση http://developer.android.com/sdk/index.html. Μεταβείτε στο κάτω μέρος της ιστοσελίδας μέχρι να φτάσετε στο  SDK Tools Only. Από εκεί επιλέξτε το installer_r24.3.3-windows.exe και κατεβάστε το. Τρέξτε το αρχείο που κατεβάσατε για να γίνει η εγκατάσταση του Android SDK.
 4. Τρέξτε το Android SDK και λάβετε επιπλέον πηγές λογισμικού: Αυτή η εφαρμογή χρησιμοποιεί ορισμένες βιβλιοθήκες της Google που πρέπει να προσθέσετε. Κάνετε κλικ στο κουμπί "Android SDK Manager". Από εκεί επιλέξτε:
 
- Tools -> Android SDK build tools 22.0.1
- Tools ->Android SDK Platform-tools 22
- Android 5.0.1(API 21) -> SDK Platform
- Android 5.0.1(API 21) -> Google APIs
- Android 5.0.1(API 21) -> Sources for Android SDK
- Android 4.1.2(API 16)->SDK Platform
- Android 4.1.2(API 16)->Google APIs
- Android 4.1.2(API 16)->Sources for Android SDK
- Android 4.0.3(API 15) -> SDK Platform
- Android 4.0.3(API 15) -> Google APIs
- Android 4.0.3(API 15) -> Sources for Android SDK
- Extras > Android Support Repository 
-  Extras > Android Support Library 
- Extras > Google Play services 
- Extras > Google Repository
- Extras ->Google USB driver
και μετά πατήστε στο κουμπι Install packages... Αποδεχτείτε τους όρους όπου χρειάζεται.

5.**Εγκαταστήστε το ADT Plugin For Eclipse**: Στο Eclipse επιλέξτε Help-->Install New Software... Στη συνέχεια πατήστε στο κουμπί Add... Στο παράθυρο που θα ανοίξει δώστε για name π.χ. το: ADT Repo, ενώ για location δώστε: http://dl-ssl.google.com/android/eclipse/ και πατήστε το κουμπί *OK*. Στη συνέχεια επιλέξτε το Developer Tools, πατήστε το κουμπί *Next*, αποδεχτείτε τους όρους και πατήστε το κουμπί *Finish*. Στη συνέχεια επανακκινήστε το Eclipse. <br />
6. Στο Eclipse επιλέξτε Window-->Preferences. Στο παράθυρο που εμφανίζεται επιλέξτε την καρτέλα Android. Πατήστε στο κουμπί *Browse*, ψάξτε για τον φάκελο που εγκαταστάθηκε το Android SDK (π.χ.:  C:\Program Files (x86)\Android\android-sdk) και επιλέξτε τον. Στη συνέχεια πατήστε στο κουμπί *OK* και μετά στο κουμπί *Apply*. Μετά πατήστε το κουμπί *OK* και επανεκκινήστε το Eclipse. <br />
7. **Εγκαταστήστε τον USB driver του τηλεφώνου Android σας**. Η πρώτη σας στάση πρέπει να είναι η λίστα με τους  [OEM USB Drivers](http://developer.android.com/tools/extras/oem-usb.html#Drivers) που διατηρεί η Google. <br />
8. **Ρυθμίστε τις Google Play Services**: Κατεβάστε την βιβλιοθήκη google-play-services_lib που βρίσκεται συμπιεσμένη στην διεύθυνση: https://www.dropbox.com/sh/dlz0c4jghkbvotr/AAC7PXqyVruLxwyVgGnys7dva?dl=0 πατώντας Download--> Doawnload as .zip. Αποσυμπιέστε τον συμπιεσμένο φάκελο που κατεβάσατε αλλά και τον συμπιεσμένο φάκελο που περιέχει.  Στη συνέχεια από το Eclipse πατήστε File--> Import... Απο το παράθυρο που θα ανοίξει επιλέξτε  Android--> Existing Android Code Into Workspece και πατήστε το κουμπί *Next*. Στο παράθυρο που θα ανοίξει πατήστε το κουμπί Browse... και αναζητήστε τον (εσωτερικό) φάκελο google-play-services_lib που περιέχεται στον φάκελο που κατεβάσατε (ο οποίος περιέχει την βιβλιοθήκη). Στη συνέχεια πατήστε το κουμπί *OK*. Τέλος, επιλέξτε Copy projects into workspace και πατήστε Finish. <br />
9. **Εισαγωγή κώδικα στον χώρο εργασίας(workspace) σας στο Eclipse**: Καταβάστε (download zip) τον κώδικα από: https://github.com/ippokratis1/PathsOnMap-Corfu-client.git και αποσυμπιέστε τον φάκελο. Αλλάξτε το όνομα του root directory (PathsOnMap-Corfu-server-client) σε MapMaker2. Στο Eclipse επιλέξτε File-->New-->Other... Από το παράθυρο που θα ανοίξει επιλέξτε Android --> Android Project From Existing Code και πατήστε το κουμπί *Next*. Από το νέο παράθυρο που θα ανοίξει πατήστε το κουμπί Browse... και αναζητήστε τον φάκελο MapMaker2 που δημιουργήσατε. Στη συνέχεια πατήστε το κουμπί *OK*. Τέλος, επιλέξτε Copy projects into workspace και πατήστε Finish. <br />
10. **Ρυθμίσεις Google Maps v2**: Πρέπει να αποκτήσετε ένα καινούργιο API_KEY καθώς το SHA1 fingerprint της εφαρμογής έχει αλλάξει. Για να βρείτε το SHA1 fingerprint από το Eclipse επιλέξτε Window-->Preferences. Από το παράθυρο που ανοίγει επιλέξτε Android-->Build. Δεξιά θα εμφανιστεί το SHA1 fingerprint, π.χ.: 76:7E:DD:0A:EC:CF:A9:15:E7:06:CD:C1:85:21:02:00:50:4D:15:02. Στη συνέχεια πηγαίνεται στο  [Google Developers Consol](https://accounts.google.com/ServiceLogin?continue=https://console.developers.google.com/project) και συνδεθείτε στον λογαριασμό σας. Πατήστε το κουμπί *Create Project*, γράψτε για όνομα π.χ. MapMaker2 και πατήστε το κουμπί *Create*. Στη συνέχεια Επιλέξετε APIs & auth-->Credentials και από εκεί πατήστε το κουμπί *Create new Key*. Από το παράθυρο που ανοίγει πατήστε το κουμπί *Android key*. Στο πλαίσιο κειμένου που εμφανίζεται στο παράθυρο που ανοίγει πληκτρολογήστε το SHA1 fingerprint και το package name της εφαρμογής χωρίζοντας τα με ελληνικό ερωτηματικό, π.χ.:76:7E:DD:0A:EC:CF:A9:15:E7:06:CD:C1:85:21:02:00:50:4D:15:02;com.ippokratis.mapmaker2.  Στη συνέχεια πατήστε το κουμπί *Create*. Αντιγράψτε το API key που εμφανίζεται π.χ.: AIzaSyDoXQ6zN2bOjUHtDTyvuMHv6SBCrzD1Tg0 και επικολήστε στην κατάλληλη θέση του αρχείου AndroidManifest.xml. Αυτή είναι η: 
>  < !-- Goolge Maps API Key -->
>       < meta-data
>            android:name="com.google.android.maps.v2.API_KEY"
 >           android:value="AIzaSyDBaG4SVQyfuGlHC7BLkudgfwCeZHXlWD4" />

όπου θα αντικαταστήσετε  το android:value με την κατάλληλη τιμή, π.χ.: android:value="AIzaSyDoXQ6zN2bOjUHtDTyvuMHv6SBCrzD1Tg0". Στη συνέχεια από το Google Developers Consol επιλέξτε APIs & auth-->APIs και από εκεί επιλέξτε Google Maps Android API. Στη συνέχεια πατήστε το κουμπί *Enable API*. <br />
11. **Εγκατάσταση  εφαρμογής στο κινητό τηλέφωνο**: Από την συσκευή επιλέξτε Ρυθμίσεις-->Επιλογές προγραμματιστή και από εκεί επιλέξτε τις επιλογές: *Να παραμείνει ενεργή* και *Εντοπισμός σφαλμάτων USB*. Στη συνέχεια συνδέστε το κινητό με τον υπολογιστή σας, μέσω καλωδίου USB. Από το Eclipse επιλέξτε τον φάκελο της εφαρμογής και κάντε δεξί κλικ. Από εκεί επιλέξτε Run As-->Android Application. Από το παράθυρο που ανήγει επιλέξτε Choose a running Android device και στην συνέχεια την συσκευή σας και πατήστε το κουμπί *OK*. <br />
12.**Εμφάνιση των ελληνικών σχολίων στο Eclipse**: Από το Eclipse επιλέξτε Window-->Preferences. Στη συνέχεια επιλέξτε την καρτέλα Workspace και ρυθμίστε το Text file encoding σε Other:UTF-8

## Αρχεία .jar που χρησιμοποιήθηκαν ##

 - [android-support-v4.jar](http://developer.android.com/reference/android/support/v4/app/package-summary.html): Παρέχει κλάσεις για να βοηθήσουν στην ανάπτυξη εφαρμογών για το Android επιπέδου API 4 ή νεότερες. Το κύριο χαρακτηριστικό είναι η προς τα πίσω συμβατότητα.
 - [commons-codec-1.6.jar](https://commons.apache.org/proper/commons-codec/): Υλοποιήσεις κοινών κωδικοποιητών και αποκωδικοποιητών
 - commoms-logging-1.1.3.jar: Υποστηρίζει μια σειρά από δημοφιλείς υλοποιήσεις εγγραφής (logging).
 - [drop-box-android-sdk-1.6.3.jar](https://www.dropbox.com/developers/core/sdks/android): Ενθυλακώνει τα ερωτήματα HTTP προς το Dropbox API.
 - [fluent-hc-4.3.6.jar](https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html):  εκθέτει μόνο τις πιο θεμελιώδεις λειτουργίες του HttpClient και προορίζεται για απλές περιπτώσεις χρήσης που δεν απαιτούν την πλήρη ευελιξία του HttpClient.
 - [httpclient-4.3.6.jar:](https://hc.apache.org/httpcomponents-client-ga/) παρέχει ένα αποτελεσματικό, ενημερωμένο, και πλούσιο σε χαρακτηριστικά πακέτο που υλοποιεί την πλευρά του πελάτη με τα πιο πρόσφατα πρότυπα HTTP.
 - [httpclient-cache-4.3.6.jar](http://hc.apache.org/httpcomponents-client-ga/httpclient-cache/apidocs/org/apache/http/impl/client/cache/CachingHttpClient.html): Προσθέτει διαφανή προσωρινή αποθήκευση στην πλευρά του πελάτη.
 - [httpcore-4.3.3.jar](https://hc.apache.org/httpcomponents-core-dev/): Ένα σύμολο στοιχείων μεταφοράς χαμηλού επιπέδου.
 - [httpmime-4.3.6.jar](https://hc.apache.org/httpcomponents-client-ga/httpmime/dependency-info.html):  Κωδικοποιημένες οντότητες MIME.
 - [json_simple-1.1.jar](https://code.google.com/p/json-simple/): Χρησιμοποιείται για την κωδικοποίηση και την αποκωδικοποίηση JSON κειμένων.

