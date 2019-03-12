package com.shin.ssr.layout.tab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.fit.samples.stepcounter.MainActivity;
import com.google.android.gms.fit.samples.stepcounter.R;

public class FinanceTab extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("log","in finance Tab");
        setContentView(R.layout.finance_tab_activity);
    }

    public void sendToFit(View view) {
        Intent intent = new Intent(FinanceTab.this, FitTab.class);
        startActivity(intent);
    }


    public void sendToPay(View view) {
        Intent intent = new Intent(FinanceTab.this, PaymentTab.class);
        startActivity(intent);
    }

    public void sendToLife(View view) {
        Intent intent = new Intent(FinanceTab.this, LifeTab.class);
        startActivity(intent);
    }

}
