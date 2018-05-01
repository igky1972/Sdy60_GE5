package com.sdy60ge5.teamge5_a;

import java.io.File;
import java.util.ArrayList;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
// import com.ippokratis.mapmaker2.R;
import com.sdy60ge5.teamge5_a.R;
import com.sdy60ge5.teamge5_a.library.ConnectionDetector;
import com.sdy60ge5.teamge5_a.library.DownloadGPXFileAsync;
import com.sdy60ge5.teamge5_a.library.ParsingGPXForDrawing;
import com.sdy60ge5.teamge5_a.library.UserFunctions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


/*********************************************************************************************************************
 * FragmentActivity που είναι υπευθυνη για την αρχική οθονη της εφαρμογής μετά το login. Δείχνει τον χάρτη της πολης *
 * μαζί με τις διαδρομές που έχουν κάνει μέχρι τωρα οι χρήστες. Επίσης δίνει την δυνατοτητα για να καταγραφεί μια    *
 * διαδρομή. Επίσης, ο χρήστης μπορεί να επιλέξει να επισημάνει τον δρομο κατά την καταγραφή.                        *
 * *******************************************************************************************************************/
public class MainActivity extends FragmentActivity {

    //Τα Id για τα googleAnalytics events
    private static String categoryRecordScreenbtId = "Record Activity buttons";
    private static String actionStartStopId = "Start_Stop button";
    private static String actionSubmitId = "Submit path type button";
    private static String labelStartID ="start";
    private static String labelStopID ="stop";
    private static String categoryRecordMenuID = "Record Activity Menu";
    private static String actionMenuChoiseID = "Menu choise";
    private static String actionShowMapsID = "Show Maps";
    private static String actionMakeReviewID="Make a review";
    private static String actionRankingID="Ranking";
    private static String actionLogOutID = "Log Out";
    private static String categoryHighAccuracyID = "HighAccuracy Alter Dialog";
    private static String categoryWiFiID = "WiFi Alter Dialog";
    private static String categoryMobileDataID = "Mobile Data Alter Dialog";
    private static String actionCancelID = "Cancel";
    private static String actionGoSettingsID = "GoToSetting";

//    private static String  allPathsFileUrl = "http://corfu.pathsonmap.eu/mergeFile/merge_gpx.gpx";
    private static String  allPathsFileUrl = "http://snf-818423.vm.okeanos.grnet.gr/teamge5_a/mergeFile/merge_gpx.gpx";

    UserFunctions userFunctions;//Την βάζουμε ωστε να μπορουμε να καλέσουμε την κατάλληλη συνάρτηση οταν ο χρήστης κάνει logout

    Polyline polyline;//H "πολυγωνικη γραμμή" που θα δείχνει την διαδρομή του χρήστη.
    private PolylineOptions rectOptions = new PolylineOptions()//Αρχικοποίηση των επιλογων της "πολυγραμμής" που θα δείχνει την διαδρομή του χρήστη
            .width(5)
            .color(Color.GREEN)
            .geodesic(true);

    //Την enableNewLocationAddToPolyline την χρησιμοποιήμε, γιατί στην περίπτωση που η MyLocationService έχει γυρίσει πίσω τις προηγουμενες θέσεις του χρήστη
    //(οταν η MainActivity γίνει onResume ή onCreate ενω η MyLocationService τρέχει), θέλουμε πρωτα να δημιουργηθεί η polyline του χρήστη που δείχνει την
    // διαδρομή του μέχρι τωρα, και μετά να προστεθουν σε αυτή οι καινουριες θέσεις του χρήστη
    boolean enableNewLocationAddToPolyline=true;

    //Η firstTimeOnResumeAfterCreated δηλωνει αν είναι η πρωτη φορά που η MainActivity μπαίνει στην onResume(). Αυτή χρησιμοποιείται σε συνδυασμο με το αν η
    //MyLocationService τρέχει, ωστε οι παλιές θέσεις του χρήστη να μην ξαναζητηθουν αν είναι η πρωτη φορά, αφου αν αυτές υπάρχουν έχουν ζητηθεί στην onCreate()
    boolean firstTimeOnResumeAfterCreated=true;
    //Η firstTimeTheActivityIsBindedToMyLocationService δείχνει αν η MainActivity συνδέται για π�?�?τη φο�?ά στην υπη�?εσία. Αν δεν είναι η π�?�?τη φο�?ά
    //βοηθάει στο να ζητήσουμε τις παλιές θέσεις του χ�?ήστη κατά την σ�?νδεση στην υπη�?εσία (αφο�? σε αυτή την πε�?ίπτωση η MaiActivity έχει καταστ�?αφεί) και �?ταν
    //ξαναδημιου�?γείται η υπη�?εσία "τ�?έχει", ά�?α θα έχει τις παλιές θέσεις του χ�?ήστη
    boolean firstTimeTheActivityIsBindedToMyLocationService=true;

    private ToggleButton togbtnStartRoute;//Το κουμπί (on-off) για να α�?χίσει η καταγ�?αφή της διαδ�?ομής
    private TextView tvLocation;//�?ήνυμα ανάλογα με αν έχει φιξα�?ιστεί η θέση ή �?χι ή �?χι
    private ProgressBar pbLocationProgress;//Δείχνει στον χ�?ήστη �?τι π�?οσπαθεί να β�?ει την τοποθεσία του, �?στε αυτ�?ς να μπο�?εί να ξεκινήσει την διαδ�?ομή του
    private Button btnSubmitPathType;//Επιτ�?έπει στον χ�?ήστη να υποβάλει το είδος της διαδ�?ομής
    private Spinner spinnerPathType;//Το spinner με τα είδη της διαδ�?ομής
    private TextView tvSelectPedestrianType;
    private GoogleMap googleMapv2;//�? χάρτης της Google απ�? το API v2

    Messenger mService = null;//O Messenger της υπη�?εσίας (μέσω του οποίου στέλνονται τα μην�?ματα στην υπη�?εσία)
    boolean mIsBound;//Δείχνει αν η activity έχει συνδεθεί στην υπη�?εσία ή �?χι

    //�? messenger που ο�?ίζεται απ�? τον client �?στε η service να μπο�?εί να στέλνει μην�?ματα πίσω.
    final Messenger mMessenger = new Messenger(new Handler(new IncomingHandlerCallback()));

    //�? Handler που χει�?ίζεται τα μην�?ματα που στέλνει η υπη�?εσία MyLocationService
    class IncomingHandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) { //�? client χει�?ίζεται τα message (απ�? την service: MyLocationService) στην handle message μέθοδο
            switch (msg.what) {
                case MyLocationService.MSG_SET_LAST_LOCATION: //Η MyLocationService στέλνει την τελευταία εντοπισμένη θέση του χ�?ήστη
                    double latitude =msg.getData().getDouble("latitude");//Το γεωγ�?φικ�? πλάτος του χ�?ήστη
                    double longitude =msg.getData().getDouble("longitude");//Το γεωγ�?αφικ�? μήκος του χ�?ήστη
                    gotoMyLocation(latitude,longitude);//�?αλείται η gotoMyLocation �?στε ο χάρτης να κεντ�?α�?ιστεί στη νέα θέση, αλλά και να π�?οστεθεί αυτή στη polyline
                    break;
                case MyLocationService.MSG_GOOGLE_PLAY_SERVICE_RESULT_CODE://Λαμβάνεται ο κ�?δικας αποτελέσματος κατά την αποτυχία σ�?νδεσης στην google play service
                    int resultCode=msg.getData().getInt("result_code");
                    DisplayErrorDialog(resultCode);// Εμφανίζει έναν διάλογο λάθους
                    break;
                case MyLocationService.MSG_SET_LOCATION_FIXED://�? client λαμβάνει μήνυμα �?τι η π�?�?τη θέση του χ�?ήστη β�?έθηκε
                    pbLocationProgress.setVisibility(View.INVISIBLE);//η μπά�?α π�?ο�?δου γίνεται α�?�?ατη (�?στε να δηλωθεί στον χ�?ήστη �?τι μπο�?εί να ξεκινήσει την διαδ�?ομή)
                    tvLocation.setText(getString(R.string.location_is_fixed));
                    btnSubmitPathType.setVisibility(View.VISIBLE);//�?στε ο χ�?ήστης να μπο�?εί να υποβάλει είδος μονοπατιο�?
                    spinnerPathType.setVisibility(View.VISIBLE);
                    tvSelectPedestrianType.setVisibility(View.VISIBLE);
                    break;
                case MyLocationService.MSG_SET_LOCATION_LOST:
                    pbLocationProgress.setVisibility(View.VISIBLE);//η μπά�?α π�?ο�?δου γίνεται ο�?ατή (�?στε να δηλωθεί στον χ�?ήστη �?τι χάθηκε  η θέση)
                    tvLocation.setText(getString(R.string.location));
                    btnSubmitPathType.setVisibility(View.INVISIBLE);//�?στε ο χ�?ήστης να μην μπο�?εί να υποβάλει είδος μονοπατιο�? (αφο�? δεν έχουμε ακ�?ιβής τοποθεσία)
                    spinnerPathType.setVisibility(View.INVISIBLE);
                    tvSelectPedestrianType.setVisibility(View.INVISIBLE);
                    break;
                case MyLocationService.MSG_SEND_POINTS_OF_POLYLINE_AND_TAGS://Λαμβάνονται οι εντοπισμένες θέσεις του χ�?ήστη μέχ�?ι στιγμής (και τα tags)
                    enableNewLocationAddToPolyline=false;//Εμποδίζει την π�?οσθήκη νέας θέσης στον χάρτη
                    ArrayList <LatLng> arrayOfCoordinates = msg.getData().getParcelableArrayList("coordinatesArrayList");//Λίστα με τις συντεταγμένες των εντοπισμένων θέσεων του χ�?ήστη
                    ArrayList<LatLng> arrayOfTagLocations = msg.getData().getParcelableArrayList("tagLocationArrayList");//Λίστα με τις θέσεις του χ�?ήστη που έχουν tags
                    ArrayList<String> arrayOfPathTypes = msg.getData().getStringArrayList("pathTypeArrayList");//Λίστα με τα είδη της διαδ�?ομής (πα�?άλληλη λίστα με την arrayOfTagLocations)

                    if (googleMapv2 != null){
                        googleMapv2.clear();//�?αθα�?ίζει τις polyline (και τα tags) που έχουν ζωγ�?αφιστεί στον χάρτη

                        loadAllPathsIfServiceRunning();

                        //Για να κεντ�?ά�?ει στα γ�?ήγο�?α τον χάρτη κοντά στην θέση του χ�?ήστη εάν υπή�?χε εντοπισμένη θέση
                        if (arrayOfCoordinates.size()!=0){
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                    new LatLng(arrayOfCoordinates.get(arrayOfCoordinates.size()-1).latitude, arrayOfCoordinates.get(arrayOfCoordinates.size()-1).longitude)).zoom(16.5f).build();
                            googleMapv2.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                        rectOptions=new PolylineOptions();//Α�?χικοποίηση των επιλογ�?ν της νέας polyline που θα ζωγ�?αφιστεί
                        rectOptions.width(5)
                                .color(Color.GREEN)
                                .geodesic(true);

                        if (arrayOfCoordinates.size()!=0){
                            //Π�?οσθέτει �?λες τις εντοπισμένες θέσεις του χ�?ήστη στην polyline (κατά χ�?ονολογική σει�?ά) -εάν υπά�?χουν τέτοιες
                            for (int i = 0; i < arrayOfCoordinates.size(); i++){
                                rectOptions.add(new LatLng(arrayOfCoordinates.get(i).latitude, arrayOfCoordinates.get(i).longitude));
                            }
                        }

                        polyline = googleMapv2.addPolyline(rectOptions);//Ζωγ�?αφίζει την διαδ�?ομή του χ�?ήστη μέχ�?ι τ�?�?α

                        //Π�?οσθέτει �?λες τις θέσεις του χ�?ήστη με ετικέτα εάν υπά�?χουν
                        if (arrayOfTagLocations.size()!=0){
                            for (int i = 0; i < arrayOfTagLocations.size(); i++){
                                googleMapv2.addMarker(new MarkerOptions()
                                        .position(new LatLng(arrayOfTagLocations.get(i).latitude, arrayOfTagLocations.get(i).longitude))
                                        .title(arrayOfPathTypes.get(i))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            }
                        }
                    }

                    enableNewLocationAddToPolyline=true;//Επιτ�?έπει την π�?οσθήκη νέας θέσης στον χάρτη
                    break;
                case MyLocationService.MSG_SEND_ALL_LOCATIONS_AND_TOTAL_DISTANCE_BEFORE_STOP://Λαμβάνονται �?λες οι εντοπισμένες θέσεις του χ�?ήστη και η απ�?σταση που διένυσε

                    boolean numberOfLcationsGreaterThanZero = msg.getData().getBoolean("numberOfLocationsDifferentZero");//Αν έχουν εντοπιστεί θέσεις
                    int numberOfTagLocations = msg.getData().getInt("numberOfTags");//Α�?ιθμ�?ς των tags
                    float distance = msg.getData().getFloat("totalDistance");//Η απ�?σταση που έχει διαν�?σει ο χ�?ήστης
                    boolean userHasGoneOut = msg.getData().getBoolean("hasGoneOutOfTown");
                    String townOfInterest = msg.getData().getString("town");
                    doUnbindService();//�?εσυνδέεται απ�? την υπη�?εσία (αν είναι συνδεδεμένη)
                    //Σταματάει την υπη�?εσία (αν τ�?έχει)
                    try{
                        stopService(new Intent(MainActivity.this, MyLocationService.class));//Σταματάει την υπη�?εσία
                    }
                    catch(Throwable t){
                        Log.e("MainActivity", "Failed to stop the service", t);
                    }
                    if (googleMapv2 != null){
                        googleMapv2.clear();//�?αθα�?ίζει τις polyline (και τα tags) που έχουν ζωγ�?αφιστεί στον χάρτη �?στε να είναι "καθα�?�?ς" αν γυ�?ίσει ο χ�?ήστης πίσω
                    }

                    startSavePathActivity(numberOfLcationsGreaterThanZero,numberOfTagLocations, distance,userHasGoneOut,townOfInterest);//�?α ξεκινήσει την Activity που ο χ�?ήστης μπο�?εί να σ�?σει την διαδ�?ομή
                    break;
                case MyLocationService.MSG_SEND_CURRENT_TAG_LOCATION://Η θέση που γυ�?ίζει απ�? την αίτηση για το tag
                    double currentLatitude=msg.getData().getDouble("currentLatitude");
                    double currentLongitude=msg.getData().getDouble("currentLongitude");
                    String pathType = msg.getData().getString("pathType");
                    Toast.makeText(getApplicationContext(), "The path type is submitted",
                            Toast.LENGTH_SHORT).show();
                    addMarkerToMap(currentLatitude, currentLongitude,pathType);
                    break;
                case MyLocationService.MSG_SEND_OUT_OF_REGION: //Η MyLocationService στέλνει την τελευταία εντοπισμένη θέση του χ�?ήστη
                    String town  =msg.getData().getString("town");//Το γεωγ�?φικ�? πλάτος του χ�?ήστη
                    //double longitude =msg.getData().getDouble("longitude");//Το γεωγ�?αφικ�? μήκος του χ�?ήστη
                    //gotoMyLocation(latitude,longitude);//�?αλείται η gotoMyLocation �?στε ο χάρτης να κεντ�?α�?ιστεί στη νέα θέση, αλλά και να π�?οστεθεί αυτή στη polyline
                    doUnbindService();//�?εσυνδέεται απ�? την υπη�?εσία (αν είναι συνδεδεμένη)
                    //Σταματάει την υπη�?εσία (αν τ�?έχει)
                    try{
                        stopService(new Intent(MainActivity.this, MyLocationService.class));//Σταματάει την υπη�?εσία
                    }
                    catch(Throwable t){
                        Log.e("MainActivity", "Failed to stop the service", t);
                    }
                    if (googleMapv2 != null){
                        googleMapv2.clear();//�?αθα�?ίζει τις polyline (και τα tags) που έχουν ζωγ�?αφιστεί στον χάρτη �?στε να είναι "καθα�?�?ς" αν γυ�?ίσει ο χ�?ήστης πίσω
                    }
                    togbtnStartRoute.setChecked(false);//�?αναγ�?�?ισε το κουμπί στην off κατάσταση, �?στε ο χ�?ήστης να μπο�?εί να ξεκινήσει μετά την ενε�?γοποίηση την καταγ�?αφή
                    Toast.makeText(getApplicationContext(), "Sorry, you are out of " + town,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    //Δεν κάνει τίποτα
            }
            return true;//Δηλ�?νει �?τι η handleMessage χει�?ίστηκε το μήνυμα 
        }
    }

    //�?έθοδος που ξεκινάει την Activity για την αποθήκευση της διαδ�?ομής (τις στέλνει τα δεδεομένα που εντοπίστηκαν)-αν �?μως δεν έχει καταγ�?φεί καμιά θέση μένει στην mainActivity
    public void startSavePathActivity(boolean locationsDifferentZero,int numberofTagLocations,Float totalDistance,boolean hasGoneOut,String town){

        if(locationsDifferentZero==true && hasGoneOut==false){
            Intent intent = new Intent(this, SavePathActivity.class);
            intent.putExtra("numberofTagLocations",numberofTagLocations);
            intent.putExtra("distance",totalDistance);
            startActivity(intent);
        }
        else if(locationsDifferentZero==true && hasGoneOut==true){
            Toast.makeText(getApplicationContext(), "Sorry, you have gone out of "+town,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Sorry, the path was not recorded, because of location provider problems",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * �?λάση για την αλληλεπίδ�?αση με την κ�?�?ια διεπαφή (interface) της MyLocationService
     */

    private ServiceConnection mConnection = new ServiceConnection() {//�?αλείται �?ταν ο client κάνει σ�?νδεση (bind) στην υπη�?εσία
        public void onServiceConnected(ComponentName className, IBinder service) {//Το σ�?στημα καλεί αυτήν �?στε να πα�?αδοθεί το IBinder που γυ�?ίζει η onBind() μέθοδος της MyLocationService.
            mService = new Messenger(service); //�? messenger με τον οποίο στέλνουμε μην�?ματα στην υπη�?εσία

            if(firstTimeTheActivityIsBindedToMyLocationService==false){//Αν δεν είναι η π�?�?τη φο�?ά η υπη�?εσία τ�?έχει και πε�?ιέχει τις θέσεις του χ�?ήστη
                try{
                    //Ζητάει τις θέσεις του χ�?ήστη μέχ�?ι τ�?�?α
                    Message msg = Message.obtain(null, MyLocationService.MSG_REQUEST_POINTS_OF_POLYLINE_AND_TAGS);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (RemoteException e) {
                    //Η υπη�?εσία έχει κατα�?�?ε�?σει
                }
            }

            try {
                Message msg = Message.obtain(null, MyLocationService.MSG_REGISTER_CLIENT);//Δημιου�?γία μην�?ματος �?στε ο client να καταγ�?αφεί στην υπη�?εσία: MyLocationService 
                msg.replyTo = mMessenger;//Η υπη�?εσία θα απαντήσει στον mMessenger του client, για αυτ�? τον στέλνουμε με το μήνυμα 
                mService.send(msg);//Στέλνει ένα μήνυμα σε αυτ�?ν τον Handler(mService), δηλ. της υπη�?εσίας. Το συγκεκ�?ιμένο είναι για να ξέ�?ει σε ποιο handler θα απαντάει η υπη�?εσία (αφο�? γίνεται register o client στου mClients της υπη�?εσίας).
            }
            catch (RemoteException e) {
                // Σε αυτή την πε�?ίπτωση η υπη�?εσία έχει κατα�?�?ε�?σει π�?ιν π�?ολάβουμε να κάνουμε κάτι με αυτήν
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // Αυτή καλείται �?ταν η σ�?νδεση με την υπη�?εσία έχει αποσυνδεθεί απ�?οσδ�?κτητα - η διαδικασία κατά�?�?ευσε
            mService = null;//Αφο�? η service δεν είναι πια bind, κάνουμε τον messeger με τον οποίο στέλναμε μην�?ματα στην υπη�?εσία null.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ελέγχει αν ο χ�?ήστης είναι συνδεδεμένος - log in - (απ�? την βάση δεδομένων) - διαφο�?ετικά θα φο�?τ�?σει την login activity
        userFunctions = new UserFunctions();
        if(userFunctions.isUserLoggedIn(getApplicationContext())){
            setTitle(R.string.record_path);
            setContentView(R.layout.activity_main);//μ�?νο αν ο χ�?ήστης είναι συνδεδεμένος φο�?τ�?νει το layout

            //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
            Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
            t.setScreenName("Record Path screen");
            t.send(new HitBuilders.AppViewBuilder().build());

            tvLocation = (TextView)findViewById(R.id.tvLocation);
            togbtnStartRoute = (ToggleButton)findViewById(R.id.togbtnStartRoute);
            pbLocationProgress = (ProgressBar)findViewById(R.id.pbLocationProgress);
            btnSubmitPathType = (Button)findViewById(R.id.btnSubmitPathType);
            spinnerPathType = (Spinner)findViewById(R.id.spinnerPathType);
            tvSelectPedestrianType = (TextView)findViewById(R.id.tvSelectPedestrianType);


            //Π�?οσπαθεί να φο�?τ�?σει τον χάρτη
            try {
                // Φο�?τ�?νει τον χάρτη
                initilizeMap(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
            CheckIfServiceIsRunning();//Αν η MyLocationService τ�?έχει �?ταν η activity ξεκινάει, θέλουμε να συνδεθο�?με αυτ�?ματα σε αυτή.

        }
        else{//�? χ�?ήστης δεν είναι συνδεδεμένος-δείξε την οθονη σ�?νδεσης
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Σταματάει την MainActivity
            finish();
        }
    }

    private void CheckIfServiceIsRunning() {
        //Εάν η υπη�?εσία τ�?έχει �?ταν η υπη�?εσία ξεκινάει, θέλουμε να συνδεθο�?με αυτ�?ματα σε αυτήν.
        if (MyLocationService.isRunning()) {//Η isRunning (που την έχουμε υλοποιήσει εμείς) είναι true αν η υπη�?εσία τ�?έχει
            togbtnStartRoute.setChecked(true);//Αφο�? η υπη�?εσία τ�?έχει, σημαίνει �?τι ο χ�?ήστης έχει πατήσει την καταγ�?αφή
            if(MyLocationService.locationIsFixed()){ //Η locationIsFixed (που την έχουμε υλοποιήσει εμείς) είναι true αν η θέση είναι φιξα�?ιμένη
                pbLocationProgress.setVisibility(View.INVISIBLE);//Τ�?τε εξαφανίζουμε την μπά�?α π�?ο�?δου
                tvLocation.setText(getString(R.string.location_is_fixed));
                tvLocation.setVisibility(View.VISIBLE);
                btnSubmitPathType.setVisibility(View.VISIBLE);
                spinnerPathType.setVisibility(View.VISIBLE);
                tvSelectPedestrianType.setVisibility(View.VISIBLE);
            }
            else if(MyLocationService.locationHasFirstFixedEvent() && !MyLocationService.locationIsFixed()){//Η θέση έχει "φιξα�?ιστεί" κάποια στιγμή και δεν είναι "φιξα�?ιμένη"
                pbLocationProgress.setVisibility(View.VISIBLE);//Τ�?τε εμφανίζουμε την μπά�?α π�?ο�?δου
                tvLocation.setText(getString(R.string.location_try_to_fix_again));//Εμφανίζουμε για κείμενο Location try to fix again:
                tvLocation.setVisibility(View.VISIBLE);//Kάνουμε το κείμενο ο�?ατ�?
            }
            else if(!MyLocationService.locationHasFirstFixedEvent() && !MyLocationService.locationIsFixed()){//Η θέση δεν έχει "φιξα�?ιστεί" κάποια στιγμή και δεν είναι "φιξα�?ιμένη"
                pbLocationProgress.setVisibility(View.VISIBLE);//Τ�?τε εμφανίζουμε την μπά�?α π�?ο�?δου
                tvLocation.setText(getString(R.string.location));//Εμφανίζουμε για κείμενο Location:
                tvLocation.setVisibility(View.VISIBLE);//Kάνουμε το κείμενο ο�?ατ�?
            }
            firstTimeTheActivityIsBindedToMyLocationService=false;//Αφο�? η υπη�?εσία τ�?έχει σημαίνει �?τι έχει συνδεθεί παλι�?τε�?α activity στην υπη�?εσία
            doBindService();
        }
    }

    //Συνδέει την activity στην υπη�?εσία
    void doBindService() {
        bindService(new Intent(this, MyLocationService.class), mConnection, Context.BIND_AUTO_CREATE);//Εδ�? συνδέουμε την υπη�?εσία
        mIsBound = true;//για να ξέ�?ουμε αν η υπη�?εσία είναι συνδεδεμένη
    }

    void doUnbindService() {
        if (mIsBound) {
            //Αν έχουμε λάβει την υπη�?εσία, και έτσι έχουμε εγγ�?αφεί σε αυτή, τ�?�?α είναι η �?�?α να απεγγ�?αφο�?με.
            if (mService != null) {//Αν η υπη�?εσία δεν έχει αποσυνδεθεί απ�? κάποιο απ�?�?σμενο λ�?γο και έχουμε συνδεθεί σε αυτήν
                try {
                    //Στέλνουμε μήνυμα αποσ�?νδεσης
                    Message msg = Message.obtain(null, MyLocationService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (RemoteException e) {
                    // Δεν υπά�?χει κάτι ιδιαίτε�?ο να κάνουμε αν η υπη�?εσία έχει κατα�?�?ε�?σει
                }
            }
            //Αποσυνδέουμε την υπά�?χουσα σ�?νδεση μας
            unbindService(mConnection);//Εδ�? γίνεται η αποσ�?νδεση
            mIsBound = false;//Για να ξέ�?ουμε �?τι η υπη�?εσία δεν είναι πια συνδεδεμένη

            tvLocation.setVisibility(View.INVISIBLE);
            tvLocation.setText(getString(R.string.location));
            pbLocationProgress.setVisibility(View.INVISIBLE);//Δεν χ�?ησιμοποιο�?με άλλο τον fused provider
            btnSubmitPathType.setVisibility(View.INVISIBLE);//�?άνουμε α�?�?ατο το κουμπί με το οποίο ο χ�?ήστης μπο�?εί να κάνει tag
            spinnerPathType.setVisibility(View.INVISIBLE);
            tvSelectPedestrianType.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyLocationService.isRunning()) {//Η isRunning (που την έχουμε υλοποιήσει εμείς) είναι true αν η υπη�?εσία τ�?έχει
            togbtnStartRoute.setChecked(true);//Αφο�? η υπη�?εσία τ�?έχει, σημαίνει �?τι ο χ�?ήστης έχει πατήσει την καταγ�?αφή
            if(MyLocationService.locationIsFixed()){ //Η locationIsFixed (που την έχουμε υλοποιήσει εμείς) είναι true αν η θέση είναι φιξα�?ιμένη
                pbLocationProgress.setVisibility(View.INVISIBLE);//Τ�?τε εξαφανίζουμε την μπά�?α π�?ο�?δου
                tvLocation.setText(getString(R.string.location_is_fixed));
                tvLocation.setVisibility(View.VISIBLE);
                btnSubmitPathType.setVisibility(View.VISIBLE);
                spinnerPathType.setVisibility(View.VISIBLE);
                tvSelectPedestrianType.setVisibility(View.VISIBLE);
            }
            else if(MyLocationService.locationHasFirstFixedEvent() && !MyLocationService.locationIsFixed()){//Η θέση έχει "φιξα�?ιστεί" κάποια στιγμή και δεν είναι "φιξα�?ιμένη"
                pbLocationProgress.setVisibility(View.VISIBLE);//Τ�?τε εμφανίζουμε την μπά�?α π�?ο�?δου
                tvLocation.setText(getString(R.string.location_try_to_fix_again));//Εμφανίζουμε για κείμενο Location try to fix again:
                tvLocation.setVisibility(View.VISIBLE);//Kάνουμε το κείμενο ο�?ατ�?
            }
            else if(!MyLocationService.locationHasFirstFixedEvent() && !MyLocationService.locationIsFixed()){//Η θέση δεν έχει "φιξα�?ιστεί" κάποια στιγμή και δεν είναι "φιξα�?ιμένη"
                pbLocationProgress.setVisibility(View.VISIBLE);//Τ�?τε εμφανίζουμε την μπά�?α π�?ο�?δου
                tvLocation.setText(getString(R.string.location));//Εμφανίζουμε για κείμενο Location:
                tvLocation.setVisibility(View.VISIBLE);//Kάνουμε το κείμενο ο�?ατ�?
            }
        }

        initilizeMap(true);//Α�?χικοποιεί εκ νέου τον χάρτη
        //Αν η υπη�?εσία τ�?έχει και δεν είναι η π�?�?τη φο�?ά στο onResume μετά την δημιου�?γία της υπη�?εσίας (που αν ήταν, θα έχουμε στείλει ένα μήνυμα στην
        //υπη�?εσία για να μας δ�?σει τις παλιές θέσεις του χ�?ήστη)
        if (MyLocationService.isRunning() && firstTimeOnResumeAfterCreated==false){
            //Ζητάμε απ�? την υπη�?εσία τις παλιές θέσεις του χ�?ήστη
            try{
                Message msg = Message.obtain(null, MyLocationService.MSG_REQUEST_POINTS_OF_POLYLINE_AND_TAGS);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e) {
                //Η υπη�?εσία έχει κατα�?�?ε�?σει
            }
        }
        firstTimeOnResumeAfterCreated=false;//Η activity έχει μπει ήδη μια φο�?ά στην onResume.
    }

    /*�?αλείται �?ταν ο χ�?ήστης κάνει κλικ στο κουμπί υποβολής είδους διαδ�?ομής*/
    public void onBtnSubmitPathTypeClicked(View view){
        //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
        //Χτίζει και στέλνει το Analytics Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(categoryRecordScreenbtId)
                .setAction(actionSubmitId)
                .setLabel(String.valueOf(spinnerPathType.getSelectedItem()))
                .build());

        try{
            //Στέλνουμε ένα μήνυμα �?στε η υπη�?εσία να μας γυ�?ίσει την τω�?ινή θέση του χ�?ήστη (που θα μπει το tag του είδους της διαδ�?ομής)
            Message msg = Message.obtain(null, MyLocationService.MSG_REQUEST_CURRENT_LOCATION_FOR_TAG);
            msg.replyTo = mMessenger;
            Bundle bundle = new Bundle();
            bundle.putString("pathType", String.valueOf(spinnerPathType.getSelectedItem()));
            msg.setData(bundle);
            mService.send(msg);
        }
        catch (RemoteException e) {
            //Η υπη�?εσία έχει κατα�?�?ε�?σει
        }
    }

    /*�?αλείται �?ταν ο χ�?ήστης κάνει κλικ στο κουμπί εναλλαγής:Start Route toggle button */
    public void onTogBtnstartRouteClicked(View view) {

        boolean on = togbtnStartRoute.isChecked();//Αν το κουμπί έχει πατηθεί (isChecked==true), τ�?τε θεω�?ο�?με πως είναι on

        if (on) {//Αν πατηθεί το on (δηλαδή το ξεκίνημα της καταγ�?αφής)
            if (android.os.Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }


            //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
            Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
            //Χτίζει και στέλνει το Analytics Event.
            t.send(new HitBuilders.EventBuilder()
                    .setCategory(categoryRecordScreenbtId)
                    .setAction(actionStartStopId)
                    .setLabel(labelStartID)
                    .build());

            //�?α ξεκινήσουμε την υπη�?εσία μ�?νο αν ο provider του gps και του wifi είναι ενε�?γοποιημένος (αλλι�?ς για την εφαρμογή μας δεν έχει ν�?ημα η καταγ�?αφή θέσεων)
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //Αν ο GPS και Network provider είναι ενε�?γοποιημένος συνδέσου με την υπη�?εσία (δηλαδή ξεκίνα την καταγ�?αφή) - αν το wifi ειναι ενε�?γοποιημένο
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER )){//�?εκινά την υπη�?εσία αν ο gps provider είναι ενε�?γοποιημένος
                //�?�?νο αν το wifi είναι ενε�?γοποιημένο ξεκινά η υπη�?εσία - αλλι�?ς δεν έχει ιδιαίτε�?ο ν�?ημα η καταγ�?αφή
                WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()){
                    ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());
                    //Αν δεν υπά�?χει δυνατ�?τητα mobile data τ�?τε επιτέπουμε την ένα�?ξη
                    if(mConnectionDetector.hasMobileDatacapability()==false){
                        if(mConnectionDetector.isInternetAvailable()){
                        	startService(new Intent (MainActivity.this, MyLocationService.class));
//                            startService(new Intent(MainActivity.this, MyLocationService.class));//�?εκινά την υπη�?εσία
                        	doBindService();//Συνδέεται στην υπη�?εσία
                            pbLocationProgress.setVisibility(View.VISIBLE);
                            tvLocation.setVisibility(View.VISIBLE);
                        }
                        else{
                            togbtnStartRoute.setChecked(false);//�?αναγ�?�?ισε το κουμπί στην off κατάσταση, �?στε ο χ�?ήστης να μπο�?εί να ξεκινήσει μετά την ενε�?γοποίηση την καταγ�?αφή
                            Toast.makeText(getApplicationContext(), R.string.internet_connection_required,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else{//Ζητάει να ανοίξουν τα mobile data για να συνεχίσει

                        //------------------------------------------------------------------------------------------------//
                        //κοιτάζει να δει αν τα mobile data επιτ�?έπονται
                        boolean mobileDataAllowed = Settings.Secure.getInt(getContentResolver(), "mobile_data", 1) == 1;
                        if(mobileDataAllowed){
                            if(mConnectionDetector.isInternetAvailable()){



                                startService(new Intent(MainActivity.this, MyLocationService.class));//�?εκινά την υπη�?εσία
                                doBindService();//Συνδέεται στην υπη�?εσία
                                pbLocationProgress.setVisibility(View.VISIBLE);
                                tvLocation.setVisibility(View.VISIBLE);
                            }
                            else{
                                togbtnStartRoute.setChecked(false);//�?αναγ�?�?ισε το κουμπί στην off κατάσταση, �?στε ο χ�?ήστης να μπο�?εί να ξεκινήσει μετά την ενε�?γοποίηση την καταγ�?αφή
                                Toast.makeText(getApplicationContext(), R.string.internet_connection_required,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            togbtnStartRoute.setChecked(false);//�?αναγ�?�?ισε το κουμπί στην off κατάσταση, �?στε ο χ�?ήστης να μπο�?εί να ξεκινήσει μετά την ενε�?γοποίηση την καταγ�?αφή
                            showMobileDataDisabledToUser();
                        }
                    }
                }
                else{
                    togbtnStartRoute.setChecked(false);//�?αναγ�?�?ισε το κουμπί στην off κατάσταση, �?στε ο χ�?ήστης να μπο�?εί να ξεκινήσει μετά την ενε�?γοποίηση την καταγ�?αφή
                    showWiFiDisabledToUser();//Δείξε στον χ�?ήστη μια π�?οειδοποίηση �?τι το wifi είναι κλειστ�?
                }
            }else{
                togbtnStartRoute.setChecked(false);//�?αναγ�?�?ισε το κουμπί στην off κατάσταση, �?στε ο χ�?ήστης να μπο�?εί να ξεκινήσει μετά την ενε�?γοποίηση την καταγ�?αφή
                showHighAccuracyDisabledAlertToUser();//Δείξε στον χ�?ήστη μια π�?οειδοποίηση �?τι το GPS είναι κλειστ�?
            }

        }
        else {//Αν πατηθεί το off
            //Στέλνουμε ένα μήνυμα �?στε η υπη�?εσία να μας γυ�?ίσει �?λες τις θέσεις του χ�?ήστη μέχ�?ι τ�?�?α
            //μ�?λις αυτές γυ�?ίσουν ο handler θα διαχει�?ιστεί το μήνυμα και στη συνέχεια θα σταματήσει την υπη�?εσία
            //και επίσης θα ξεκινήσει μία άλλη Activity στην οποία θα στείλει τις θέσεις του χ�?ήστη, �?στε ο χ�?ήστης να
            //μπο�?εί να σ�?σει την διαδ�?ομή του

            //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
            Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
            //Χτίζει και στέλνει το Analytics Event.
            t.send(new HitBuilders.EventBuilder()
                    .setCategory(categoryRecordScreenbtId)
                    .setAction(actionStartStopId)
                    .setLabel(labelStopID)
                    .build());

            try{
                Message msg = Message.obtain(null, MyLocationService.MSG_REQUEST_ALL_LOCATIONS_AND_TOTAL_DISTANCE_BEFORE_STOP);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e) {
                //Η υπη�?εσία έχει κατα�?�?ε�?σει
            }
        }
    }

    //Η π�?οειδοποίηση στον χ�?ήστη (�?τι π�?έπει να ανοίξει τον GPS πά�?οχο)
    private void showHighAccuracyDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alter_dialog_for_gps))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button_of_alter_dialog_for_gps),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                                //Χτίζει και στέλνει το Analytics Event.
                                t.send(new HitBuilders.EventBuilder()
                                        .setCategory(categoryHighAccuracyID)
                                        .setAction(actionGoSettingsID)
                                        .build());

                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);//Πηγαίνει τον χ�?ήστη στην Activity με τις �?υθμίσεις του GPS, �?στε να μπο�?εί να το ενε�?γοποιήσει
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_button_of_alter_dialog_for_gps),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){

                        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                        //Χτίζει και στέλνει το Analytics Event.
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory(categoryHighAccuracyID)
                                .setAction(actionCancelID)
                                .build());

                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    //Η π�?οειδοποίηση στον χ�?ήστη (�?τι π�?έπει να ανοίξει το WiFi)
    private void showWiFiDisabledToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alter_dialog_for_wifi))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button_of_alter_dialog_for_wifi),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                                //Χτίζει και στέλνει το Analytics Event.
                                t.send(new HitBuilders.EventBuilder()
                                        .setCategory(categoryMobileDataID)
                                        .setAction(actionGoSettingsID)
                                        .build());

                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_WIFI_SETTINGS);
                                startActivity(callGPSSettingIntent);//Πηγαίνει τον χ�?ήστη στην Activity με τις �?υθμίσεις του WiFi, �?στε να μπο�?εί να το ενε�?γοποιήσει
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_button_of_alter_dialog_for_wifi),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){

                        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                        //Χτίζει και στέλνει το Analytics Event.
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory(categoryMobileDataID)
                                .setAction(actionCancelID)
                                .build());

                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    //Η π�?οειδοποίηση στον χ�?ήστη (�?τι π�?έπει να ανοίξει τα MobileData)
    private void showMobileDataDisabledToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alter_dialog_for_mobile_data))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button_of_alter_dialog_for_mobile_data),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                                //Χτίζει και στέλνει το Analytics Event.
                                t.send(new HitBuilders.EventBuilder()
                                        .setCategory(categoryWiFiID)
                                        .setAction(actionGoSettingsID)
                                        .build());

                                Intent callDataRoamingSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                                startActivity(callDataRoamingSettingIntent);//Πηγαίνει τον χ�?ήστη στην Activity με τις �?υθμίσεις του WiFi, �?στε να μπο�?εί να το ενε�?γοποιήσει
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_button_of_alter_dialog_for_mobile_data),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
			            	
			            	/*Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
			    			//Χτίζει και στέλνει το Analytics Event.
			    			t.send(new HitBuilders.EventBuilder()
			    			.setCategory(categoryWiFiID)
			    			.setAction(actionCancelID)
			    			.build());*/

                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();//Αποσυνδε�?μαστε απ�? την υπη�?εσία
        }
        catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }

    //* Λειτου�?γία για να φο�?τωθεί ο χάρτης. Αν ο χάρτης δεν έχει δημιου�?γηθεί αυτή θα τον δημιου�?γήσει.
    private void initilizeMap(boolean fromResume) {
        if (googleMapv2 == null) {
            googleMapv2 = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.mapv2)).getMap();
            
           /* CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(35.516622, 24.016934)).zoom(16.5f).build();//Για κέντ�?ο βάλαμε μια θέση στα Χανιά*/

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(35.366836, 24.482159)).zoom(14.5f).build();//Για κέντρο βάλαμε μια θέση στο Ρέθυμνο

            googleMapv2.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            googleMapv2.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMapv2.setMyLocationEnabled(false); // Δεν επιτ�?έπουμε τον εντοπισμ�? θέσης απ�? το GoogleMap API
            googleMapv2.getUiSettings().setMyLocationButtonEnabled(false);//�?�?τε το κουμπί εντοπισμ�?�? θέσης




            //Ελέγχει αν ο χάρτης δημιου�?γήθηκε με επιτυχία η �?χι
            if (googleMapv2 == null) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.unable_to_creat_maps), Toast.LENGTH_SHORT)
                        .show();
            }
        }

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());

        //Αν υπά�?χει σ�?νδεση internet και χάρτης
        if(mConnectionDetector.isInternetAvailable() && googleMapv2 != null){

            //Εάν η οθονη δεν εμφανίζεται απ�? Resume τ�?τε κατεβάζει τις διαδ�?ομές των χ�?ηστ�?ν απ�? τον server
            if(fromResume == false){
                new DownloadGPXFileAsync(MainActivity.this,googleMapv2).execute(allPathsFileUrl);
            }

            //αλλι�?ς εμφανίζει τις διαδ�?ομές απ�? το merge.gpx α�?χείο αν έχει κατέβει στην συσκευή
            else{
                String myNewFileName = "merge.gpx";
                Context mContext=MainActivity.this.getApplicationContext();
                File mFile = new File (mContext.getFilesDir(), myNewFileName);
                if(mFile.exists()){
                    ParsingGPXForDrawing parsingForDrawing = new ParsingGPXForDrawing(mFile,googleMapv2);

                    parsingForDrawing.decodeGPXForTrksegs();

                    parsingForDrawing.decodeGpxForWpts();
                }
                //αλλι�?ς κατεβάζει τις διαδ�?ομές των χ�?ηστ�?ν απ�? τον server
                else{
                    new DownloadGPXFileAsync(MainActivity.this,googleMapv2).execute(allPathsFileUrl);
                }

            }

        }
        //αν δεν υπά�?χει χάρτης �?τε σ�?νδεση internet
        else if (googleMapv2 != null ){
            String myNewFileName = "merge.gpx";
            Context mContext=MainActivity.this.getApplicationContext();
            File mFile = new File (mContext.getFilesDir(), myNewFileName);

            //αν έχει κατέβει το α�?χείο με τις διαδ�?ομές τις εμφανίζει
            if(mFile.exists()){
                ParsingGPXForDrawing parsingForDrawing = new ParsingGPXForDrawing(mFile,googleMapv2);

                parsingForDrawing.decodeGPXForTrksegs();

                parsingForDrawing.decodeGpxForWpts();
            }
            //αλλι�?ς εμφανίζεται κατάλληλο μήνυμα
            else{
                Toast.makeText(getApplicationContext(),
                        getString(R.string.unable_to_load_all_paths), Toast.LENGTH_SHORT)
                        .show();
            }

        }



    }

    //Συνά�?τηση για να φο�?τ�?σει τις διαδ�?ομές των χ�?ηστ�?ν αν τ�?έχει η υπη�?εσία καταγ�?αφής της διαδ�?ομής
    private void loadAllPathsIfServiceRunning(){
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());
        if(mConnectionDetector.isInternetAvailable() && googleMapv2 != null){




            String myNewFileName = "merge.gpx";
            Context mContext=MainActivity.this.getApplicationContext();
            File mFile = new File (mContext.getFilesDir(), myNewFileName);
            if(mFile.exists()){
                ParsingGPXForDrawing parsingForDrawing = new ParsingGPXForDrawing(mFile,googleMapv2);

                parsingForDrawing.decodeGPXForTrksegs();

                parsingForDrawing.decodeGpxForWpts();
            }
            else{
                new DownloadGPXFileAsync(MainActivity.this,googleMapv2).execute(allPathsFileUrl);
            }



        }
        else if (googleMapv2 != null ){
            String myNewFileName = "merge.gpx";
            Context mContext=MainActivity.this.getApplicationContext();
            File mFile = new File (mContext.getFilesDir(), myNewFileName);

            if(mFile.exists()){
                ParsingGPXForDrawing parsingForDrawing = new ParsingGPXForDrawing(mFile,googleMapv2);

                parsingForDrawing.decodeGPXForTrksegs();

                parsingForDrawing.decodeGpxForWpts();
            }

            else{
                Toast.makeText(getApplicationContext(),
                        getString(R.string.unable_to_load_all_paths), Toast.LENGTH_SHORT)
                        .show();
            }

        }

    }


    //�?εντ�?ά�?ει τον χάρτη στην θέση του χ�?ήστη και σχηματίζει μια polyline απ�? τις θέσεις που έχει πε�?άσει ο χ�?ήστης
    private void gotoMyLocation(double lat, double lng){

        if (googleMapv2 != null){

            if(enableNewLocationAddToPolyline=true){//�?ταν δηλαδή δεν δημιου�?γείται ένα polyline με τις παλιές θέσεις του χ�?ήστη
                rectOptions.add(new LatLng(lat, lng));//Π�?οσθέτει την καινο�?για θέση του χ�?ήστη
                polyline = googleMapv2.addPolyline(rectOptions);//�?αι την εμφανίζει εδ�?
            }

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(lat, lng)).zoom(16.5f).build();

            googleMapv2.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));//�?εντ�?ά�?ει τον χάρτη στη νέα θέση του χ�?ήστη

        }
    }

    //Π�?οσθέει το tag που έβαλε ο χ�?ήστης
    private void addMarkerToMap(double lat, double lng, String pathtype){
        if (googleMapv2 != null){
            googleMapv2.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(pathtype)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        }
    }

    //Εμφανίζει ένα διάλογο λάθους κατά την αποτυχή π�?οσπάθεια σ�?νδεσης της MyLocationService στην google play service
    private void DisplayErrorDialog(int resultCode){
        // Εμφανίζει τον διάλογο λάθους
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
        if (dialog != null) {
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            errorFragment.setDialog(dialog);
            errorFragment.show(getSupportFragmentManager(), getString(R.string.app_name));
        }
    }

    //�?�?ίζει ένα DialogFragment για να εμφανίχει τον διάλογο λάθους που δημιου�?γείται στην showErrorDialog.
    public static class ErrorDialogFragment extends DialogFragment {

        // "Γενικ�?" (Global) πεδίο που πε�?ιέχει τον διάλογο λάθους
        private Dialog mDialog;


        // Π�?οεπιλεγμένος κατασκευαστής. �?έτει τον διάλογο του πεδίου σε null.
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // �?έτει το διαλ�?γο για να εμφανισεί.  @param dialog �?νας διάλογος λάθους
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }


        // Η μέθοδος αυτή π�?έπει να επιστ�?έ�?ει έναν Dialog στο DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Δι�?γκωση (Inflate) του μενο�?. Π�?οσθέτει στοιχεία στην γ�?αμμή ενε�?γει�?ν (action bar) αν υπά�?χει.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Χει�?ίζεται τα κλικ του χ�?ήστη στο μενο�?.
        int id = item.getItemId();

        switch (id) {
			/*case R.id.action_settings:
				return true;*/
            case R.id.action_menu:

                Tracker t1 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t1.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRecordMenuID)
                        .setAction(actionMenuChoiseID)
                        .build());

                return true;
            case R.id.submenu_show_maps:

                Tracker t2 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t2.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRecordMenuID)
                        .setAction(actionShowMapsID)
                        .build());

                Intent mapsIntent= new Intent(MainActivity.this,MapsActivity.class);
                mapsIntent.putExtra("mapsID",2);
                startActivity(mapsIntent);

                return true;

            case R.id.submenu_review_paths:

                Tracker t3 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t3.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRecordMenuID)
                        .setAction(actionMakeReviewID)
                        .build());

                Intent intent= new Intent(MainActivity.this,ReviewPathActivity.class);
                startActivity(intent);
                return true;
            case R.id.submenu_rank__list_of_players:

                Tracker t4 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t4.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRecordMenuID)
                        .setAction(actionRankingID)
                        .build());

                Intent intent2= new Intent(MainActivity.this,RankListOfPlayersActivity.class);
                startActivity(intent2);
                return true;
            case R.id.submenu_log_out:

                Tracker t5 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t5.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRecordMenuID)
                        .setAction(actionLogOutID)
                        .build());

                userFunctions.logoutUser(getApplicationContext());
                if (MyLocationService.isRunning()){
                    doUnbindService();//�?εσυνδέεται απ�? την υπη�?εσία (αν είναι συνδεδεμένη)
                    //Σταματάει την υπη�?εσία (αν τ�?έχει)
                    try{
                        stopService(new Intent(MainActivity.this, MyLocationService.class));//Σταματάει την υπη�?εσία
                    }
                    catch(Throwable thr){
                        Log.e("MainActivity", "Failed to stop the service", thr);
                    }

                }
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                // Σταματάει την MainActivity
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
