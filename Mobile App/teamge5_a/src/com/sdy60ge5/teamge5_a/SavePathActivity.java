package com.sdy60ge5.teamge5_a;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
// import com.ippokratis.mapmaker2.R;
import com.sdy60ge5.teamge5_a.R;
import com.sdy60ge5.teamge5_a.library.AsynGPXWriter;
import com.sdy60ge5.teamge5_a.library.ConnectionDetector;
import com.sdy60ge5.teamge5_a.library.UserFunctions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/*********************************************************************************************************************
 * Η Activity για την οθ�?νη που επιτ�?έπει στον χ�?ήστη να ανεβάσει την διαδ�?ομή του στον server καθ�?ς και να δει τους *
 * π�?ντους που θα κε�?δίσει στην πε�?ίπτωση που το κάνει.                                                              *
 * *******************************************************************************************************************/
public class SavePathActivity extends Activity {

//    private static String uploadPathUrl = "http://corfu.pathsonmap.eu/request_log_reg_store_path.php";//Το Url στο οποίο θα ανέβει η διαδ�?ομή
    private static String uploadPathUrl = "http://snf-818423.vm.okeanos.grnet.gr/teamge5_a/request_log_reg_store_path.php";//Το Url στο οποίο θα ανέβει η διαδ�?ομή

    //private static String uploadPathUrl = "http://192.168.1.65/mapmaker_local/request_log_reg_store_path.php";//Το Url στο οποίο θα ανέβει η διαδ�?ομή (τοπικά)

    //Τα Id για τα googleAnalytics events
    private static String categorySavePathId = "SavePathActivity buttons";
    private static String actionDiscardId = "Discard button";
    private static String actionUploadPathId = "Upload Path button";
    private static String categoryRecordFinishID = "Record Finish Menu";
    private static String actionMenuChoiseID = "Menu choise";
    private static String actionUploadDropBoxID = "UploadDropbox";
    private static String actionBackToRecordID = "Back To Record";
    private static String categoryDiscardDialogID = "Discard Alter Dialog";
    private static String actionNoID = "No";
    private static String actionYesID = "Yes";

    private boolean userHasUploadedThePath = false;//Αν ο χ�?ήστης έχει ανεβάσει το α�?χείο στο server
    private TextView tvDistanceMessage;//Εμφανίζει μήνυμα π�?σους π�?ντους θα κε�?ίδει ανάλογα με την απ�?σταση που διένυσε ο χ�?ήστης
    private TextView tvTagLocationsMessage;//Εμφανίζει μήνυμα π�?σους π�?ντους θα κε�?ίδει ανάλογα με τα tags που έβαλε ο χ�?ήστης
    private int numberofTagLocations;//Α�?ιθμ�?ς των tags που έβαλε ο χ�?ήστης
    private float totaldistance;//Η απ�?σταση που έχει διαν�?σει ο χ�?ήστης
    private ProgressDialog pDialog;//Για να δείξει στον χ�?ήστη �?τι ανεβαίνει το gpx α�?χείο


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.record_finish);
        setContentView(R.layout.activity_save_path);

        //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
        t.setScreenName("Record Finish screen");
        t.send(new HitBuilders.AppViewBuilder().build());

        tvDistanceMessage=(TextView)findViewById(R.id.distance_message_to_user);
        tvTagLocationsMessage=(TextView)findViewById(R.id.tag_locations_message_to_user);
        Intent intent = getIntent();//Παί�?νει το intent απ�? την mainactivity
        numberofTagLocations = intent.getExtras().getInt("numberofTagLocations");
        totaldistance = intent.getExtras().getFloat("distance");//Η απ�?σταση που έχει διαν�?σει ο χ�?ήστης
        double points=0;//�?ι π�?ντοι που θα κε�?δίσει ο χ�?ήστης

        points = Math.round(totaldistance) * 0.05;//�? χ�?ήστης κε�?δίζει 5 π�?ντους για κάθε 100 μέτ�?α

        int pointsOfTagLocations = numberofTagLocations * 20;//�? χ�?ήστης κε�?δίζει 20 π�?ντους για κάθε tag location;

        //Το μήνυμα που λέει στον χ�?ήστη π�?σους π�?ντους θα κε�?δίσει για την απ�?σταση που διένυσε
        tvDistanceMessage.setText(getString(R.string.first_part_of_distance_message_to_user)+" "+String.valueOf(points)+" "
                +getString(R.string.second_part_of_distance_message_to_user)+" "+String.valueOf(Math.round(totaldistance))+" "
                +getString(R.string.third_part_of_distance_message_to_user));
        tvTagLocationsMessage.setText(getString(R.string.first_part_of_tag_locations_message_to_user)+" "+String.valueOf(pointsOfTagLocations)+" "
                +getString(R.string.second_part_of_tag_locations_message_to_user)+" "+String.valueOf(numberofTagLocations)+" "
                +getString(R.string.third_part_of_tag_locations_message_to_user));

        //Η δημιου�?γία του gpx α�?χείου που δημιου�?γείται απ�? το gps
        Context c = getApplicationContext();
        File file = new File(c.getFilesDir(), "path.gpx");//Το �?νομα του α�?χείου που θα αποθηκευτεί η διαδ�?ομή
        File segmentfile = new File(c.getFilesDir(), "segmentOfTrkpt.txt");//Tο �?νομα του α�?χείου που πε�?ιέχει το τμήμα με τα trackpoints
        File segmentOfWayPointsFile = new File(c.getFilesDir(), "segmentOfWpt.txt");//Tο �?νομα του α�?χείου που πε�?ιέχει το τμήμα με τα waypoints

        if (file.exists()){//Αν το α�?χείο πε�?ιέχει δεδομένα απ�? μια παλι�?τε�?η διαδ�?ομή, το "καθα�?ίζουμε"
            String string1 = "";
            FileWriter fWriter;
            try{
                fWriter = new FileWriter(file);
                fWriter.write(string1);
                fWriter.flush();
                fWriter.close();
            }
            catch (Exception e) {
                e.printStackTrace();}
        }

        //Η δημιου�?γία του gpx α�?χείου που δημιου�?γείται απ�? τον fused provider
        File fileGoogle = new File(c.getFilesDir(), "pathGoogle.gpx");//Το �?νομα του α�?χείου που θα αποθηκευτεί η διαδ�?ομή που έχει π�?οκ�?�?ει απ�? την google service
        File segmentfileGoogle = new File(c.getFilesDir(), "segmentOfTrkptGoogle.txt");//Tο �?νομα του α�?χείου που πε�?ιέχει το τμήμα με τα trackpoints που έχουν π�?οκ�?�?ει απ�? την google service
        File segmentOfWayPointsFileGoogle = new File(c.getFilesDir(), "segmentOfWptGoogle.txt");

        if (fileGoogle.exists()){//Αν το α�?χείο πε�?ιέχει δεδομένα απ�? μια παλι�?τε�?η διαδ�?ομή, το "καθα�?ίζουμε"
            String string1 = "";
            FileWriter fWriter;
            try{
                fWriter = new FileWriter(fileGoogle);
                fWriter.write(string1);
                fWriter.flush();
                fWriter.close();
            }
            catch (Exception e) {
                e.printStackTrace();}
        }

        //Εδ�? καταγ�?άφεται η διαδ�?ομή στα δ�?ο GPX α�?χεία
        AsynGPXWriter asynWrFile = new AsynGPXWriter(file,segmentfile,segmentOfWayPointsFile, SavePathActivity.this,fileGoogle,segmentfileGoogle,segmentOfWayPointsFileGoogle);

        asynWrFile.execute();
    }

    //�?ταν ο χ�?ήστης απο�?ί�?ει την διαδ�?ομή του δείχνει ένα μήνυμα �?τι η διαδ�?ομή θα χαθεί
    public void onBtnDiscardClicked(View view){

        //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
        //Χτίζει και στέλνει το Analytics Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(categorySavePathId)
                .setAction(actionDiscardId)
                .build());

        showDiscardAlertToUser();
    }

    //Η π�?οειδοποίηση στον χ�?ήστη, �?τι η διαδ�?ομή θα χαθεί
    private void showDiscardAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alter_dialog_for_discard))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button_of_alter_dialog_for_discard),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                                //Χτίζει και στέλνει το Analytics Event.
                                t.send(new HitBuilders.EventBuilder()
                                        .setCategory(categoryDiscardDialogID)
                                        .setAction(actionYesID)
                                        .build());

                                finishActivity();
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_button_of_alter_dialog_for_discard),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){

                        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                        //Χτίζει και στέλνει το Analytics Event.
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory(categoryDiscardDialogID)
                                .setAction(actionNoID)
                                .build());

                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void finishActivity(){
        this.finish();//Σταματάει την SavePathActivity
    }

    //Η συνά�?τηση που καλείται �?ταν ο χ�?ήστης επιλέξει να ανεβάσει την διαδ�?ομή
    public void onBtnUploadPathClicked(View view){

        //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
        //Χτίζει και στέλνει το Analytics Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(categorySavePathId)
                .setAction(actionUploadPathId)
                .build());

        //Αν ο χ�?ήστης έχει ανεβάσει ήδη την διαδ�?ομή, δεν τον αφήνει να την ξανανεβάσει
        if(userHasUploadedThePath == true){
            Toast.makeText(this,
                    getString(R.string.msg_already_uploaded), Toast.LENGTH_LONG).show();
        }
        else{
            //Εδ�? θα ανέβουν τα GPX α�?χεία στον Server

            if (android.os.Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            //Αν υπά�?χει σ�?νδεδη internet η διαδ�?ομή θα ανέβει - αλλι�?ς θα βγάλει ένα μήνυμα στον χ�?ήστη
            ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());

            if(mConnectionDetector.isNetworkConnected() == true &&  mConnectionDetector.isInternetAvailable()==true){
                //Υπά�?χει σ�?νδεση internet και έτσι καλείται η UploadGpxFileToServer �?στε να ανέβουν τα α�?χεία
                new UploadGpxFileToServer(numberofTagLocations,totaldistance).execute();
                userHasUploadedThePath = true;//Τα α�?χεία ανέβηκαν
            }
            else{//Δεν υπά�?χει σ�?νδεση internet
                Toast.makeText(getApplicationContext(), "You must be connected to the internet", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Background Async Task για να ανέβουν τα gpx α�?χεία - με αίτημα HTTP
     * */
    class UploadGpxFileToServer extends AsyncTask<String, String, String> {

        private int mnumberOfTags;//ο α�?ιθμ�?ς των tags (waypoints)
        private float mdistance;
        String data;
        String json = "";
        String KEY_SUCCESS = "success";
        String KEY_MESSAGE = "message";
        String KEY_ERROR = "error";
        String KEY_ERROR_MESSAGE = "error_msg";

        public UploadGpxFileToServer(int numberOfTags,float distance){

            mnumberOfTags=numberOfTags;
            mdistance=distance;
        }

        @Override
        protected void onPreExecute() {//Δείχνουμε �?τι η διαδ�?ομή ανεβαίνει στον χ�?ήστη
            super.onPreExecute();
            pDialog = new ProgressDialog(SavePathActivity.this);
            pDialog.setMessage("Uploading path...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res ="";
            String resMes="";
            try{

                Context c = getApplicationContext();
                File file = new File(c.getFilesDir(), "path.gpx");//Το �?νομα του α�?χείου που έχει αποθηκευτεί η διαδ�?ομή

                File fileGoogle = new File(c.getFilesDir(), "pathGoogle.gpx");//Το �?νομα του α�?χείου που έχει αποθηκευτεί η διαδ�?ομή απ�? την google service

                Log.v("SavePathActivity.java", "postURL: " + uploadPathUrl);

                // ένας νέος HttpClient
                HttpClient httpClient = new DefaultHttpClient();

                // post header - κεφαλίδα
                HttpPost httpPost = new HttpPost(uploadPathUrl);

                
             // addition proposed by https://stackoverflow.com/questions/26210679/post-files-to-server-from-android-multipartentitybuilder-http?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
             // April 30, 2018 10:00am.                
                             
                String boundary = "-------------" + System.currentTimeMillis();             
                httpPost.setHeader("Content-type", "multipart/form-data; boundary="+boundary);
             // addition proposed by https://stackoverflow.com/questions/26210679/post-files-to-server-from-android-multipartentitybuilder-http?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
             // April 30, 2018 10:00am.
                            
                
                
                //Το fileBody θα πε�?ιέχει το α�?χείο gpx που δημιο�?�?γησε το gps
                FileBody fileBody = new FileBody(file);

                //Το fileBodyGoogle θα πε�?ιέχει το α�?χείο gpx που δημιο�?�?γησε ο fused provider
                FileBody fileBodyGoogle = new FileBody(fileGoogle);

                //Εδ�? αποκτάται το uid του χ�?ήστη
                UserFunctions userFunctions = new UserFunctions();
                String uid = String.valueOf(userFunctions.getUserUid(getApplicationContext()));
                StringBody stringBody = new StringBody(uid,ContentType.TEXT_PLAIN);

                StringBody stringBody2 = new StringBody(String.valueOf(mnumberOfTags),ContentType.TEXT_PLAIN);//ο α�?ιθμ�?ς των tags της διαδ�?ομής
                StringBody stringBody3 = new StringBody("storageFile",ContentType.TEXT_PLAIN);//στο post θα βάλουμε στο tag = storageFile για να ξέ�?ει ο server τι να κάνει
                int metersOfPath = Math.round(mdistance);//Το μέτ�?α της διαδ�?ομής
                StringBody stringBody4 = new StringBody(String.valueOf(metersOfPath),ContentType.TEXT_PLAIN);

                //Δημιου�?γία του builder
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                
             // April 30, 2018 10:00am.
             // addition proposed by https://stackoverflow.com/questions/26210679/post-files-to-server-from-android-multipartentitybuilder-http?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
                builder.setBoundary(boundary);
             // addition proposed by https://stackoverflow.com/questions/26210679/post-files-to-server-from-android-multipartentitybuilder-http?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
             // April 30, 2018 10:00am.
                
                
                //Π�?οσθέτουμε τα α�?χεία και τα strings στον builder
                builder.addPart("file", fileBody);
                builder.addPart("fileGoogle", fileBodyGoogle);
                builder.addPart("tag", stringBody3);
                builder.addPart("player_id",stringBody);
                builder.addPart("tagsOfPath",stringBody2);
                builder.addPart("meters",stringBody4);
                HttpEntity reqEntity = builder.build();

                httpPost.setEntity(reqEntity);

                // εκτέλεση του HTTP post αιτήματος
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();

                //Εδ�? θα διαβαστεί η απ�?κ�?ιση του αιτήματος
                InputStream is = null;
                is = resEntity.getContent();//Δημιου�?γεί ένα νέο InputStream αντικείμενο της οντ�?τητας.
                try {
                    //�?αλ�?πτει(wrap) έναν υπά�?χον reader και κάνει buffer την είσοδο
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "utf-8"), 8);//InputStreamReader είναι μια κλάση που μετατ�?έπει ένα �?ε�?μα bytes σε ένα �?ε�?μα χα�?ακτή�?ων. Εδ�? χ�?ησιμοποιείται η κωδικοποίηση utf-8
                    StringBuilder sb = new StringBuilder();//�?ία τ�?οποποιήσιμη ακολουθία χα�?ακτή�?ων για χ�?ήση στη δημιου�?γία αλφα�?ιθμητικ�?ν
                    String line = null;
                    while ((line = reader.readLine()) != null) {//Διαβάζει μια γ�?αμμή κειμένου
                        sb.append(line + "\n");//Π�?οσθέτει την συμβολοσει�?ά
                    }
                    is.close();

                    json = sb.toString();//Επιστ�?έφει τα πε�?ιεχ�?μενα αυτο�? του builder (του sb εδ�?)
                    Log.e("JSON", json);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }
                // Π�?οσπαθεί να αναλ�?σει την συμβολοσει�?ά σε ένα αντκείμνο JSON
                JSONObject jObj = null;//�?να json αντικείμενο
                try {
                    jObj = new JSONObject(json);//Δημιου�?γεί ένα νέο JSONObject με τις αντιστοιχίσεις �?νοματος / τιμής απ�? τη συμβολοσει�?ά JSON.
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

                if (jObj.getString(KEY_SUCCESS) != null){

                    res = jObj.getString(KEY_SUCCESS);

                    if(Integer.parseInt(res) == 1){//Αν ήταν επιτυχής η αποθήκευση

                        resMes=jObj.getString(KEY_MESSAGE);//Πά�?ε το μήνυμα της απ�?κ�?ισης
                    }
                    else if(Integer.parseInt(res) == 0 && jObj.getString(KEY_ERROR) != null){
                        if(Integer.parseInt(jObj.getString(KEY_ERROR)) == 1){//Αν δεν ήταν επιτυχής η αποθήκευση
                            resMes=jObj.getString(KEY_ERROR_MESSAGE);//Πά�?ε το μήνυμα λάθους
                        }

                    }
                }

                //Γ�?άφει στο log την απ�?κ�?ιση του αιτήματος
                if (resEntity != null) {

                    String responseStr = EntityUtils.toString(resEntity).trim();
                    Log.v("SavePathActivity.java", "Response: " +  responseStr);
                }



            } catch (NullPointerException e) {
                e.printStackTrace();
                return resMes="Error Connecting to server";

            }
            catch (Exception e) {
                e.printStackTrace();

            }

            return resMes;//Γυ�?ίζει το μήνυμα που θα εμφανιστεί στον χ�?ήστη
        }

        //�?ταν τελει�?ση η π�?οσπάθεια ανεβάσματος της διαδ�?ομής σταμάτησε τον διάλογο - εμφάνισε το κατάλληλο μήνυμα και σταμάτα την activity
        @Override
        protected void onPostExecute(String resMes) {

            // dismiss the dialog after getting all products
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), resMes,
                    Toast.LENGTH_LONG).show();
            finishActivity();
        }
    }


    //Για το harware button
    @Override
    public void onBackPressed() {
        //Αν χ�?ήστης δεν έχει ανεβάσει το path θα τον �?ωτήσει
        //αν θέλει να γυ�?ίσει πίσω
        if(userHasUploadedThePath==false){
            showDiscardAlertToUser();
        }
        else{
            super.onBackPressed();
        }//αλλι�?ς θα γυ�?ίσει πίσω
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(SavePathActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(SavePathActivity.this).reportActivityStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Δι�?γκωση (Inflate) του μενο�?. Π�?οσθέτει στοιχεία στην γ�?αμμή ενε�?γει�?ν (action bar) αν υπά�?χει.
        getMenuInflater().inflate(R.menu.save_path, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Εδ�? γίνεται ο χει�?ισμ�?ς των action bar item κλικ. Η action bar θα χει�?ιστεί
        // αυτ�?ματα τα κλικ του Home/Up button,
        // αν έχει καθο�?ιστεί μια parent activity στο AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}*/


        switch (item.getItemId()) {
            // Απ�?κ�?ιση του action bar's Up/Home κουμπιο�?
            case android.R.id.home:

                Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRecordFinishID)
                        .setAction(actionBackToRecordID)
                        .build());

                //Αν χ�?ήστης δεν έχει ανεβάσει το path θα τον �?ωτήσει
                //αν θέλει να γυ�?ίσει πίσω
                if(userHasUploadedThePath==false){
                    showDiscardAlertToUser();
                }
                else{
                    NavUtils.navigateUpFromSameTask(this);
                }//αλλι�?ς θα γυ�?ίσει πίσω

                return true;

            case R.id.action_menu1:

                Tracker t1 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t1.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRecordFinishID)
                        .setAction(actionMenuChoiseID)
                        .build());

                return true;

            //Απ�?κ�?ιση του DropBox μενο�?
            case R.id.submenu_drop_box:

                Tracker t2 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t2.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRecordFinishID)
                        .setAction(actionUploadDropBoxID)
                        .build());

                if (android.os.Build.VERSION.SDK_INT > 9)
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                //αν υπά�?χει σ�?νδεση internet, ξεκινάει την dropbox activity
                ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());

                if(mConnectionDetector.isNetworkConnected() == true &&  mConnectionDetector.isInternetAvailable()==true){
                    Intent intent2 = new Intent(this, DropBoxActivity.class);
                    intent2.putExtra("filename", "path.gpx");
                    intent2.putExtra("filenameGoogle", "pathGoogle.gpx");
                    startActivity(intent2);
                }
                else{//Αλλι�?ς λέει στον χ�?ήστη �?τι π�?έπει να συνδεθεί στο internet

                    Toast.makeText(getApplicationContext(), "You must be connected to the internet", Toast.LENGTH_LONG).show();

                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
