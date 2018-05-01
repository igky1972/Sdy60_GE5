package com.sdy60ge5.teamge5_a.library;

import java.net.InetAddress;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*********************************************************************************************************
 * Κλάση με τρεις μεθόδους που εντοπίζουν αν υπάρχει σύνδεση δικτύου, internet ή υπάρχει δυνατότητα data *
 * μέσω παρόχου κινητής τηλεφωνίας στην συσκευή.                                                         *
 * *******************************************************************************************************/
public class ConnectionDetector {
    private Context mContext;

    public ConnectionDetector(Context context){
        this.mContext = context;
    }
    //Γυρίζει αν υπάρχει σύνδεση δικτύου
    public boolean isNetworkConnected(){

        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() == true)
        {
            return true;
        } else   return false;

    }

    //Γυρίζει αν υπάρχει internet - Για να το κάνει αυτό κάνει ping στην διεύθυνση snf-818423.vm.okeanos.grnet.gr (αφού αυτή η διεύθυνση μας ενδιαφέρει)
    public boolean isInternetAvailable() {
        try {
//            InetAddress ipAddr = InetAddress.getByName("pathsonmap.eu");
        	InetAddress ipAddr = InetAddress.getByName("snf-818423.vm.okeanos.grnet.gr");
        	
        	
            if (ipAddr.equals("")) {            
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }

    //Γυρίζει αν η συσκευή έχει mobile data δυνατότητα
    public boolean hasMobileDatacapability(){

        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(ni == null)
        {
            // Device does not have mobile data capability
            return false;
        } else  return true;

    }

}
