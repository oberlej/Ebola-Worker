package com.example.ebolaworker.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ebolaworker.R;
import com.example.ebolaworker.helper.DatabaseHelper;
import com.example.ebolaworker.helper.SymptomArrayAdapter;
import com.example.ebolaworker.mainScreens.TriageActivity;
import com.example.ebolaworker.model.Symptom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jeremiaoberle on 10/24/16.
 */
public class SymptomsFragment extends Fragment implements View.OnClickListener {
    Button mButtonSetAllUnknown;
    SymptomArrayAdapter adapter;

    ListView listView;

    EditText mDateField;
    static Calendar mCalendar;
    DatePickerFragment dateFragment;
    static final String DATE_FORMAT = "dd/MM/yyyy";
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    DatabaseHelper mDBHelper;

    private String[] symptoms = {
            "Fever",
            "Vomiting",
            "Diarrhea",
            "Conjunctivitis",
            "Intense fatigue/weakness",
            "Anorexia",
            "Abdominal pain",
            "Muscle pain",
            "Joint pain",
            "Headache",
            "Difficulty breathing",
            "Difficulty swallowing",
            "Skin rash",
            "Hiccups",
            "Unexplained bleeding"
    };

    public SymptomsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_sick:
                showDatePickerDialog();
                break;
            case R.id.btn_all_unk:
                setAllUnk();
                break;
        }
    }

    private void setAllUnk() {
        for (int i = 0; i < adapter.getCount(); i++) {
            View current = adapter.getView(i, null, null);
            RadioGroup rg = (RadioGroup) current.findViewById(R.id.symptom_row_rg);
            if (rg != null) {
                if (rg.getCheckedRadioButtonId() == -1) {
                    ((RadioButton) current.findViewById(R.id.symptom_radio_unk)).setChecked(true);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_symptoms, container, false);


        //footer for list view
        LinearLayout footer = new LinearLayout(getContext());
        footer.setOrientation(LinearLayout.HORIZONTAL);


        mDateField = (EditText) v.findViewById(R.id.date_sick);
        mDateField.setOnClickListener(this);

        mButtonSetAllUnknown = (Button) v.findViewById(R.id.btn_all_unk);
        mButtonSetAllUnknown.setOnClickListener(this);

        mCalendar = Calendar.getInstance();
        getSymptoms(v);
        updateDateButtonText();




        LinearLayout ll = (LinearLayout) v.findViewById(R.id.symptoms_footer_wrapper);
        ((ViewGroup) ll.getParent()).removeView(ll);
        footer.addView(ll);
        listView.addFooterView(footer);
        return v;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mDBHelper = DatabaseHelper.getInstance(getContext());
    }

    public void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateForButton = dateFormat.format(mCalendar.getTime());
        mDateField.setText(dateForButton);
    }

    public void getSymptoms(View v) {
        long patientId = ((TriageActivity) getActivity()).getPatientId();
        List<Symptom> list;

        if (patientId != -1) {
            list = mDBHelper.getAllSymptomsByPatientId(patientId);

            Cursor c = mDBHelper.fetchByIdFromTable(patientId, DatabaseHelper.TABLE_PATIENT);
            if (c != null && c.moveToFirst()) {
                String dateForButton = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.DATE_ILLNESS));
                mDateField.setText(dateForButton);
                try {
                    mCalendar.setTime(dateFormat.parse(dateForButton));
                } catch (ParseException e) {
                }
            }else{
            }
        } else {
            list = new ArrayList<Symptom>();
            for (String label : symptoms) {
                list.add(new Symptom(-1, -1, label, -1));
            }
        }

        if (!list.isEmpty()) {
            // Create the adapter to convert the array to views
            adapter = new SymptomArrayAdapter(getContext(), R.layout.row_symptom, list);
            // Attach the adapter to a ListView
            listView = (ListView) v.findViewById(R.id.symptoms_list);
            listView.setAdapter(adapter);
        } else {
            Toast toast = Toast.makeText(getContext(), "Error loading the symptoms of the patient", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void showDatePickerDialog() {
        dateFragment = new DatePickerFragment();
        dateFragment.show(getFragmentManager(), "datePicker");
    }


    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH);
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);
            ((TriageActivity) getActivity()).getSymptomFragment().updateDateButtonText();
        }

    }

    public boolean validateSymptoms() {
        boolean allSet = true;
        for (int i = 0; i < adapter.getCount(); i++) {
            SymptomArrayAdapter.ViewHolder tag = (SymptomArrayAdapter.ViewHolder) adapter.getView(i, null, null).getTag();
            RadioGroup rg = (RadioGroup) tag.getGroup();
            if (rg.getCheckedRadioButtonId() == -1) {
                allSet = false;
            }
        }

        return allSet;
    }

    public List<Symptom> getSymptoms(){
        return ((SymptomArrayAdapter) ((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter()).getData();
    }

    public void saveSymptoms(long patientId) {
        for (Symptom s : ((SymptomArrayAdapter) ((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter()).getData()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.SYMPTOM_LABEL, s.label);
            values.put(DatabaseHelper.IS_PRESENT, s.is_present);
            values.put(DatabaseHelper.PATIENT_ID, patientId);
            if (s.id == -1) {
                if (mDBHelper.createForTable(values, DatabaseHelper.TABLE_SYMPTOM) == -1) {
                    Toast.makeText(getContext(), "There was a problem updating the symptom " + s.label, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mDBHelper.updateByIdForTable(values, s.id, DatabaseHelper.TABLE_SYMPTOM) != 1) {
                    Toast.makeText(getContext(), "There was a problem updating the symptom " + s.label, Toast.LENGTH_SHORT).show();
                }
            }
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DATE_ILLNESS, dateFormat.format(mCalendar.getTime()));
        mDBHelper.updateByIdForTable(values, ((TriageActivity) getActivity()).getPatientId(), DatabaseHelper.TABLE_PATIENT);

    }
}
