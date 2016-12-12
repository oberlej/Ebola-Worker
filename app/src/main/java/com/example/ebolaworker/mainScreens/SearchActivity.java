package com.example.ebolaworker.mainScreens;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ebolaworker.helper.DatabaseHelper;
import com.example.ebolaworker.R;

public class SearchActivity extends AppCompatActivity {

    DatabaseHelper mDBHelper;

    EditText mLastName;
    EditText mFirstName;
    Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mDBHelper = DatabaseHelper.getInstance(getApplicationContext());


        //init fields
        mSearchButton = (Button) findViewById(R.id.search_btn);
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mSearchButton.setText(getResources().getString(R.string.search_patient));
                } else {
                    mSearchButton.setText(getResources().getString(R.string.show_all_patients));
                }
            }
        };
        mLastName = (EditText) findViewById(R.id.search_last_name);
        mLastName.addTextChangedListener(tw);
        mFirstName = (EditText) findViewById(R.id.search_first_name);
        mFirstName.addTextChangedListener(tw);
    }

    public void search(View view) {
        if (mSearchButton.getText().toString().equals(getResources().getString(R.string.show_all_patients))) {
            Cursor c = mDBHelper.fetchAllFromTable(DatabaseHelper.TABLE_PATIENT);
            if (c != null && c.moveToFirst()) {
                int nbP = c.getCount();
                if (nbP == 0) {
                    Toast.makeText(getApplicationContext(), R.string.search_no_patient_msg, Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(this, PatientListActivity.class);
                    int idIndex = c.getColumnIndexOrThrow(DatabaseHelper.ID);
                    long[] ids = new long[nbP];
                    int i = 0;
                    while (!c.isAfterLast()) {
                        ids[i++] = c.getLong(idIndex);
                        c.moveToNext();
                    }
                    intent.putExtra(getResources().getString(R.string.PATIENTS_LIST), ids);
                    startActivity(intent);
                }
            }
            mDBHelper.closeDB();
        } else

        {
            Cursor c = mDBHelper.fetchPatientsByCriterias(mFirstName.getText().toString(), mLastName.getText().toString());
            if (c != null && c.moveToFirst()) {
                int nbP = c.getCount();
                if (nbP == 0) {
                    Toast.makeText(getApplicationContext(), R.string.search_no_patient_msg, Toast.LENGTH_LONG).show();
                } else {
                    if (nbP == 1) {
                        Intent intent = new Intent(this, TriageActivity.class);
                        intent.putExtra(getResources().getString(R.string.PATIENT_ID), c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.ID)));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(this, PatientListActivity.class);
                        int idIndex = c.getColumnIndexOrThrow(DatabaseHelper.ID);
                        long[] ids = new long[nbP];
                        int i = 0;
                        while (!c.isAfterLast()) {
                            ids[i++] = c.getLong(idIndex);
                            c.moveToNext();
                        }
                        intent.putExtra(getResources().getString(R.string.PATIENTS_LIST), ids);
                        startActivity(intent);
                    }
                }
            }
            mDBHelper.closeDB();
        }
    }
}
