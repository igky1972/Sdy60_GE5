package com.sdy60ge5.teamge5_a.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

/************************************
 * Κλάση με βοηθητικές συναρτήσεις  *
 ************************************/
public class UserFunctions {
    private JSONParser jsonParser;

//    private static String loginURL = "http://corfu.pathsonmap.eu/request_log_reg_store_path.php";
//    private static String registerURL = "http://corfu.pathsonmap.eu/request_log_reg_store_path.php";
//    private static String storeReviewURL = "http://corfu.pathsonmap.eu/storeReview.php";
    
    private static String loginURL = "http://snf-818423.vm.okeanos.grnet.gr/teamge5_a/request_log_reg_store_path.php";
    private static String registerURL = "http://snf-818423.vm.okeanos.grnet.gr/teamge5_a/request_log_reg_store_path.php";
    private static String storeReviewURL = "http://snf-818423.vm.okeanos.grnet.gr/teamge5_a/storeReview.php";

    //Αν ο server έχει στηθεί τοπικά
    //private static String loginURL = "http://192.168.1.65/mapmaker_local/request_log_reg_store_path.php";
    //private static String registerURL = "http://192.168.1.65/mapmaker_local/request_log_reg_store_path.php";
    //private static String storeReviewURL = "http://192.168.1.65/mapmaker_local/storeReview.php";

    private static String login_tag = "login";//Το tag για την είσοδο
    private static String register_tag = "register";//Το tag για την εγγραφή
    private static String storeReview_tag = "storeReview"; //Το tag για την κριτική στο μονοπάτι

    // κατασκευαστής
    public UserFunctions(){
        jsonParser = new JSONParser();
    }

    /**
     * function make Login Request - Συνάρτηση που κάνει την αίτηση σύνδεσης
     * @param email
     * @param password
     * */
    public JSONObject loginUser(String email, String password){
        // Building Parameters - Οικοδομεί τις παραμέτρους
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }

    /**
     * function make Register Request - Συνάρτηση που κάνει την αίτηση εγγραφής
     * @param name
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String name, String email, String password){
        // Building Parameters - Οικοδομεί τις παραμέτρους
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));

        // getting JSON Object-παίρνει το JSON αντικείμενο
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        // γυρίζει json
        return json;
    }

    /**
     * Συνάρτηση που στέλνει το κατάλληλο αίτημα στον server όταν ο χρήστης σχολιάζει μια διαδρομή
     * και γυρίζει την απάντηση του
     * */
    public JSONObject reviewPath(int user_id, int path_id, int rated,int rated_tags){
        // Building Parameters - Οικοδομεί τις παραμέτρους
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", storeReview_tag));
        params.add(new BasicNameValuePair("player_id", Integer.toString(user_id)));
        params.add(new BasicNameValuePair("path_id", Integer.toString(path_id)));
        params.add(new BasicNameValuePair("rated", Integer.toString(rated)));
        params.add(new BasicNameValuePair("rated_tags", Integer.toString(rated_tags)));

        // getting JSON Object-παίρνει το JSON αντικείμενο
        JSONObject json = jsonParser.getJSONFromUrl(storeReviewURL, params);
        // γυρίζει json
        return json;
    }

    /**
     * Function get Login status - Συνάρτηση που κοιτάει αν ο χρήστης έχει συνδεθεί ή όχι
     * */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){//Εάν υπάρχουν εγγραφές σημαίνει ότι ο χρήστης είναι συνδεδεμένος
            // user logged in-ο χρήστης είναι συνδεδεμένος
            return true;
        }
        return false;
    }

    /**
     * Function to logout user - Συνάρτηση αποσύνδεσης του χρήστη
     * Reset Database - Μηδενισμός (επαναφορά) βάσης δεδομένων
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }

    /**
     * Συνάρτηση που γυρίζει το uid του χρήστη από την βάση δεδομένων της συσκευής
     * */
    public int getUserUid(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int uid = db.getUserUid();
        return uid;

    }

}

