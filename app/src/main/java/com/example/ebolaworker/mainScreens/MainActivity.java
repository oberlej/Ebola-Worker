package com.example.ebolaworker.mainScreens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.ebolaworker.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) findViewById(R.id.toolbar_title)).setText(R.string.app_name);
    }

    public void triage(View view) {
        Intent intent=new Intent(this, TriageActivity.class);
        startActivity(intent);
    }

    public void searchPatient(View view) {
        Intent intent=new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}
