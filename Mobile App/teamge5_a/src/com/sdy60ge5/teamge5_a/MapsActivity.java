package com.sdy60ge5.teamge5_a;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
// import com.ippokratis.mapmaker2.R;
import com.sdy60ge5.teamge5_a.R;
import com.sdy60ge5.teamge5_a.library.ConnectionDetector;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
//import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/************************************************************************
 * Η Activity για την οθ�?νη που εμφανίζει τον χάρτη πεζών του Ρεθυμνου *
 * **********************************************************************/
@SuppressLint("SetJavaScriptEnabled")
public class MapsActivity extends Activity {

//    private static String chaniaMapURL = "http://corfu.pathsonmap.eu/ChaniaMap.html";
	private static String rethimnoMapURL = "http://snf-818423.vm.okeanos.grnet.gr/teamge5_a/RethimnoMap.html";
//    private static String corfuMapURL = "http://corfu.pathsonmap.eu/CorfuMap.html";
    
    //Τα Id για τα googleAnalytics events
    private static String categoryMapsMenuID = "Maps Activity Menu";
    private static String actionMenuChoiseID = "Menu choise";
    private static String actionMakeReviewID="Make a review";
    private static String actionRankingID="Ranking";
    private static String actionBackToRecord = "Back To Record";

    private WebView browser;
    private String mapUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int mapsID = intent.getExtras().getInt("mapsID");
        if(mapsID==1){
        	setTitle(R.string.map_of_rethimno);
            //mapUrl = chaniaMapURL;
            mapUrl = rethimnoMapURL;
        }
        else if(mapsID==2){
        	// setTitle(R.string.map_of_corfu);
            // mapUrl = corfuMapURL;
        	setTitle(R.string.map_of_rethimno);
        	mapUrl = rethimnoMapURL;
        }

        setContentView(R.layout.activity_maps);

        //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
        t.setScreenName("Maps screen");
        t.send(new HitBuilders.AppViewBuilder().build());

        browser = (WebView)findViewById(R.id.wvMaps);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Αν υπά�?χει σ�?νδεδη internet θα δείξει τον χά�?τη - αλλι�?ς θα βγάλει ένα μήνυμα στον χ�?ήστη
        ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());

        if(mConnectionDetector.isNetworkConnected() == true &&  mConnectionDetector.isInternetAvailable()==true){

            setUpWebViewDefaults(browser);
            // Φο�?τ�?νει το website με τον χά�?τη πεζ�?ν
            // Αναγκάζει το link να ανακατευθυνθεί και να ανοίξει σε WebView αντί για τον browser
            browser.setWebViewClient(new WebViewClient(){

                public void onPageFinished(WebView view, String url) {
                    //Δείχνει ένα μήνυμα μ�?λις η σελίδα φο�?τ�?σει- Εδ�? δείχνει �?τι τα μονοπάτια φο�?τ�?νουν αφο�? η σελίδα είναι ασ�?γχ�?ονη
                    Toast.makeText(getApplicationContext(), "Τhe map is loading",
                            Toast.LENGTH_LONG).show();
                }});
            browser.loadUrl(mapUrl);

        }
        else {
            //�?ήνυμα στον χ�?ήστη �?τι δεν υπά�?χει σ�?νδεση internet
            Toast.makeText(getApplicationContext(), "You must be connected to the internet", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressLint("NewApi")
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps, menu);
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
                        .setCategory(categoryMapsMenuID)
                        .setAction(actionBackToRecord)
                        .build());

                finish();
                return true;

            case R.id.action_maps:

                Tracker t1 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t1.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryMapsMenuID)
                        .setAction(actionMenuChoiseID)
                        .build());

                return true;


            case R.id.submenu_review_paths:

                Tracker t3 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t3.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryMapsMenuID)
                        .setAction(actionMakeReviewID)
                        .build());

                Intent intent= new Intent(MapsActivity.this,ReviewPathActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.submenu_rank__list_of_players:

                Tracker t4 = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t4.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryMapsMenuID)
                        .setAction(actionRankingID)
                        .build());

                Intent intent2= new Intent(MapsActivity.this,RankListOfPlayersActivity.class);
                startActivity(intent2);
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}

