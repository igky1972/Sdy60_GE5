package com.sdy60ge5.teamge5_a.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Κλάση που διαβάζει την απάντηση του server και την αναλύει σε ένα αντικείμενο JSON
 * **/
public class JSONParser {

    static InputStream is = null;//Διαβάζει δεδομένα από το δίκτυο
    static JSONObject jObj = null;//Ένα json αντικείμενο
    static String json = "";//Στην αρχή το json string είναι κενό. Μετά θα πάρει την απάντηση του server

    // κατασκευαστής (constructor)
    public JSONParser() {

    }

    //Η συνάρτηση γυρίζει τo json αντικείμενο της απάντησης του server. H NameValuePair είναι μια απλή κλάση που ενθυλακώνει ένα ζευγάρι χαρακτηριστικού/τιμής
    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

        // Making HTTP request-Εδώ γίνεται το αίτημα HTTP
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();//Προεπιλεγμένη υλοποίηση ενός πελάτη HTTP
            HttpPost httpPost = new HttpPost(url);//Η μέθοδος HTTP POST

            //UrlEncodedFormEntity είναι μια οντότητα που αποτελείται από μια λίστα url-κωδικοποιημένα ζεύγη. Η setEntity χειρίζεται την οντότητα στην αίτηση
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(params,"UTF-8");
            form.setContentEncoding(HTTP.UTF_8);
            httpPost.setEntity(form);//Το utf-8 το βάλαμε ώστε να υποστηρίζει ελληνικά

            HttpResponse httpResponse = httpClient.execute(httpPost);//Εκτελεί την αίτηση και γυρίζει την απάντηση
            HttpEntity httpEntity = httpResponse.getEntity();//Λαμβάνει το μήνυμα οντότητας αυτής της απάντησης, αν υπάρχει
            is = httpEntity.getContent();//Δημιουργεί ένα νέο InputStream αντικείμενο της οντότητας.

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Καλύπτει(wrap) έναν υπάρχον reader και κάνει buffer την είσοδο
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);//InputStreamReader είναι μια κλάση που μετατρέπει ένα ρεύμα bytes σε ένα ρεύμα χαρακτήρων. Εδώ χρησιμοποιείται η κωδικοποίηση utf-8
            StringBuilder sb = new StringBuilder();//Μία τροποποιήσιμη ακολουθία χαρακτήρων για χρήση στη δημιουργία αλφαριθμητικών
            String line = null;
            while ((line = reader.readLine()) != null) {//Διαβάζει μια γραμμή κειμένου
                sb.append(line + "\n");//Προσθέτει την συμβολοσειρά
            }
            is.close();
            json = sb.toString();//Επιστρέφει τα περιεχόμενα αυτού του builder (του sb εδώ)
            Log.e("JSON", json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // Προσπαθεί να αναλύσει την συμβολοσειρά σε ένα αντκείμνο JSON
        try {
            jObj = new JSONObject(json);//Δημιουργεί ένα νέο JSONObject με τις αντιστοιχίσεις όνοματος / τιμής από τη συμβολοσειρά JSON.
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // Γυρίζει το JSON αντικείμενο
        return jObj;

    }
}
