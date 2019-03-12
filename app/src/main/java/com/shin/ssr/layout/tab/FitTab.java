package com.shin.ssr.layout.tab;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.stepcounter.R;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.util.Timer;


public class FitTab extends AppCompatActivity {

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private Timer mTimer = new Timer();
    private Handler mHandler = new Handler();
  /*  private MainTimerTask timerTask = new MainTimerTask();*/




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fit_tab_activity);



        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
            android.util.Log.d("log","in Fitness regist");
        } else {
            android.util.Log.d("log","in Fitness regist");
            subscribe();

        }
        /*mTimer.schedule(timerTask, 5000, 10000);*/
        android.util.Log.d("log","in fit Tab");

        String steps= getIntent().getStringExtra("Steps");

        TextView view = findViewById(R.id.steps_taken);
        view.setText("steps : " + steps);


    }

    public void sendToFinance(View view) {
        Intent intent = new Intent(FitTab.this, FinanceTab.class);
        startActivity(intent);
    }


    public void sendToLife(View view) {
        Intent intent = new Intent(FitTab.this, LifeTab.class);
        startActivity(intent);
    }

    public void sendToPay(View view) {
        Intent intent = new Intent(FitTab.this, PaymentTab.class);
        startActivity(intent);
    }

    /*Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            readData();

        }
    };


    class MainTimerTask extends TimerTask {
        public void run() {
            mHandler.post(mUpdateTimeTask);
        }
    }

    @Override
    protected void onResume() {
        MainTimerTask timerTask = new MainTimerTask();
        mTimer.schedule(timerTask, 5000, 10000);
        super.onResume();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("log",Integer.toString(resultCode));
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();

            }
        }
    }

@Override
    protected void onResume()
    {

        super.onResume();
        Log.d("log","in resume");
        readData();
    }

    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Log.d("log","in subscribe");
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    /*Log.i(TAG, "Successfully subscribed!");*/
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }

                                readData();
                            }
                        });
    }

    private void readData() {
        Log.d("log","in readdata");
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {

                            @Override
                            public void onSuccess(DataSet dataSet) {

                                int total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();


                                TextView textView = findViewById(R.id.steps_taken);
                                textView.setText("오늘 걸은 걸음 : " + Long.toString(total));
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }

}

