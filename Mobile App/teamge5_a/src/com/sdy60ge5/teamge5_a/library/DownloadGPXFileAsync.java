package com.sdy60ge5.teamge5_a.library;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;

/***********************************************************************************************************
 * Κλάση για να κατεβάσουμε όλα το αρχείο gpx που περιέχει όλες τις διαδρομές των χρηστών από τον server.  *
 * Επίσης, μόλις κατέβει το αρχείο καλεί την ParsingGPXForDrawing για να σχεδιάσει τις διαδρομές στον χάρτη*
 * *********************************************************************************************************/
public class DownloadGPXFileAsync extends AsyncTask<String, String, String>{

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private final ProgressDialog mProgressDialog;
    private Context mContext;
    private File mFile;
    private GoogleMap mGoogleMap;


    public DownloadGPXFileAsync(Context context,GoogleMap GoogleMap){

        //mFile = File;
        mGoogleMap = GoogleMap;
        mContext = context.getApplicationContext();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMax(100);
        mProgressDialog.setMessage("Downloading all paths..");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgress(0);
        mProgressDialog.show();
    }
	


    @Override
    protected String doInBackground(String... aurl) {
        int count;

        try {

            URL url = new URL(aurl[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();

            int lenghtOfFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());

            //Το όνομα του αρχείου που θα αποθηκευτεί στην συσκευή και θα περιέχει όλες τις διαδρομές των χρηστών
            String myNewFileName = "merge.gpx";

            mFile = new File (mContext.getFilesDir(), myNewFileName);

            OutputStream output = new FileOutputStream(mFile);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {}
        return null;
    }

    protected void onProgressUpdate(String... progress) {
        Log.d("ANDRO_ASYNC",progress[0]);
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String unused) {
        mProgressDialog.dismiss();
        //dismissDialog(DIALOG_DOWNLOAD_PROGRESS);

        ParsingGPXForDrawing parsingForDrawing = new ParsingGPXForDrawing(mFile,mGoogleMap);

        parsingForDrawing.decodeGPXForTrksegs();

        parsingForDrawing.decodeGpxForWpts();
    }



}
