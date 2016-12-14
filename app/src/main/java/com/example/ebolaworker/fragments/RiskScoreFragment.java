package com.example.ebolaworker.fragments;

import android.database.Cursor;
import android.graphics.Paint;
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
    private TextView mEbolaScoreLabel;
    private Button mDeathScore;
    private TextView mDeathScoreLabel;

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
    private float mLabelSize = 0;
    private final String mLabel = "X";

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
        mEbolaScoreLabel = (TextView) mView.findViewById(R.id.risk_ebola_score_label);

        mDeathBarLow = (TextView) mView.findViewById(R.id.risk_bar_death_low);
        mDeathBarMed = (TextView) mView.findViewById(R.id.risk_bar_death_med);
        mDeathBarHigh = (TextView) mView.findViewById(R.id.risk_bar_death_high);
        mWrapperDeath = (LinearLayout) mView.findViewById(R.id.risk_death_wrapper);
        mDeathScore = (Button) mView.findViewById(R.id.risk_death_score);
        mDeathScoreLabel = (TextView) mView.findViewById(R.id.risk_death_score_label);
        mDeathPlaceholder = (TextView) mView.findViewById(R.id.risk_death_placeholder);

        mView.findViewById(R.id.risk_ebola_unk_rb).setOnClickListener(this);
        mView.findViewById(R.id.risk_ebola_no_rb).setOnClickListener(this);
        mView.findViewById(R.id.risk_ebola_yes_rb).setOnClickListener(this);
        mDBHelper = DatabaseHelper.getInstance(getContext());

        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(R.dimen.text_size));
        mLabelSize = paint.measureText(mLabel);
    }

    public void computeScore(long patientId) {

//        if (patientId != -1) {
//            List<Symptom> symptoms = mDBHelper.getAllSymptomsByPatientId(patientId);
//            Cursor patient = mDBHelper.fetchByIdFromTable(patientId, DatabaseHelper.TABLE_PATIENT);
//            int score = 0;
//            if (patient != null && patient.moveToFirst() && !symptoms.isEmpty()) {
//                String date = patient.getString(patient.getColumnIndexOrThrow(DatabaseHelper.DATE_ILLNESS));
//                try {
//                    Calendar dateIllness = Calendar.getInstance();
//                    dateIllness.setTime(dateFormat.parse(date));
//                    Calendar today = Calendar.getInstance();
//                    int diff = today.compareTo(dateIllness);
//                    if (diff > 4) {
//                        score += 3;
//                    }
//                } catch (ParseException e) {
//                }
//
//            }
//
//        }
        double logit = -3.326909;
        for (Symptom s : ((TriageActivity) getActivity()).getSymptomFragment().getSymptoms()) {
            if (!(s.is_present == DatabaseHelper.SymptomPresent.YES.ordinal())) continue;
            logit += getCoef(s.label);
        }
        double prob = Math.exp(logit) / (1 + Math.exp(logit)) * 100;

        //-mLabelSize/2 so that the Label (X) is centered on the percentage. Otherwise it starts at the percentage
        mEbolaScoreLabel.setPadding((int)(mEbolaScoreLabel.getWidth()*(prob/100) - mLabelSize/2),0,0,0);
        mEbolaScoreLabel.setText(mLabel);
        mEbolaScore.setText(String.valueOf(Math.round(prob * 100d) / 100d) + "%");
    }


    public double getCoef(String symp) {
        switch (symp) {
            case "Fever":
                return 0.5714839;
            case "Vomiting":
                break;
            case "Diarrhea":
                return 1.338666;
            case "Conjunctivitis":
                return 1.970277;
            case "Intense fatigue/weakness":
                break;
            case "Anorexia":
                break;
            case "Abdominal pain":
                break;
            case "Muscle pain":
                return -0.7896175;
            case "Joint pain":
                break;
            case "Headache":
                break;
            case "Difficulty breathing":
                return 0.8100781;
            case "Difficulty swallowing":
                break;
            case "Skin rash":
                break;
            case "Hiccups":
                break;
            case "Unexplained bleeding":
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
