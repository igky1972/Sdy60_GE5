package com.sdy60ge5.teamge5_a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
// import com.ippokratis.mapmaker2.R;
import com.sdy60ge5.teamge5_a.R;
import com.sdy60ge5.teamge5_a.library.ConnectionDetector;
import com.sdy60ge5.teamge5_a.library.UserFunctions;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
//import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/*******************************************************************************************
 * Η Activity στην οποία οι χ�?ήστες μπο�?ο�?ν να σχολιάσουν τις διαδ�?ομές των άλλων χ�?ηστ�?ν  *
 * *****************************************************************************************/
public class ReviewPathActivity extends Activity {

//    private static String request_path_url = "http://corfu.pathsonmap.eu/requestPath.php";
//    private static String show_no_path_url = "http://corfu.pathsonmap.eu/noPath.php?path=";
//    private static String show_path_url = "http://corfu.pathsonmap.eu/showGpxFileInMap.php?path=";
    private static String request_path_url = "http://snf-818423.vm.okeanos.grnet.gr/teamge5_a/requestPath.php";
    private static String show_no_path_url = "http://snf-818423.vm.okeanos.grnet.gr/teamge5_a/noPath.php?path=";
    private static String show_path_url = "http://snf-818423.vm.okeanos.grnet.gr/teamge5_a/showGpxFileInMap.php?path=";

    //Αν ο server έχει στηθεί τοπικά
    //private static String request_path_url = "http://192.168.1.65/mapmaker_local/requestPath.php";
    //private static String show_no_path_url = "http://192.168.1.65/mapmaker_local/noPath.php?path=";
    //private static String show_path_url = "http://192.168.1.65/mapmaker_local/showGpxFileInMap.php?path=";

    //Τα Id για τα googleAnalytics events
    private static String categoryReviewPathId = "ReviewPathActivity buttons";
    private static String actionDiscardNewPathId = "Discard_NewPath button";
    private static String actionSubmitId = "Submit button";
    private static String categoryMakeReviewID = "Make a Review Menu";
    private static String actionMenuChoiseID = "Menu choise";
    private static String actionShowMapsID = "Show Maps";
    private static String actionRankingID="Ranking";
    private static String actionBackToRecord = "Back To Record";

    private int path_id;//Το uid του μονοπατιο�?
    private WebView browser;//Σε αυτ�? το WebView θα φαίνεται το μονοπάτι που θα γίνει review (ή �?τι δεν υπά�?χει μονοπάτι για review)
    private Button btnSubmit;//Το κουμπί που ο παίκτης θα υποβάλει το review του
    private Button btnDiscard;//Το κουμπί για να μπο�?εί ο παίκτης να απο�?�?ί�?ει το μονοπάτι
    private ProgressDialog pDialog;//Για να δείξει στον χ�?ήστη �?τι αιτείται ένα μονοπάτι
    private int user_id;//Το uid του παίκτη
    private Spinner spinnerReview;//Για επιλογή κ�?ιτικής μονοπατιο�?
    private Spinner spinnerReviewTags;//Για επιλογή κ�?ιτικής μονοπατιο�?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.submenu_review_paths);
        setContentView(R.layout.activity_review_path);

        //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
        t.setScreenName("Make a review screen");
        t.send(new HitBuilders.AppViewBuilder().build());

        spinnerReview = (Spinner)findViewById(R.id.spinnerReview);
        spinnerReviewTags = (Spinner)findViewById(R.id.spinnerReview2);
        browser = (WebView)findViewById(R.id.webView1);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnDiscard = (Button)findViewById(R.id.btnDiscard);

        spinnerReview.setSelection(4);
        spinnerReviewTags.setSelection(4);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Αν υπά�?χει σ�?νδεδη internet θα γίνει π�?οσπάθεια φ�?�?τωσης εν�?ς μονοπατιο�? - αλλι�?ς θα βγάλει ένα μήνυμα στον χ�?ήστη
        ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());

        if(mConnectionDetector.isNetworkConnected() == true &&  mConnectionDetector.isInternetAvailable()==true){

            UserFunctions userFunctions = new UserFunctions();
            user_id = userFunctions.getUserUid(getApplicationContext());//Γυ�?ίζει το uid του χ�?ήστη που είναι αποθηκευμένο στην sqlite database του κινητο�?
            new RequestRandomPath(user_id).execute();

        }
        else
        {	//�?ήνυμα στον χ�?ήστη �?τι δεν υπά�?χει σ�?νδεση internet
            Toast.makeText(getApplicationContext(), "You must be connected to the internet", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Background Async Task για να πά�?ει ένα τυχαίο μονοπάτι(την διαδ�?ομή ουσιαστικά του gpx α�?χείου του στον server)
     * */
    class RequestRandomPath extends AsyncTask<String, String, String> {

        private int mplayerID;
        private String path_request = "pathRequest";//αυτή την τιμή θα πά�?ει η ετικέτα tag στο http έτοιμα για path
        JSONObject jObj = null;//�?να json αντικείμενο
        String data;
        String json = "";

        // �?νομα κ�?μβου(node) JSON απ�?κ�?ισης
        String KEY_SUCCESS = "success";
        String KEY_MESSAGE = "message";
        String KEY_ERROR = "error";
        String KEY_ERROR_MESSAGE = "error_msg";
        String PATH = "path";//Η διαδ�?ομή του gpx α�?χείου
        String PATH_ID= "path_id";//Το uid της διαδ�?ομής που γυ�?ίζει

        public RequestRandomPath(int playerID){

            mplayerID=playerID;//Το id του χ�?ήστη που αιτείται το μονοπάτι

        }

        /**
         * Π�?ιν α�?χίσει το background thread δείξε ένα διάλογο π�?ο�?δου
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReviewPathActivity.this);
            pDialog.setMessage("Request path...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        //Γυ�?ίζει το path (την διαδ�?ομή) του gpx α�?χείου του μονοπατιο�?-Αν υπά�?χει σφάλμα το path παί�?νει το μήνυμα σφάλματος
        @Override
        protected String doInBackground(String... params) {
            InputStream is = null;//Διαβάζει δεδομένα απ�? το δίκτυο
            String path ="";//Εδ�? θα πε�?ιέχεται η διαδ�?ομή του gpx α�?χείου στον server

            try{

                DefaultHttpClient httpClient = new DefaultHttpClient();//Π�?οεπιλεγμένη υλοποίηση εν�?ς πελάτη HTTP
                HttpPost httpPost = new HttpPost(request_path_url);//Η μέθοδος HTTP POST
                List<NameValuePair> parames = new ArrayList<NameValuePair>();
                parames.add(new BasicNameValuePair("tag", path_request));//To tag στο http αίτημα θα είναι path_request
                parames.add(new BasicNameValuePair("playerID", String.valueOf(mplayerID)));//To playerID στο http αίτημα θα πά�?ει το uid του παίκτη

                //UrlEncodedFormEntity είναι μια οντ�?τητα που αποτελείται απ�? μια λίστα url-κωδικοποιημένα ζε�?γη. Η setEntity χει�?ίζεται την οντ�?τητα στην αίτηση
                UrlEncodedFormEntity form = new UrlEncodedFormEntity(parames,"UTF-8");
                form.setContentEncoding(HTTP.UTF_8);
                httpPost.setEntity(form);//Το utf-8 το βάλαμε �?στε να υποστη�?ίζει ελληνικά
                HttpResponse httpResponse = httpClient.execute(httpPost);//Εκτελεί την αίτηση και γυ�?ίζει την απάντηση
                HttpEntity httpEntity = httpResponse.getEntity();//Λαμβάνει το μήνυμα οντ�?τητας αυτής της απάντησης, αν υπά�?χει
                is = httpEntity.getContent();//Δημιου�?γεί ένα νέο InputStream αντικείμενο της οντ�?τητας.
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                json = sb.toString();//Επιστ�?έφει τα πε�?ιεχ�?μενα αυτο�? του builder (του sb εδ�?)-Τα πε�?ιεχ�?μανα θα είναι ένα json αντικείμενο
                Log.e("JSON", json);
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            // Π�?οσπαθεί να αναλ�?σει την συμβολοσει�?ά σε ένα αντκείμνο JSON
            try {
                jObj = new JSONObject(json);//Δημιου�?γεί ένα νέο JSONObject με τις αντιστοιχίσεις �?νοματος / τιμής απ�? τη συμβολοσει�?ά JSON.
            }
            catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            //Αναλ�?ει το json αντικείμενο και βάζει στο path την διαδ�?ομή του gpx α�?χείου και στο path_id το uid του μονοπατιο�?-Αν δεν υπά�?χει μονοπάτι τ�?τε το θεω�?εί 0
            try {

                if (jObj.getString(KEY_SUCCESS) != null) {

                    String result = jObj.getString(KEY_SUCCESS);

                    if (Integer.parseInt(result) == 1){//Αν η αίτηση ήταν επιτυχής
                        JSONObject json_path =jObj.getJSONObject("path");
                        path = json_path.getString(PATH);//Η διαδ�?ομή του gpx στον server
                        path_id= json_path.getInt(PATH_ID);//To uid της διαδ�?ομής
                    }
                    else{
                        path = jObj.getString(KEY_ERROR_MESSAGE);//Αν υπή�?ξε σφάλμα, βάλε στο path το μήνυμα σφάλματος
                        path_id = 0;
                    }
                }
                else{
                    path="Oops! An error occurred!";//Υπή�?ξε σφάλμα
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return path;
        }


        //Εμφανίζει την σελίδα με το μονοπάτι στον χά�?τη ή τη σελίδα με το μήνυμα σφάλματος
        @Override
        protected void onPostExecute(String path) {

            String url ="";//Το url της σελίδας που θα γυ�?ίσει
            // Απο�?�?ίπτει τον διάλογο �?ταν γυ�?ίσει το path
            pDialog.dismiss();
            //Toast.makeText(getApplicationContext(), path + String.valueOf(path_id),
            //	   Toast.LENGTH_LONG).show();

            if (path_id == 0){//Αν δεν υπά�?χει διαδ�?ομή

                url = show_no_path_url + path;//Βάλε για url τη σελίδα σφάλματος. Η αίτηση θα γίνει με μέθοδο get με τιμή path το μήνυμα σφάλματος
            }
            else{

                //Εμφάνισε τα κουμπιά �?στε ο χ�?ήστης να μπο�?εί να κάνει review
                btnSubmit.setVisibility(View.VISIBLE);
                btnDiscard.setVisibility(View.VISIBLE);
                url = show_path_url + path;//Βάλε για url τη σελίδα του χά�?τη με το μονοπάτι. Η αίτηση θα γίνει με μέθοδο get με τιμή path τη διαδ�?ομή του gpx στο server
            }

            setUpWebViewDefaults(browser);
            browser.setWebViewClient(new WebViewClient(){

                public void onPageFinished(WebView view, String url) {
                    //Δείχνει ένα μήνυμα μ�?λις η σελίδα φο�?τ�?σει- Εδ�? δείχνει �?τι τα μονοπάτι φο�?τ�?νει αφο�? η σελίδα είναι ασ�?γχ�?ονη
                    Toast.makeText(getApplicationContext(), "Τhe path is loading",
                            Toast.LENGTH_LONG).show();
                }});

            browser.loadUrl(url);

        }
    }

    @SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        //Ενε�?γοποιεί την Javascript
        settings.setJavaScriptEnabled(true);

        //browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        browser.getSettings().setLoadsImagesAutomatically(true);

        // Επιτ�?έπει απομακ�?ισμένο debugging μέσω του chrome
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);

        }
    }

    //Το κουμπί submit
    public void submit(View view){

        //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
        //Χτίζει και στέλνει το Analytics Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(categoryReviewPathId)
                .setAction(actionSubmitId)
                .setLabel(String.valueOf(spinnerReview.getSelectedItem())+" "+String.valueOf(spinnerReviewTags.getSelectedItem()))
                .build());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        UserFunctions userFunction = new UserFunctions();

        int rated = spinnerReview.getSelectedItemPosition() + 1;//Η βαθμολογία του χ�?ήστη είναι η θέση που επέλεξε + 1 (αφο�? οι θέσεις α�?χίζουν απ�? το 0)
        int rated_tags = spinnerReviewTags.getSelectedItemPosition() + 1;//Η βαθμολογία του χ�?ήστη είναι η θέση που επέλεξε + 1 (αφο�? οι θέσεις α�?χίζουν απ�? το 0)
        JSONObject json = userFunction.reviewPath(user_id, path_id,rated,rated_tags);//�?αλεί το storeReview.php script �?στε να γ�?αφεί το review και γυ�?ίζει ένα κατάλληλο μήνυμα

        String message ="Oops! Something goes wrong";
        try {

            if (json.getString("success") != null) {

                String result = json.getString("success");

                if (Integer.parseInt(result) == 1){//Αν το review γ�?άφτηκε επιτυχ�?ς, το message παί�?νει το κατάλληλο μήνυμα
                    message = json.getString("message");
                }
                else{
                    message = json.getString("error_msg");//Αν το review δεν γ�?άφτηκε επιτυχ�?ς, το message παί�?νει το κατάλληλο μήνυμα

                }
            }
            else{//�?άποιο λάθος έγινε...
                message="Oops! Something goes wrong";
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
        startActivity(new Intent(ReviewPathActivity.this,ReviewPathActivity.class));//Η activity καλέι τον ευατ�? της για να μπο�?εί ο χ�?ήστης να κάνει ένα νέο review
        finish();
    }


    public void discard(View view){

        //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
        //Χτίζει και στέλνει το Analytics Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(categoryReviewPathId)
                .setAction(actionDiscardNewPathId)
                .build());

        Toast.makeText(getApplicationContext(), "Try loading new path",
                Toast.LENGTH_LONG).show();

        startActivity(new Intent(ReviewPathActivity.this,ReviewPathActivity.class));//Η activity καλεί τον εαυτ�? της για να μπο�?εί ο χ�?ήστης να κάνει ένα νέο review
        finish();
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(ReviewPathActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(ReviewPathActivity.this).reportActivityStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Δι�?γκωση (Inflate) του μενο�?. Π�?οσθέτει στοιχεία στην γ�?αμμή ενε�?γει�?ν (action bar) αν υπά�?χει.
        getMenuInflater().inflate(R.menu.review_path, menu);
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
                        .setCategory(categoryMakeReviewID)
                        .setAction(actionBackToRecord)
                        .build());

                finish();
                return true;
            case R.id.action_menu_review_path:

                Tracker t1 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t1.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryMakeReviewID)
                        .setAction(actionMenuChoiseID)
                        .build());

                return true;
            case R.id.submenu_show_maps:

                Tracker t2 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t2.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryMakeReviewID)
                        .setAction(actionShowMapsID)
                        .build());

                Intent mapsIntent= new Intent(ReviewPathActivity.this,MapsActivity.class);
                mapsIntent.putExtra("mapsID",2);
                startActivity(mapsIntent);
                finish();

                return true;

            case R.id.submenu_rank__list_of_players:

                Tracker t3 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t3.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryMakeReviewID)
                        .setAction(actionRankingID)
                        .build());

                Intent intent2= new Intent(ReviewPathActivity.this,RankListOfPlayersActivity.class);
                startActivity(intent2);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}

