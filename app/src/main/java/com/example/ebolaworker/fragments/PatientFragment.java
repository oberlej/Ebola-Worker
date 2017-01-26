package com.example.ebolaworker.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.ebolaworker.helper.DatabaseHelper;
import com.example.ebolaworker.R;
import com.example.ebolaworker.mainScreens.TriageActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class PatientFragment extends Fragment implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 14123;
    DatabaseHelper mDBHelper;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_BARCODE_SCANE = 2;

    //activity fields
    ImageView mImageView;
    EditText mOCI;
    EditText mETCI;

    EditText mTriageDate;
    EditText mLastName;
    EditText mFirstName;
    EditText mBirthDate;
    EditText mAge;
    EditText mHousehold;
    EditText mAddress;
    EditText mRegion;
    EditText mCity;
    EditText mCountry;
    EditText mPhone;
    CheckBox mCBSameAddress;
    EditText mIllnessAddress;
    EditText mIllnessRegion;
    EditText mIllnessCity;
    EditText mIllnessCountry;
    EditText mHealthcarePosition;
    EditText mHealthcareFacility;
    EditText mOtherOccupatino;
    EditText mComment;
    RadioButton mRBMale;
    RadioButton mRBFemale;


    static Calendar mCalendar;
    DatePickerFragment dateFragment;
    static final String DATE_FORMAT = "dd/MM/yyyy";
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    private View mView;

    public PatientFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_patient, container, false);
            initFields();
            updateFields();
        }
        requestFocus(mLastName);
        return mView;
    }

    public void updateFields() {
        //check if we want to display a patient or show empty patient form
        long patientId = ((TriageActivity) getActivity()).getPatientId();
        if (patientId != -1) {
            //populate form
            Cursor c = mDBHelper.fetchByIdFromTable(patientId, DatabaseHelper.TABLE_PATIENT);
            if (c != null && c.moveToFirst()) {
                mLastName.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.LAST_NAME)));
                mFirstName.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.FIRST_NAME)));

                String dateForButton = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.BIRTH_DATE));
                mBirthDate.setText(dateForButton);
                try {
                    mCalendar.setTime(dateFormat.parse(dateForButton));
                } catch (ParseException e) {
                    mCalendar.set(Calendar.YEAR, 1965);
                    mCalendar.set(Calendar.MONTH, 0);
                    mCalendar.set(Calendar.DAY_OF_MONTH, 1);
                }
                String age = getAge(mCalendar);
                mBirthDate.setText(dateForButton);
                mAge.setText(age);

                mRBMale.setChecked(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.GENDER)) == DatabaseHelper.Genders.MALE.ordinal());
                mRBFemale.setChecked(!mRBMale.isChecked());
                mHousehold.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.HOUSEHOLD)));
                mAddress.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.ADDRESS)));
                mRegion.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.REGION)));
                mCity.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.CITY)));
                mCountry.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COUNTRY)));
                mPhone.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.PHONE)));

                if (c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.SAME_ADDRESS_ILLNESS)) == 0) {
                    mCBSameAddress.setChecked(false);
                    mIllnessAddress.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.ADDRESS_ILLNESS)));
                    mIllnessRegion.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COUNTRY_ILLNESS)));
                    mIllnessCity.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.CITY_ILLNESS)));
                    mIllnessCountry.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.REGION_ILLNESS)));
                }

                mHealthcarePosition.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.HEALTHCARE_POSITION)));
                mHealthcareFacility.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.HEALTHCARE_FACILITY)));
                mOtherOccupatino.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.OTHER_OCCUPATION)));
                mComment.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COMMENT)));
                mBirthDate.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.BIRTH_DATE)));

                int gender = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COMMENT));
                switch (gender) {
                    case 1:
                        mRBMale.setChecked(true);
                        break;
                    case 2:
                        mRBFemale.setChecked(true);
                        break;
                }
            } else {
                Toast.makeText(getContext(), "There was a problem, please try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void initFields() {

        mDBHelper = DatabaseHelper.getInstance(getContext());
        mLastName = (EditText) mView.findViewById(R.id.patient_last_name);
        mFirstName = (EditText) mView.findViewById(R.id.first_name);
        mAge = (EditText) mView.findViewById(R.id.age);
        mHousehold = (EditText) mView.findViewById(R.id.household);
        mAddress = (EditText) mView.findViewById(R.id.address);
        mRegion = (EditText) mView.findViewById(R.id.region);
        mCity = (EditText) mView.findViewById(R.id.city);
        mCountry = (EditText) mView.findViewById(R.id.country);
        mPhone = (EditText) mView.findViewById(R.id.phone_number);
        mCBSameAddress = (CheckBox) mView.findViewById(R.id.cb_same_address);
        mIllnessAddress = (EditText) mView.findViewById(R.id.illness_address);
        mIllnessRegion = (EditText) mView.findViewById(R.id.illness_region);
        mIllnessCity = (EditText) mView.findViewById(R.id.illness_city);
        mIllnessCountry = (EditText) mView.findViewById(R.id.illness_country);
        mHealthcarePosition = (EditText) mView.findViewById(R.id.healthcare_position);
        mHealthcareFacility = (EditText) mView.findViewById(R.id.healthcare_facility);
        mOtherOccupatino = (EditText) mView.findViewById(R.id.other_occupation);
        mComment = (EditText) mView.findViewById(R.id.comment);
        mRBFemale = (RadioButton) mView.findViewById(R.id.patient_female_rb);
        mRBMale = (RadioButton) mView.findViewById(R.id.patient_male_rb);

        mImageView = (ImageView) mView.findViewById(R.id.patient_image);
        mImageView.setOnClickListener(this);
        mOCI = (EditText) mView.findViewById(R.id.outbreak_id);
        mETCI = (EditText) mView.findViewById(R.id.app_id);
        String id = "";
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            id += r.nextInt(10);
        }
        mCalendar = Calendar.getInstance();
        mTriageDate = (EditText) mView.findViewById(R.id.patient_triage_date);
        mTriageDate.setText(dateFormat.format(mCalendar.getTime()));
        mBirthDate = (EditText) mView.findViewById(R.id.birth_date);
        mETCI.setText(id);
//        set default date
        mCalendar.set(Calendar.YEAR, 1965);
        mCalendar.set(Calendar.MONTH, 0);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);


        mAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edt) {
                if (edt.length() == 1 && edt.toString().equals("0")) {
                    mAge.setText("");
                    Toast toast = Toast.makeText(getContext(), "Age needs to be over 0", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });
        mAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //field lost focus so user is done typing
                if (!hasFocus) {
                    try {
                        int age = Integer.parseInt(mAge.getText().toString());
                        if (age != 0 && age < 120) {
                            mCalendar = Calendar.getInstance();
                            mCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR) - age);
                            updateDateButtonText();
                        } else {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException nfe) {
                        Toast toast = Toast.makeText(getContext(), "Please enter a valid age", Toast.LENGTH_SHORT);
                        toast.show();
                        mAge.setText("60");
                    }

                }
            }
        });
        updateDateButtonText();

        mBirthDate.setOnClickListener(this);
        mCBSameAddress.setOnClickListener(this);
        mView.findViewById(R.id.button_barcode).setOnClickListener(this);
        mView.findViewById(R.id.cb_other_occupation).setOnClickListener(this);
        mView.findViewById(R.id.cb_healthcare_worker).setOnClickListener(this);
        mAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((ScrollView) mView.findViewById(R.id.patient_scrollview)).requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mIllnessAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((ScrollView) mView.findViewById(R.id.patient_scrollview)).requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((ScrollView) mView.findViewById(R.id.patient_scrollview)).requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(mFirstName, (TextInputLayout) mView.findViewById(R.id.patient_first_name_layout));
            }
        });
        mLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(mLastName, (TextInputLayout) mView.findViewById(R.id.patient_last_name_layout));
            }
        });

    }

    public void dispatchTakePictureIntent() {
        //Check for camera permission
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            takePicture();
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        } else if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    mOCI.setText(result.getContents());
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                Toast.makeText(getContext(), "Problem number x2342", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void updateDateButtonText() {
        String dateForButton = dateFormat.format(mCalendar.getTime());
        mBirthDate.setText(dateForButton);
        String age = getAge(mCalendar);
        mAge.setText(age);
    }

    private void showDatePickerDialog() {
        dateFragment = new DatePickerFragment();
        dateFragment.show(getFragmentManager(), "datePicker");
    }

    String getAge(Calendar dob) {
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    public void onCbSameAddress(View view) {
        CheckBox cb = ((CheckBox) view);
        if (cb.isChecked()) {
            getView().findViewById(R.id.wrapper_location_illness).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.wrapper_location_illness).setVisibility(View.VISIBLE);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
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
            ((TriageActivity) getActivity()).getPatientFragment().updateDateButtonText();
        }

    }

    /**
     * onClick handler for the checkboxes of the patient's occupation
     *
     * @param view
     */
    public void onCbOccupation(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.cb_healthcare_worker:
                if (checked) {
                    getView().findViewById(R.id.wrapper_healthcare).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.wrapper_other_occupation).setVisibility(View.GONE);

                    ((CheckBox) getView().findViewById(R.id.cb_other_occupation)).setChecked(false);
                } else {
                    getView().findViewById(R.id.wrapper_healthcare).setVisibility(View.GONE);
                }
                break;
            case R.id.cb_other_occupation:
                if (checked) {
                    getView().findViewById(R.id.wrapper_other_occupation).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.wrapper_healthcare).setVisibility(View.GONE);

                    ((CheckBox) getView().findViewById(R.id.cb_healthcare_worker)).setChecked(false);
                } else {
                    getView().findViewById(R.id.wrapper_other_occupation).setVisibility(View.GONE);
                }
                break;
        }
    }

    public void barcodeScan() {
        IntentIntegrator scanner = new IntentIntegrator(getActivity());
        scanner.setOrientationLocked(false);

        Intent intent = scanner.createScanIntent();
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra("RESULT_DISPLAY_DURATION_MS", 0L);
        startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
    }

    private boolean validateEditText(EditText t, TextInputLayout l) {
        boolean b = t.getText().toString() != null && !t.getText().toString().trim().isEmpty();
        if (!b) {
            requestFocus(t);
            l.setError("This field is mandatory!");
        } else {
            l.setErrorEnabled(false);
        }

        return b;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public boolean validatePatient() {
        boolean b1 = validateEditText(mLastName, (TextInputLayout) mView.findViewById(R.id.patient_last_name_layout));
        boolean b2 = validateEditText(mFirstName, (TextInputLayout) mView.findViewById(R.id.patient_first_name_layout));
        return b1 && b2;
    }

    public long savePatient(long patientId) {
        //save patient
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.LAST_NAME, mLastName.getText().toString().trim());
        values.put(DatabaseHelper.FIRST_NAME, mFirstName.getText().toString().trim());
        values.put(DatabaseHelper.BIRTH_DATE, dateFormat.format(mCalendar.getTime()));
        values.put(DatabaseHelper.HOUSEHOLD, mHousehold.getText().toString().trim());
        values.put(DatabaseHelper.ADDRESS, mAddress.getText().toString().trim());
        values.put(DatabaseHelper.REGION, mRegion.getText().toString().trim());
        values.put(DatabaseHelper.CITY, mCity.getText().toString().trim());
        values.put(DatabaseHelper.COUNTRY, mCountry.getText().toString().trim());
        values.put(DatabaseHelper.PHONE, mPhone.getText().toString().trim());
        values.put(DatabaseHelper.SAME_ADDRESS_ILLNESS, mCBSameAddress.isChecked() ? 1 : 0);
        if (!mCBSameAddress.isChecked()) {
            values.put(DatabaseHelper.ADDRESS_ILLNESS, mIllnessAddress.getText().toString().trim());
            values.put(DatabaseHelper.REGION_ILLNESS, mIllnessRegion.getText().toString().trim());
            values.put(DatabaseHelper.CITY_ILLNESS, mIllnessCity.getText().toString().trim());
            values.put(DatabaseHelper.COUNTRY_ILLNESS, mIllnessCountry.getText().toString().trim());
        }
        values.put(DatabaseHelper.HEALTHCARE_POSITION, mHealthcarePosition.getText().toString().trim());
        values.put(DatabaseHelper.HEALTHCARE_FACILITY, mHealthcareFacility.getText().toString().trim());
        values.put(DatabaseHelper.OTHER_OCCUPATION, mOtherOccupatino.getText().toString().trim());
        values.put(DatabaseHelper.COMMENT, mComment.getText().toString().trim());
        values.put(DatabaseHelper.GENDER, mRBMale.isChecked() ? DatabaseHelper.Genders.MALE.ordinal() : DatabaseHelper.Genders.FEMALE.ordinal());
        values.put(DatabaseHelper.DATE_ILLNESS, "-1");

        if (patientId != -1) {
            if (mDBHelper.updateByIdForTable(values, patientId, DatabaseHelper.TABLE_PATIENT) < 1) {
                Toast.makeText(getContext(), "Error updating patient", Toast.LENGTH_LONG).show();
            }
        } else {
            patientId = mDBHelper.createForTable(values, DatabaseHelper.TABLE_PATIENT);
            if (patientId == -1) {
                Toast.makeText(getContext(), "Error inserting patient", Toast.LENGTH_LONG).show();
            }
        }

        return patientId;
    }

    public String getName() {
        return mLastName.getText().toString() + " " + mFirstName.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_barcode:
                barcodeScan();
                break;
            case R.id.patient_image:
                dispatchTakePictureIntent();
                break;
            case R.id.cb_same_address:
                onCbSameAddress(v);
                break;
            case R.id.birth_date:
                showDatePickerDialog();
                break;
            case R.id.cb_other_occupation:
            case R.id.cb_healthcare_worker:
                onCbOccupation(v);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                }
                return;
            }
        }
    }
}
