package com.example.ebolaworker.mainScreens;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.ebolaworker.R;
import com.example.ebolaworker.helper.DatabaseHelper;
import com.example.ebolaworker.helper.PatientArrayAdapter;
import com.example.ebolaworker.helper.SymptomArrayAdapter;
import com.example.ebolaworker.model.BasicPatient;

import java.util.ArrayList;
import java.util.List;

public class PatientListActivity extends AppCompatActivity {
    DatabaseHelper mDBHelper;
    ListView mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        mDBHelper = DatabaseHelper.getInstance(getApplicationContext());
        mList = (ListView) findViewById(R.id.patient_list);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));


        Intent intent = getIntent();
        if (intent.hasExtra(getResources().getString(R.string.PATIENTS_LIST))) {
            long[] ids = intent.getLongArrayExtra(getResources().getString(R.string.PATIENTS_LIST));
            List<BasicPatient> list = new ArrayList<BasicPatient>();
            for (long id : ids) {
                BasicPatient p = mDBHelper.getBasicPatientInformationByPatientId(id);
                if (p != null) list.add(p);
            }
            if (!list.isEmpty()) {
                // Create the adapter to convert the array to views
                PatientArrayAdapter adapter = new PatientArrayAdapter(this, R.layout.row_patient, list);
                // Attach the adapter to a ListView
                mList.setAdapter(adapter);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Error loading the patients", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }
}

