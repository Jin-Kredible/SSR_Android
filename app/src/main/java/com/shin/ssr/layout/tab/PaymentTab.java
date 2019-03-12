package com.shin.ssr.layout.tab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.fit.samples.stepcounter.R;

public class PaymentTab extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("log","in payment Tab");
        setContentView(R.layout.payment_tab_activity);
    }

    public void sendToFit(View view) {
        Intent intent = new Intent(PaymentTab.this, FitTab.class);
        startActivity(intent);
    }

    public void sendToFinance(View view) {
        Intent intent = new Intent(PaymentTab.this, FinanceTab.class);
        startActivity(intent);
    }


    public void sendToLife(View view) {
        Intent intent = new Intent(PaymentTab.this, LifeTab.class);
        startActivity(intent);
    }


}
