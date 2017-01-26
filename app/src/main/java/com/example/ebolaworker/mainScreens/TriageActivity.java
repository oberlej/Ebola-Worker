package com.example.ebolaworker.mainScreens;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ebolaworker.R;
import com.example.ebolaworker.fragments.PatientFragment;
import com.example.ebolaworker.fragments.RiskScoreFragment;
import com.example.ebolaworker.fragments.SymptomsFragment;
import com.example.ebolaworker.helper.DatabaseHelper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class TriageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public PatientFragment getPatientFragment() {
        return mPatientFragment;
    }

    private PatientFragment mPatientFragment;

    public SymptomsFragment getSymptomFragment() {
        return mSymptomFragment;
    }

    private SymptomsFragment mSymptomFragment;

    public RiskScoreFragment getRiskScoreFragment() {
        return mRiskScoreFragment;
    }

    private RiskScoreFragment mRiskScoreFragment;

    DatabaseHelper mDBHelper;

    private long mPatientId = -1;
    private boolean mIsUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triage);
        mDBHelper = DatabaseHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent.hasExtra(getResources().getString(R.string.PATIENT_ID))) {
            mPatientId = intent.getLongExtra(getResources().getString(R.string.PATIENT_ID), -1);
            mIsUpdate = true;
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        adapter = ((ViewPagerAdapter) viewPager.getAdapter());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_action_bar, menu);
        return true;
    }

    public boolean savePatient() {
        if (!mPatientFragment.validatePatient()) {
            mSymptomFragment.validateSymptoms();
            if (viewPager.getCurrentItem() != 0) viewPager.setCurrentItem(0);
            return false;
        }
        if (!mSymptomFragment.validateSymptoms()) {
            Toast.makeText(getApplicationContext(), "Please specify all symptoms!", Toast.LENGTH_SHORT).show();
            if (viewPager.getCurrentItem() != 1) viewPager.setCurrentItem(1);
            return false;
        }
        mPatientId = mPatientFragment.savePatient(mPatientId);
        mSymptomFragment.saveSymptoms(mPatientId);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save:
                if (!savePatient()) return true;
                if (mIsUpdate) {
                    Toast.makeText(getApplicationContext(), "Patient " + mPatientFragment.getName() + " updated.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Patient " + mPatientFragment.getName() + " created.", Toast.LENGTH_LONG).show();
                }
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_cancel:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                String message = mIsUpdate ? "Changes discarded." : "Patient discarded.";
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                NavUtils.navigateUpFromSameTask(TriageActivity.this);
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(TriageActivity.this);
                String message = mIsUpdate ? "Discard changes ?" : "Discard patient ?";
                builder.setMessage(message)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        mPatientFragment = new PatientFragment();
        adapter.addFragment(mPatientFragment, "Patient Information");

        mSymptomFragment = new SymptomsFragment();
        adapter.addFragment(mSymptomFragment, "Symptoms");

        mRiskScoreFragment = new RiskScoreFragment();
        adapter.addFragment(mRiskScoreFragment, "Result");
//        viewPager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(v.equals(mRiskScoreFragment)){
//                    mSymptomFragment.validateSymptoms();
//                }
//
//            }
//        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(getApplicationContext());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE && viewPager.getCurrentItem() == 2) {
                    if (mSymptomFragment.validateSymptoms()) {
                        mRiskScoreFragment.computeScore(mPatientId);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please specify all symptoms to see the infection risk score!", Toast.LENGTH_SHORT).show();
                        if (viewPager.getCurrentItem() != 1) viewPager.setCurrentItem(1);
                    }
                }
            }
        });
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public long getPatientId() {
        return mPatientId;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Triage Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

//    public void setFragmentTitleColorRed(int index) {
//        TabLayout.Tab tab = tabLayout.getTabAt(index);
//        View v = tab.getCustomView().findViewById(R.id.title);
//
//    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}