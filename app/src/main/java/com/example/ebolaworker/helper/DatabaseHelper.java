package com.example.ebolaworker.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ebolaworker.R;
import com.example.ebolaworker.model.BasicPatient;
import com.example.ebolaworker.model.Symptom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremiaoberle on 11/9/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    static DatabaseHelper sInstance;

    static final String LOG = DatabaseHelper.class.getName();
    //database information
    static final String DATABASE_NAME = "local_patients";
    static final int DATABASE_VERSION = 1;

    //Table names
    public static final String TABLE_PATIENT = "patient";
    public static final String TABLE_SYMPTOM = "symptom";

    public static final String ID = "id";
    //Patient table
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";

    public enum Genders {MALE, FEMALE}
    public static final String TRIAGE_DATE = "triage_date";
    public static final String GENDER = "gender";
    public static final String BIRTH_DATE = "birth_date";

    public static final String HOUSEHOLD = "household";
    public static final String ADDRESS = "address";
    public static final String REGION = "region";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String PHONE = "phone";
    public static final String SAME_ADDRESS_ILLNESS = "same_address_illness";
    public static final String ADDRESS_ILLNESS = "address_illness";
    public static final String REGION_ILLNESS = "region_illness";
    public static final String CITY_ILLNESS = "city_illness";
    public static final String COUNTRY_ILLNESS = "country_illness";

    public static final String HEALTHCARE_POSITION = "healthcare_position";
    public static final String HEALTHCARE_FACILITY = "healthcare_facility";
    public static final String OTHER_OCCUPATION = "other_occupation";

    public static final String COMMENT = "comment";

    public static final String DATE_ILLNESS = "date_illness";

//    public static final String[] PATIENT_FIELDS = {"first_name", "last_name", "gender", "birth_date", "household", "address", "region", "city", "country", "phone", "same_address_illness", "address_illness", "region_illness", "city_illness", "country_illness", "healthcare_position", "healthcare_facility", "other_occupation", "comment"};

//    String[] symptoms = {
//            "Fever",
//            "Vomiting",
//            "Diarrhea",
//            "Conjunctivitis",
//            "Intense fatigue/weakness",
//            "Anorexia",
//            "Abdominal pain",
//            "Muscle pain",
//            "Joint pain",
//            "Headache",
//            "Difficulty breathing",
//            "Difficulty swallowing",
//            "Skin rash",
//            "Hiccups",
//            "Unexplained bleeding"
//    };

    //Symptom table
    public static final String SYMPTOM_LABEL = "symptom_label";
    public static final String PATIENT_ID = "patient_id";

    public enum SymptomPresent {YES, NO, UNKNOWN}

    ;
    public static final String IS_PRESENT = "is_present";

    public static final String[] SYMPTOM_FIELDS = {"patient_id", "is_present", "symptom_label"};

    //Table Create Statements
    //Patient
    static final String CREATE_TABLE_PATIENT = "CREATE TABLE " + TABLE_PATIENT + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TRIAGE_DATE + " TEXT NOT NULL,"
            + FIRST_NAME + " TEXT NOT NULL,"
            + LAST_NAME + " TEXT NOT NULL,"
            + GENDER + " INTEGER,"
            + BIRTH_DATE + " TEXT NOT NULL,"
            + HOUSEHOLD + " TEXT,"
            + ADDRESS + " TEXT,"
            + REGION + " TEXT,"
            + CITY + " TEXT,"
            + COUNTRY + " TEXT,"
            + PHONE + " TEXT,"
            + SAME_ADDRESS_ILLNESS + " INTEGER,"
            + ADDRESS_ILLNESS + " TEXT,"
            + REGION_ILLNESS + " TEXT,"
            + CITY_ILLNESS + " TEXT,"
            + COUNTRY_ILLNESS + " TEXT,"
            + HEALTHCARE_POSITION + " TEXT,"
            + HEALTHCARE_FACILITY + " TEXT,"
            + OTHER_OCCUPATION + " TEXT,"
            + COMMENT + " TEXT,"
            + DATE_ILLNESS + " TEXT NOT NULL);";

    //Symptom
    static final String CREATE_TABLE_SYMPTOM = "CREATE TABLE " + TABLE_SYMPTOM + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PATIENT_ID + " INTEGER NOT NULL,"
            + SYMPTOM_LABEL + " TEXT NOT NULL,"
            + IS_PRESENT + " INTEGER NOT NULL);";


    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // creating required tables
            Log.e(LOG, CREATE_TABLE_PATIENT);
            db.execSQL(CREATE_TABLE_PATIENT);

            Log.e(LOG, CREATE_TABLE_SYMPTOM);
            db.execSQL(CREATE_TABLE_SYMPTOM);
        } catch (SQLException e) {
            Log.e(LOG, e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_SYMPTOM);

        // create new tables
        onCreate(db);
    }

    //generic methods

    public Cursor fetchByIdFromTable(long id, String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + table + " WHERE "
                + ID + " = '" + id + "';";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        return c;
    }

    public Cursor fetchAllFromTable(String table) {
        String selectQuery = "SELECT  * FROM " + table + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        return c;
    }

    public int updateByIdForTable(ContentValues values, long id, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(table, values, ID + " = ?", new String[]{String.valueOf(id)});
    }

    public long createForTable(ContentValues values, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e(LOG, "insert in " + table);

        long id = db.insert(table, null, values);
        return id;
    }

    // --- patient methods --- //

    public Cursor fetchPatientsByCriterias(String firstName, String lastName, String triageDate,String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true,TABLE_PATIENT,null,FIRST_NAME+" LIKE ? AND "+LAST_NAME+" LIKE ?",new String[]{"%"+firstName+"%","%"+lastName+"%"},null,null,null,"25");
        return c;
    }


    public BasicPatient getBasicPatientInformationByPatientId(long patientId) {
        Cursor c = fetchByIdFromTable(patientId,TABLE_PATIENT);
        BasicPatient p = null;
        if (c != null && c.moveToFirst()) {
            p = new BasicPatient(c.getLong(c.getColumnIndexOrThrow(ID)), c.getString(c.getColumnIndexOrThrow(LAST_NAME)), c.getString(c.getColumnIndexOrThrow(FIRST_NAME)), c.getString(c.getColumnIndexOrThrow(BIRTH_DATE)));
        }
        return p;
    }

    public Cursor fetchAllSymptomsByPatientId(long patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SYMPTOM + " WHERE "
                + PATIENT_ID + " = '" + patientId + "';";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        return c;
    }

    public List<Symptom> getAllSymptomsByPatientId(long patientId) {
        List<Symptom> list = new ArrayList<Symptom>();
        Cursor c = fetchAllSymptomsByPatientId(patientId);
        if (c != null && c.moveToFirst()) {
            int idIndex = c.getColumnIndexOrThrow(ID);
            int pIdIndex = c.getColumnIndexOrThrow(PATIENT_ID);
            int labelIndex = c.getColumnIndexOrThrow(SYMPTOM_LABEL);
            int ispIndex = c.getColumnIndexOrThrow(IS_PRESENT);
            while (!c.isAfterLast()) {
                list.add(new Symptom(c.getLong(idIndex), c.getLong(pIdIndex), c.getString(labelIndex), c.getInt(ispIndex)));
                c.moveToNext();
            }
        }
        c.close();
        return list;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}
