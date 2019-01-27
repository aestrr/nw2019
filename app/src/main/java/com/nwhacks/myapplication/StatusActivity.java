package com.nwhacks.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

        final EditText budgetText = findViewById(R.id.initial_budget);

        final Budget budget = new Budget(monthlyBudget);
        ProgressBar progressBar = findViewById(R.id.bar_progress);
        Double dProgress = (budget.getCurrentBudget() / monthlyBudget * 100.0);
        int iProgress = dProgress.intValue();
        progressBar.setProgress(iProgress);

        TextView textView1 = findViewById(R.id.text_1);
        Double dInitialBudget = budget.getInitialMonthlyBudget();
        String sInitialBudget = dInitialBudget.toString();
        textView1.setText(sInitialBudget);

        TextView textView2 = findViewById(R.id.text_2);
        Double dBudgetUsed = budget.getBudgetUsed();
        String sBudgetUsed = dBudgetUsed.toString();
        textView2.setText(sBudgetUsed);

        TextView textView3 = findViewById(R.id.text_3);
        Double dBudgetRemaining = budget.getCurrentBudget();
        String sBudgetRemaining = dBudgetRemaining.toString();
        textView3.setText(sBudgetRemaining);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatusActivity.this, InitialBudgetActivity.class));
                monthlyBudget = Log.v("EditText", budgetText.getText().toString());
            }
        });
    }
}
