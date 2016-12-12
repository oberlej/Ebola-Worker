package com.example.ebolaworker.mainScreens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ebolaworker.R;
import com.example.ebolaworker.fragments.PatientFragment;
import com.example.ebolaworker.fragments.RiskScoreFragment;
import com.example.ebolaworker.fragments.SymptomsFragment;
import com.example.ebolaworker.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class TriageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    public PatientFragment getPatientFragment() {
        return mPatientFragment;
    }

    private PatientFragment mPatientFragment;

    public SymptomsFragment getSymptomFragment() {
        return mSymptomFragment;
    }

    private SymptomsFragment mSymptomFragment;

    public RiskScoreFragment getRiskScoreFragment() { return mRiskScoreFragment; }

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save:
                if (!mPatientFragment.validatePatient()) {
                    mSymptomFragment.validateSymptoms();
                    if (viewPager.getCurrentItem() != 0) viewPager.setCurrentItem(0);
                    return true;
                }
                if (!mSymptomFragment.validateSymptoms()) {
                    Toast.makeText(getApplicationContext(), "Please specify all symptoms!", Toast.LENGTH_SHORT).show();
                    if (viewPager.getCurrentItem() != 1) viewPager.setCurrentItem(1);
                    return true;
                }
                mPatientId = mPatientFragment.savePatient(mPatientId);
                mSymptomFragment.saveSymptoms(mPatientId);
                if (mIsUpdate) {
                    Toast.makeText(getApplicationContext(), "Patient " + mPatientFragment.getName() + " updated.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Patient " + mPatientFragment.getName() + " created.", Toast.LENGTH_LONG).show();
                }
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_cancel:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
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
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public long getPatientId() {
        return mPatientId;
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