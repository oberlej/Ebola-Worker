package com.example.ebolaworker.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ebolaworker.R;
import com.example.ebolaworker.helper.DatabaseHelper;
import com.example.ebolaworker.mainScreens.TriageActivity;
import com.example.ebolaworker.model.Symptom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RiskScoreFragment extends Fragment implements View.OnClickListener {

    private Button mEbolaScore;
    private Button mDeathScore;

    private TextView mEbolaBarLow;
    private TextView mEbolaBarMed;
    private TextView mEbolaBarHigh;

    private TextView mDeathBarLow;
    private TextView mDeathBarMed;
    private TextView mDeathBarHigh;

    private LinearLayout mWrapperEbola;
    private LinearLayout mWrapperDeath;
    private TextView mDeathPlaceholder;

    static final String DATE_FORMAT = "dd/MM/yyyy";
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    private DatabaseHelper mDBHelper;


    private View mView = null;

    public RiskScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_risk_score, container, false);
            initFields();
            computeScore();
        }


        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initFields() {
        mEbolaBarLow = (TextView) mView.findViewById(R.id.risk_bar_ebola_low);
        mEbolaBarMed = (TextView) mView.findViewById(R.id.risk_bar_ebola_med);
        mEbolaBarHigh = (TextView) mView.findViewById(R.id.risk_bar_ebola_high);
        mWrapperEbola = (LinearLayout) mView.findViewById(R.id.risk_ebola_wrapper);
        mEbolaScore = (Button) mView.findViewById(R.id.risk_ebola_score);

        mDeathBarLow = (TextView) mView.findViewById(R.id.risk_bar_death_low);
        mDeathBarMed = (TextView) mView.findViewById(R.id.risk_bar_death_med);
        mDeathBarHigh = (TextView) mView.findViewById(R.id.risk_bar_death_high);
        mWrapperDeath = (LinearLayout) mView.findViewById(R.id.risk_death_wrapper);
        mDeathScore = (Button) mView.findViewById(R.id.risk_ebola_score);
        mDeathPlaceholder = (TextView) mView.findViewById(R.id.risk_death_placeholder);

        mView.findViewById(R.id.risk_ebola_unk_rb).setOnClickListener(this);
        mView.findViewById(R.id.risk_ebola_no_rb).setOnClickListener(this);
        mView.findViewById(R.id.risk_ebola_yes_rb).setOnClickListener(this);
        mDBHelper = DatabaseHelper.getInstance(getContext());
    }

    private void computeScore() {
        long patientId = ((TriageActivity) getActivity()).getPatientId();

        if (patientId != -1) {
            List<Symptom> symptoms = mDBHelper.getAllSymptomsByPatientId(patientId);
            Cursor patient = mDBHelper.fetchByIdFromTable(patientId, DatabaseHelper.TABLE_PATIENT);
            int score = 0;
            if (patient != null && patient.moveToFirst() && !symptoms.isEmpty()) {
                String date = patient.getString(patient.getColumnIndexOrThrow(DatabaseHelper.DATE_ILLNESS));
                try {
                    Calendar dateIllness = Calendar.getInstance();
                    dateIllness.setTime(dateFormat.parse(date));
                    Calendar today = Calendar.getInstance();
                    int diff = today.compareTo(dateIllness);
                    if (diff > 4) {
                        score += 3;
                    }
                } catch (ParseException e) {
                }

                for (Symptom s : symptoms) {
                    if (!(s.is_present == DatabaseHelper.SymptomPresent.YES.ordinal())) continue;
                    score += getCoef(s.label);
                }

                if (score <= 1) mEbolaBarLow.setText('X');
                else if (score >= 12) mEbolaBarHigh.setText('X');
                else mEbolaBarMed.setText('X');
            }

        }

    }


    public double getCoef(String symp) {
        switch (symp) {
            case "Conjunctivities":
                break;
            case "Diarrhoea":
                break;
            case "Haemorrhage":
            case "Dysphagia":
                break;
            case "Myalgia":
                break;
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.risk_ebola_yes_rb:
                mDeathPlaceholder.setVisibility(View.GONE);
                mWrapperDeath.setVisibility(View.VISIBLE);
                break;
            case R.id.risk_ebola_no_rb:
                mDeathPlaceholder.setVisibility(View.VISIBLE);
                mWrapperDeath.setVisibility(View.GONE);
                mDeathPlaceholder.setText(R.string.risk_death_no_ebola);
                break;
            case R.id.risk_ebola_unk_rb:
                mDeathPlaceholder.setVisibility(View.VISIBLE);
                mWrapperDeath.setVisibility(View.GONE);
                mDeathPlaceholder.setText(R.string.risk_death_input_results);
                break;
        }
    }
}
