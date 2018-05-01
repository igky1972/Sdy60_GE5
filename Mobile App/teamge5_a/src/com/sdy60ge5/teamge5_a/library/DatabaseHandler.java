package com.sdy60ge5.teamge5_a.library;

import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**********************************************************************************************************************
 * Κλάση για την δημιουργία και την διαχείριση της βάσης δεδομένων στη συσκευή που έχει τις πληροφορίες του χρήστη που*
 * είναι συνδεδεμένος.                                                                                                *
 * ********************************************************************************************************************/
public class DatabaseHandler extends SQLiteOpenHelper{
    // All Static variables
    // Database Version-Έκδοση βάσης δεδομένων
    private static final int DATABASE_VERSION = 1;

    // Database Name - Όνομα βάσης δεδομέων
    private static final String DATABASE_NAME = "android_api";

    // Login table name - Όνομα πίνακα: login
    private static final String TABLE_LOGIN = "login";

    // Login Table Columns names - Ονόματα στηλών του πίνακα login
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables - Δημιουργία πίνακα
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_UID + " INTEGER,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    // Upgrading database - αναβάθμιση βάσης δεδομένων
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Απόθεση μεγαλύτερου πίνακα, εφόσον υπήρχε
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    /**
     * Αποθήκευση των στοιχείων των χρηστών στη βάση δεδομένων
     * */
    public void addUser(String name, String email, int uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();//Η κλάση αυτή χρησιμοποιείται για να αποθηκεύσει ένα σύνολο τιμών που ο ContentResolver μπορεί να επεξεργαστεί.
        values.put(KEY_NAME, name); // 'Ονομα
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // uid

        values.put(KEY_CREATED_AT, created_at); // Δημιουργήθηκε στις

        // Εισαγωγή Σειράς
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Κλείσιμο σύνδεσης βάσης δεδομένων
    }

    /**
     *Παίρνει τα δεδομένα των χρηστών από τη βάση δεδομένων (γυρίζουν όλα σαν string)
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//Γυρίζει έναν κέρσορα που δείχνει πριν την πρώτη εγγραφή
        // Μετακινεί τον κέρσορα στην πρώτη γραμμή.
        cursor.moveToFirst();
        if(cursor.getCount() > 0){//Η getCount επιστρέφει τον αριθμό των γραμμών του κέρσορα.
            user.put("name", cursor.getString(1));//Η getString επιστρέφει την τιμή της ζητούμενης στήλης ως String, έχει βάση το 0.
            user.put("email", cursor.getString(2));
            user.put("uid", String.valueOf(cursor.getInt(3)));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();

        return user;
    }

    public int getUserUid(){
        int uid;
        uid = 0;
        String selectQuery = "SELECT " + KEY_UID + " FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//Γυρίζει έναν κέρσορα που δείχνει πριν την πρώτη εγγραφή
        // Μετακινεί τον κέρσορα στην πρώτη γραμμή.
        cursor.moveToFirst();
        if(cursor.getCount() > 0){//Η getCount επιστρέφει τον αριθμό των γραμμών του κέρσορα.
            uid = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return uid;
    }



    /**
     * Παίρνει την κατάσταση σύνδεσης(login) του χρήστη
     * γυρίζει τον αριθμό των γραμμών του πίνακα (αν είναι 1 υπάρχει σύνδεση, ενώ αν είναι 0 δεν υπάρχει σύνδεση)
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        return rowCount;
    }

    /**
     * Επαναδημιουργία βάσης δεδομένων
     * Διαφράφει τους πίνακες και τους ξαναδημιουργεί
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Διαγράφει όλες τις γραμμές
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }
}
