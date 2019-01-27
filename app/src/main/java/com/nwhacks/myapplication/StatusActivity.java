package com.nwhacks.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import Model.Budget;

public class StatusActivity extends AppCompatActivity {
    private double monthlyBudget;

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

        Budget budget = new Budget(monthlyBudget);
        ProgressBar progressBar = findViewById(R.id.bar_progress);
        Double dProgress = (budget.getCurrentBudget() / monthlyBudget * 100.0);
        int iProgress = dProgress.intValue();
        progressBar.setProgress(iProgress);
    }
}
