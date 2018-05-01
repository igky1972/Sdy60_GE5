/*
 * Copyright (c) 2011 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.sdy60ge5.teamge5_a.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

/* Εδώ ανεβαίνει (upload) ένα αρχείο μέσω ενός νήματος φόντου (background thread), χρησιμοποιώντας
 * τυπικό χειριμό εξαιρέσεων και ροής ελέγχουγια μια εφαρμογή που ανεβάζει έαν αρχείο στο Dropbox */
public class UploadFile extends AsyncTask<Void, Long, Boolean> {

    private DropboxAPI<?> mApi;
    private String mPath;
    private File mFile;

    private long mFileLen;
    private UploadRequest mRequest;
    private Context mContext;
    private final ProgressDialog mDialog;

    private String mErrorMsg;


    public UploadFile(Context context, DropboxAPI<?> api, String dropboxPath,
                      File file) {
        //Θέτουμε το πλαίσο (context) με αυτό τον τρόπο ώστε να μην υπάρξει κατά λάθος διαρροή δραστηριοτήτων (avtivities)
        mContext = context.getApplicationContext();

        mFileLen = file.length();
        mApi = api;
        mPath = dropboxPath;
        mFile = file;

        mDialog = new ProgressDialog(context);
        mDialog.setMax(100);
        mDialog.setMessage("Uploading " + file.getName());
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Αυτό θα ακυρώσει την λειτουργία putFile
                mRequest.abort();
            }
        });
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            // Με την δημιουργία ενός αιτήματος, θα πάρουμε ένα χειρισμό για την λειτουργία putFile
            // έτσι ώστε να αργότερα να μπορούμε να την ακυρώσουμε αν θέλουμε
            FileInputStream fis = new FileInputStream(mFile);
            String path = mPath + mFile.getName();
            mRequest = mApi.putFileOverwriteRequest(path, fis, mFile.length(),
                    new ProgressListener() {
                        @Override
                        public long progressInterval() {
                            // Ενημέρωση της γραμμης προόδου κάθε μισό δευτερόλεπτο
                            return 500;
                        }

                        @Override
                        public void onProgress(long bytes, long total) {
                            publishProgress(bytes);
                        }
                    });

            if (mRequest != null) {
                mRequest.upload();
                return true;
            }

        } catch (DropboxUnlinkedException e) {
            // Η σύνοδος αυτή δεν έχει επικυρωθεί σωστά ή ο χρήστης έχει αποσυνδεθεί
            mErrorMsg = "This app wasn't authenticated properly.";
        } catch (DropboxFileSizeException e) {
            // Το μέγεθος του αρχείου είναι πολύ μεγάλο για να ανέβει μέσω του API
            mErrorMsg = "This file is too big to upload";
        } catch (DropboxPartialFileException e) {
            // Ακυρώσαμε την λειτουργία
            mErrorMsg = "Upload canceled";
        } catch (DropboxServerException e) {
            // Εξαίρεση στην πλευρά του server. Αυτά είναι παραδείγματα του τι θα μπορούσε να συμβαίνει,
            // αλλά δεν κάνουμε τίποτα ιδιαίτερο με εδώ.
            if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Μη εξουσιοδοτημένη σύνδεση, οπότε θα πρέπει να αποσυνδεθεί.  
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Δεν επιτρέπεται η πρόσβαση
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // Η διαδρομή (path) δεν βρέθηκε. 
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // Ο χρήστης έχει υπερβεί το όριο
            } else {
                // Κάτι άλλο
            }
            // Παίρνουμε το σφάλμα του Dropbox, και το μεταφράζουμε στη γλώσσα του χρήστη
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Συμβαίνει όλη την ώρα, (θα μπορούσαμε να το κάνουμε και αυτόματα.
            mErrorMsg = "Network error.  Try again.";
        } catch (DropboxParseException e) {
            // Πιθανώς λόγω επανεκκίνησης του server του Dropbox, θα πρέπει να προσπαθήσετε ξανά
            mErrorMsg = "Dropbox error.  Try again.";
        } catch (DropboxException e) {
            // Άγνωστο σφάλμα
            mErrorMsg = "Unknown error.  Try again.";
        } catch (FileNotFoundException e) {
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
        int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
        mDialog.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mDialog.dismiss();
        if (result) {
            showToast("The file successfully uploaded");
        } else {
            showToast(mErrorMsg);
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }
}
