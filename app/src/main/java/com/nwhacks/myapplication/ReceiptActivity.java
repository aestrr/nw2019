package com.nwhacks.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nwhacks.myapplication.Model.JSONToReceiptParser;
import com.nwhacks.myapplication.Model.Receipt;
import com.nwhacks.myapplication.Model.ReceiptManager;

import org.json.JSONObject;

public class ReceiptActivity extends AppCompatActivity {

    String jsonString;
    ReceiptManager receiptManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
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
            jsonString = extras.getString("EXTRA_JSON");
            System.out.println(jsonString);
        }

        parseReceipts();

    }

    public void sendToBunny(View view) {
        Intent intent = new Intent(ReceiptActivity.this, FullscreenActivity.class);
        if (receiptManager != null) {
            intent.putExtra("EXTRA_RECEIPT", "true");
        }
        startActivity(intent);
    }

    private void parseReceipts() {
        JSONObject jsonReceipt;
        try {
            jsonReceipt = new JSONObject(jsonString);
            Receipt r = JSONToReceiptParser.parseJson(jsonReceipt);
            receiptManager = ReceiptManager.getInstance();
            receiptManager.addReceipt(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
