package com.nwhacks.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nwhacks.myapplication.Model.Budget;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class StatusActivity extends AppCompatActivity {
    private double monthlyBudget;
    private Budget budget;
    private int currentBalance;
    private EditText editText;
    private String initialBudget;
    private int initialBudgetInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            TextView text1 = findViewById(R.id.text_1);
            text1.setText(value);

            TextView text3 = findViewById(R.id.text_3);
            text3.setText(value);

            ProgressBar bar = findViewById(R.id.bar_progress);
            bar.setProgress(100);
        }

//
//        Button button = findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }
}
