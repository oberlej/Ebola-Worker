package com.example.ebolaworker.mainScreens;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

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
            //no search criteria => show all paptients
            Cursor c = mDBHelper.fetchAllFromTable(DatabaseHelper.TABLE_PATIENT);
            if (c != null && c.moveToFirst()) {
                showList(c);
            } else {
                Toast.makeText(getApplicationContext(), R.string.search_no_patient_msg, Toast.LENGTH_LONG).show();
            }
            mDBHelper.closeDB();
        } else {
            //search by criteria
            Cursor c = mDBHelper.fetchPatientsByCriterias(mFirstName.getText().toString(), mLastName.getText().toString(), "", "");
            if (c != null && c.moveToFirst()) {
                if (c.getCount() == 1) {
                    //only one patient so load directly
                    Intent intent = new Intent(this, TriageActivity.class);
                    intent.putExtra(getResources().getString(R.string.PATIENT_ID), c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.ID)));
                    startActivity(intent);
                } else {
                    showList(c);
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.search_no_patient_msg, Toast.LENGTH_LONG).show();
            }
            mDBHelper.closeDB();
        }

    }

    private void showList(Cursor c) {
        Intent intent = new Intent(this, PatientListActivity.class);
        int idIndex = c.getColumnIndexOrThrow(DatabaseHelper.ID);
        long[] ids = new long[c.getCount()];
        int i = 0;
        while (!c.isAfterLast()) {
            ids[i++] = c.getLong(idIndex);
            c.moveToNext();
        }
        intent.putExtra(getResources().getString(R.string.PATIENTS_LIST), ids);
        startActivity(intent);
    }
}
