package com.sdy60ge5.teamge5_a;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.ippokratis.mapmaker2.R;
import com.sdy60ge5.teamge5_a.R;
import com.sdy60ge5.teamge5_a.library.ConnectionDetector;
import com.sdy60ge5.teamge5_a.library.DatabaseHandler;
import com.sdy60ge5.teamge5_a.library.UserFunctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/****************************************************
 * Η Activity για την οθ�?νη εγγ�?αφής στην εφα�?μογή. *
 ****************************************************/
public class RegisterActivity extends Activity {

    //Τα Id για τα googleAnalytics events
    private static String categoryRegisterId = "RegisterActivity buttons";
    private static String actionRegisterId = "Register button";
    private static String actionLoginMeId = "Login me button";

    Button btnRegister;
    Button btnLinkToLogin;
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;

    // ον�?ματα κ�?μβου(node) JSON απ�?κ�?ισης
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";

    private static String KEY_CREATED_AT = "created_at";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.register);
        setContentView(R.layout.activity_register);

        //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
        t.setScreenName("Register screen");
        t.send(new HitBuilders.AppViewBuilder().build());

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // εισαγωγή κουμπι�?ν, πλαισίων, κτλ.
        inputFullName = (EditText) findViewById(R.id.registerName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);

        //κλικ συμβάν του κουμπιο�? εγγ�?αφής
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
                Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRegisterId)
                        .setAction(actionRegisterId)
                        .build());

                //Αν υπά�?χει σ�?νδεδη internet θα γίνει π�?οσπάθεια σ�?νδεσης - αλλι�?ς θα βγάλει ένα μήνυμα στον χ�?ήστη
                ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());

                if(mConnectionDetector.isNetworkConnected() == true &&  mConnectionDetector.isInternetAvailable()==true){

                    String name = inputFullName.getText().toString();
                    String email = inputEmail.getText().toString();
                    String password = inputPassword.getText().toString();
                    UserFunctions userFunction = new UserFunctions();
                    //�?αλεί την μέθοδο σ�?νδεσης που γυ�?ίζει ένα JSON αντικείμενο
                    JSONObject json = userFunction.registerUser(name, email, password);

                    // ελέγχει την απ�?κ�?ιση εγγ�?αφής
                    try {
                        if (json.getString(KEY_SUCCESS) != null) {
                            registerErrorMsg.setText("");
                            String res = json.getString(KEY_SUCCESS);
                            if(Integer.parseInt(res) == 1){
                                // �? χ�?ήστης εγγ�?άφηκε επιτυχ�?ς
                                //Αποθηκε�?ει τις πλη�?οφο�?ίες του χ�?ήστη στην SQLite Βάση
                                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                                JSONObject json_user = json.getJSONObject("player");

                                // �?αθα�?ίζει �?λα τα π�?οηγο�?μενα δεδομένα της βάσης και βάζει τα καινο�?�?για
                                userFunction.logoutUser(getApplicationContext());
                                db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getInt(KEY_UID),  json_user.getString(KEY_CREATED_AT));

                                // �?εκινά την κεντ�?ική οθ�?νη
                                Intent main = new Intent(getApplicationContext(), MainActivity.class);

                                // �?λείνει �?λες τις π�?οβολές- views- π�?ιν ξεκινήσει το Dashboard
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(main);

                                // �?λείνει την οθ�?νη εγγ�?αφής
                                finish();
                            }else if(json.getString(KEY_ERROR) != null){
                                registerErrorMsg.setText("");
                                String resError = json.getString(KEY_ERROR);
                                if(Integer.parseInt(resError) == 2 || Integer.parseInt(resError) == 1){
                                    // Λάθος κατά την σ�?νδεση
                                    registerErrorMsg.setText(json.getString(KEY_ERROR_MSG));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else
                {	//�?ήνυμα στον χ�?ήστη �?τι δεν υπά�?χει σ�?νδεση internet
                    Toast.makeText(getApplicationContext(), "You must be connected to the internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Σ�?νδεσμος για την οθ�?νη σ�?νδεσης
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                //Παί�?νει έναν tracker (κάνει αυτο-αναφο�?ά)
                Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(GoogleAnalyticsApp.TrackerName.APP_TRACKER);
                //Χτίζει και στέλνει το Analytics Event.
                t.send(new HitBuilders.EventBuilder()
                        .setCategory(categoryRegisterId)
                        .setAction(actionLoginMeId)
                        .build());

                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                // Σταματάει την τ�?έχουσα activity
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(RegisterActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(RegisterActivity.this).reportActivityStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Δι�?γκωση (Inflate) του μενο�?. Π�?οσθέτει στοιχεία στην γ�?αμμή ενε�?γει�?ν (action bar) αν υπά�?χει.
        getMenuInflater().inflate(R.menu.register, menu);
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
        return super.onOptionsItemSelected(item);
    }
}
