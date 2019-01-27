package com.nwhacks.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nwhacks.myapplication.Model.JSONToReceiptParser;
import com.nwhacks.myapplication.Model.Receipt;
import com.nwhacks.myapplication.Model.ReceiptManager;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ReceiptActivity extends AppCompatActivity {

    String jsonString;
    ReceiptManager receiptManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonString = extras.getString("EXTRA_JSON");
            System.out.println(jsonString);
            parseReceipts();
            TextView tv1 = (TextView) findViewById(R.id.textView1);
            TextView tv2 = (TextView) findViewById(R.id.textView2);
            TextView tv3 = (TextView) findViewById(R.id.textView3);
            Receipt r = receiptManager.getReceipt();
            System.out.println("transactionDate: " + r.getTransactionDate());
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            tv1.setText(df.format(r.getTransactionDate()));
            tv2.setText(r.getCompanyName());
            tv3.setText(Double.toString(r.getTotalCost()));
        }
    }

    public void sendToBunny(View view) {
        Intent intent = new Intent(ReceiptActivity.this, FullscreenActivity.class);
        if (receiptManager != null) {
            intent.putExtra("EXTRA_RECEIPT", receiptManager.getReceipt().getTotalCost());
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
