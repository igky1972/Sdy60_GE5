package com.sdy60ge5.teamge5_a.library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/******************************************************************
 * Κλάση που γράφει τα GPX αρχεία (καλείται ασύγχρονα από το UI)  *
 * ****************************************************************/
public class AsynGPXWriter extends AsyncTask<Void, Long, Boolean>{

    private File mfile;//που θα αποθηκευτεί το αρχείο gpx
    private File msegmentfile;//το αρχείο που περιέχει το τμήμα με τα trackpoints
    private File msegmentOfWayPointsFile;//το αρχείο που περιέχει το τμήμα με τα waypoints

    private File mfileGoogle;//που θα αποθηκευτεί το αρχείο gpx που παράχθηκε από την google play service
    private File msegmentfileGoogle;//το αρχείο που περιέχει το τμήμα με τα trackpoints που παράχθηκε από την google play service
    private File msegmentOfWayPointsFileGoogle;//το αρχείο που περιέχει το τμήμα με τα waypoints που παράχθηκε από την google play service

    private String n ="Tracking by PathsOnMap";//Δηλώνει ποια εφαρμογή έχει δημιουργήσει το GPX αρχείο
    private Context mcontext;
    private ProgressDialog mDialog;

    public AsynGPXWriter(File file,File segmentfile,File segmentOfWayPointsFile,Context context,File fileGoogle,File segmentfileGoogle,File segmentOfWayPointsFileGoogle){

        mfile=file;
        msegmentfile=segmentfile;
        msegmentOfWayPointsFile=segmentOfWayPointsFile;

        mfileGoogle = fileGoogle;
        msegmentfileGoogle = segmentfileGoogle;
        msegmentOfWayPointsFileGoogle = segmentOfWayPointsFileGoogle;

        mcontext=context;
        mDialog= ProgressDialog.show(mcontext,"Please wait ...","Saving path...",true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {


        // Η κεφαλίδα του gpx αρχείου (και των δύο αρχείων)
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"PathsOnMap\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n";

        //Το όνομα του της ιχνηλάτησης και η αρχή των τμημάτων ιχνηλάτισης (και των δύο αρχείων)
        String name = "<trk>\n<name>" + n + "</name><trkseg>\n";

        //Το τέλος του gpx αρχείου (και των δύο αρχείων)
        String footer = "</trkseg></trk></gpx>";

        //Εδώ γράφεται το αρχείο
        try {
            FileWriter writer = new FileWriter(mfile, false);
            writer.append(header);

            if(msegmentOfWayPointsFile.exists()){//Εάν υπάρχει το αρχείο με τα WayPoints--Εάν ο χρήστης δεν έχει βάλει ποτέ του tags το αρχείο δεν θα υπάρχει
                //Στο string totalWpt θα υπάρχει το τμήμα με τα waypoints
                InputStream inputStream1 = new FileInputStream(msegmentOfWayPointsFile);
                BufferedReader r1 = new BufferedReader(new InputStreamReader(inputStream1));
                StringBuilder totalWpt = new StringBuilder();
                String line1;

                while ((line1 = r1.readLine()) != null) {
                    totalWpt.append(line1);
                }
                r1.close();

                writer.append(totalWpt);//Εδώ προστίθεται το τμήμα με τα waypoints
            }
            writer.append(name);

            //Στο string totalTrkp θα υπάρχει το τμήμα με τα trackpoints
            InputStream inputStream2 = new FileInputStream(msegmentfile);
            BufferedReader r2 = new BufferedReader(new InputStreamReader(inputStream2));
            StringBuilder totalTrkp = new StringBuilder();
            String line2;

            while ((line2 = r2.readLine()) != null) {
                totalTrkp.append(line2);
            }
            r2.close();

            writer.append(totalTrkp);//Εδώ προστίθεται το τμήμα με τα trackpoints
            writer.append(footer);
            writer.flush();
            writer.close();

        } catch (IOException e) {

            return false; //Δηλώνει ότι το αρχείο δεν γράφτηκε
        }

        //Εδώ γράφεται το αρχείο που δημιουργείται από την google play service
        try {
            FileWriter writer = new FileWriter(mfileGoogle, false);
            writer.append(header);

            if(msegmentOfWayPointsFileGoogle.exists()){//Εάν υπάρχει το αρχείο με τα WayPoints--Εάν ο χρήστης δεν έχει βάλει ποτέ του tags το αρχείο δεν θα υπάρχει
                //Στο string totalWpt θα υπάρχει το τμήμα με τα waypoints
                InputStream inputStream1 = new FileInputStream(msegmentOfWayPointsFileGoogle);
                BufferedReader r1 = new BufferedReader(new InputStreamReader(inputStream1));
                StringBuilder totalWpt = new StringBuilder();
                String line1;

                while ((line1 = r1.readLine()) != null) {
                    totalWpt.append(line1);
                }
                r1.close();

                writer.append(totalWpt);//Εδώ προστίθεται το τμήμα με τα waypoints
            }
            writer.append(name);

            //Στο string totalTrkp θα υπάρχει το τμήμα με τα trackpoints
            InputStream inputStream2 = new FileInputStream(msegmentfileGoogle);
            BufferedReader r2 = new BufferedReader(new InputStreamReader(inputStream2));
            StringBuilder totalTrkp = new StringBuilder();
            String line2;

            while ((line2 = r2.readLine()) != null) {
                totalTrkp.append(line2);
            }
            r2.close();

            writer.append(totalTrkp);//Εδώ προστίθεται το τμήμα με τα trackpoints
            writer.append(footer);
            writer.flush();
            writer.close();

        } catch (IOException e) {

            return false; //Δηλώνει ότι το αρχείο δεν γράφτηκε
        }


        return true;//Δηλώνει ότι τα αρχεία γράφτηκαν
    }

    @Override
    protected void onPostExecute(Boolean result) {

        mDialog.dismiss();

        if (result) {
            showToast("The path is saved in device");

        } else {
            showToast("Error Writting Path");
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mcontext, msg, Toast.LENGTH_LONG);
        error.show();
    }

}
