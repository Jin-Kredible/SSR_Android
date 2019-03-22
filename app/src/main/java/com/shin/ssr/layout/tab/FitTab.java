package com.shin.ssr.layout.tab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.stepcounter.MainActivity;
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
import com.shin.ssr.layout.chart.MyMarkerView;
import com.shin.ssr.layout.chart.MyXAxisValueFormatter;
import com.shin.ssr.layout.notification.PushNotification;
import com.shin.ssr.layout.point.Point;
import com.shin.ssr.vo.StepVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import at.grabner.circleprogress.CircleProgressView;

import static android.graphics.Color.rgb;


public class FitTab extends AppCompatActivity  {

    private PopupWindow mPopupWindow;


    private static double step_percentage;
    private static double mall_percentage;
    private static double ssgpaycon_percentage;

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private Timer mTimer = new Timer();
    private LineChart lineChart;
    private final LineChart[] charts = new LineChart[1];

    public static final String SERVER_URL="http://15.164.49.52:8088/";
    public ImageView help;
    private int total;
    private Handler handler=new Handler();



    /*Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            updateSteps();
            TextView txtView = findViewById(R.id.steps_taken);
            TextView txtView2 = findViewById(R.id.todo1_step);
            txtView.setText(" " + total + " / 6000  ");
            txtView2.setText(" " + total + " / 6000  ");
            String text = "<font color='#333743'> <b> "+total+ "</b> / 6000 </font>";
            txtView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            mHandler.sendEmptyMessageDelayed(0,2000);
        }
    };*/

    private FrameLayout mBackground;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fit_tab_activity);

            readData();
        /*FitnessOptions fitnessOptions =
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
            android.util.Log.d("fit","in Fitness regist1");
        } else {
            android.util.Log.d("fit","in Fitness regist2");
            subscribe();

        }*/

        Log.d("fit","after readdata"+Integer.toString(total));

        setTitle("LineChartActivityColored");
        charts[0] = findViewById(R.id.chart1);

        help = findViewById(R.id.helppop);
        help.setOnClickListener(new helpListener());

        mBackground = findViewById(R.id.backmain);
        readData();



            handler.post(new Runnable(){
                @Override
                public void run() {
                    updateData();
                    TextView txtView = findViewById(R.id.steps_taken);
                    TextView txtView2 = findViewById(R.id.todo1_step);
                    txtView.setText(" " + total + " / 7000  ");
                    txtView2.setText(" " + total + " / 7000  ");
                    String text = "<font color='#333743'> <b> "+total+ "</b> / 7000 </font>";
                    txtView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                    handler.postDelayed(this,5000); // set time here to refresh textView
                }
            });
    }


    public void sendToFinance(View view) {
        Intent intent = new Intent(FitTab.this, MainActivity.class);
        intent.putExtra("buttonNum",1);
        startActivity(intent);
        finish();
    }

    public void sendToPay(View view) {
        Intent intent = new Intent(FitTab.this, MainActivity.class);
        intent.putExtra("buttonNum",2);
        startActivity(intent);
        finish();
    }

    public void sendToLife(View view) {
        Intent intent = new Intent(FitTab.this, MainActivity.class);
        intent.putExtra("buttonNum",3);
        startActivity(intent);
        finish();
    }

    public void sendToPoint(View view) {
        Intent intent = new Intent(FitTab.this, Point.class);
        android.util.Log.d("CHECK", "sendToPoint: OK");
        startActivity(intent);
    }


    CircleProgressView mCircleView;



        @SuppressLint("ClickableViewAccessibility")
        public void getTodoList(double result){

            this.step_percentage = result;
            System.out.println("getTodoList" + result);

            System.out.println("getTodoList" + this.step_percentage);


            View popupView = getLayoutInflater().inflate(R.layout.popup_window, null);

            mBackground.setVisibility(View.VISIBLE);
            mCircleView = popupView.findViewById(R.id.circleView);
            mCircleView.setFocusable(true);
            mBackground.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        mBackground.setVisibility(View.GONE);
                    }
                    return false;
                }
            });


            mCircleView.setValueAnimated((float)step_percentage);


        mPopupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);


    }



    public void stepgoal2(View v){
            HttpUtil_Todo hu = new HttpUtil_Todo(FitTab.this);
            String[] params = {SERVER_URL+"todayGoal.do", "wk_am:"+ 0, "user_id:"+ 2} ;
            hu.execute(params);


    }

    public void stepgoal1(View v){
        HttpUtil_Todo1 hu = new HttpUtil_Todo1(FitTab.this);

        String[] params = {SERVER_URL+"visitmall.do", "wk_am:"+ 0, "user_id:"+ 1} ;
        hu.execute(params);



    }



    public void getTodoList2(double result){

        this.mall_percentage = result;
        System.out.println("getTodoList" + result);

        System.out.println("getTodoList" + this.mall_percentage);


        View popupView = getLayoutInflater().inflate(R.layout.popup_window2, null);

        mBackground.setVisibility(View.VISIBLE);
        mCircleView = popupView.findViewById(R.id.circleView);
        mCircleView.setFocusable(true);
        //mPopupWindow.setOutsideTouchable(true);
        mBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mBackground.setVisibility(View.GONE);
                }
                return false;
            }
        });


        mCircleView.setValueAnimated(1);


        mPopupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);


    }




    public void stepgoal3(View v){


        View popupView = getLayoutInflater().inflate(R.layout.popup_window3, null);

        mBackground.setVisibility(View.VISIBLE);
        mCircleView = popupView.findViewById(R.id.circleView);



        mCircleView.setValueAnimated(1);


        mPopupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    }

    private final int[] colors = new int[] {
            /*Color.rgb(217, 77, 50)*/
            rgb(250, 250, 250)
    };

    private void setupChart(LineChart chart, LineData data, int color) {

        Log.d("fit", "in setup chart");

        // no description text
        chart.getDescription().setEnabled(false);

        // chart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);
//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(color);



        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setWordWrapEnabled(true);
        l.setTextColor(Color.rgb(51, 55, 68));
        l.setXEntrySpace(20f);
        l.setMaxSizePercent(0.5f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTypeface(Typeface.createFromAsset(getAssets(),"font/applesgothic_regular.ttf"));
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setSpaceTop(40);
        chart.getAxisLeft().setSpaceBottom(40);
        chart.getAxisRight().setEnabled(false);
   /*     chart.getXAxis().setEnabled(false);*/


        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(50, 30, 50, 90);

        Log.d("log","inside chart creation");



        XAxis xAxis = chart.getXAxis();

        xAxis.setYOffset(-10f);
        xAxis.setTextSize(10f);
        xAxis.setTypeface(Typeface.createFromAsset(getAssets(),"font/applesgothic_regular.ttf"));
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTextColor(Color.rgb(51, 55, 68));
        xAxis.setTextSize(15f);

        /*String[] values = new String[] {"월","화","수","목","금","토","일"};*/


        String[] today = new String[7];

        for(int i=0; i< today.length; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-i);
            today[today.length-i-1] = new SimpleDateFormat("EE").format(cal.getTime());
        }


        xAxis.setValueFormatter(new MyXAxisValueFormatter(today));


        // animate calls invalidate()...
    /*    chart.animateX(700);*/
        chart.animateY(1200,Easing.EasingOption.EaseInOutCirc);
       /* chart.animateX(1200, Easing.EasingOption.EaseInCubic);*/

        MyMarkerView marker = new MyMarkerView(this,R.layout.markerviewtext);
        marker.setChartView(charts[0]);
        charts[0].setMarker(marker);

        LimitLine ll1 = new LimitLine(6000F, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setTextSize(10f);
        ll1.setLineColor(Color.RED);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setDrawLimitLinesBehindData(false);
    }




    private LineData getData(int count, float range, int total, ArrayList<StepVO> stepAry) {


        Log.d("fit", "in getdata");
        for(int i =0; i < stepAry.size(); i++ ) {
            Log.d("values", Integer.toString(stepAry.get(i).getWk_am()));
        }

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i  <7; i++) {
            Log.d("result", "are you here");
            float val = (float) stepAry.get(i).getWk_am();
            values2.add(new Entry(i, val));
        }

        for (int i = 7; i < stepAry.size(); i++) {
                Log.d("result", "are you here");
                float val = (float) stepAry.get(i).getWk_am();
                values.add(new Entry(i-7, val));

            }
        values.add(new Entry(6, total));



        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "이번 주");
        LineDataSet set2 = new LineDataSet(values2, "지난 주");
   /*     set1.setFillAlpha(110);
        set1.setFillColor(Color.RED);*/


        set2.setLineWidth(1.75f);
        set2.setCircleRadius(7f);
        set2.setCircleHoleRadius(2.5f);
        set2.setCircleColorHole(Color.TRANSPARENT);
        set2.setColor(rgb(227, 179, 196));
        set2.setCircleColor(rgb(227, 179, 196));
        set2.setHighLightColor(rgb(227, 179, 196));
        set2.setDrawValues(false);


        set1.setLineWidth(1.75f);
        set1.setCircleRadius(7f);
        set1.setCircleHoleRadius(2.5f);
        set1.setCircleColorHole(Color.TRANSPARENT);
        set1.setColor(Color.rgb(203, 55, 55));
        set1.setCircleColor(Color.rgb(203, 55, 55));
        set1.setHighLightColor(rgb(203, 55, 55));
        set1.setValueTextColor(Color.rgb(51, 55, 68));
        set1.setValueTextSize(12f);
        set1.setDrawValues(false);




        // create a data object with the data sets

        return new LineData(set2, set1);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("fit", "in activity result");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();

    }

    /*public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Log.d("fit","in subscribe");
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    *//*Log.i(TAG, "Successfully subscribed!");*//*
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                                Log.d("readdata","before read data - in subscribe");

                            }
                        });
    }*/

    private void readData() {
        Log.d("fit","in readdata");
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {

                            @Override
                            public void onSuccess(DataSet dataSet) {
                                ArrayList<StepVO> stepAry = new ArrayList<>();
                                HttpUtil hu = new HttpUtil(FitTab.this);

                                String[] params = {SERVER_URL+"step.do", "wk_am:"+ total, "user_id:"+ 1} ;

                                hu.execute(params);
                                total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                                JSONArray object = null;
                                String result;
                                try {
                                    result = hu.get();
                                    object =  new JSONArray(result);

                                    android.util.Log.d("log","result from spring" + result);

                                    for(int i =0; i < object.length(); i++) {
                                        JSONObject obj = (JSONObject)object.get(i);
                                        android.util.Log.d("log",obj.getString("wk_am"));
                                        android.util.Log.d("log",obj.getString("user_id"));
                                        stepAry.add(new StepVO(obj.optInt("user_id"),obj.optInt("wk_am"),obj.optString("wk_dt")));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                Log.d("fit", "todays walk");
                                Log.d("fit", "stepvO" + stepAry);

                                LineData data1 = getData(7, 10000, total, stepAry);

                                Log.d("fit","getdata" + data1.getDataSets().toString());

                                // add some transparency to the color with "& 0x90FFFFFF"
                                setupChart(charts[0], data1, colors[0 % colors.length]);

                                TextView txtView = findViewById(R.id.steps_taken);
                                TextView txtView2 = findViewById(R.id.todo1_step);
                                txtView.setText(" " + total + " / 7000  ");

                                String text = "<font color='#333743'> <b> "+total+ "</b> / 7000 </font>";
                                txtView2.setText(" " + total + " / 7000  ");
                                txtView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

                                //////////////http connection
                                 // 서버 주소


                                if(total>=100) {
                                    Log.d("fit", "inside checkbox");
                                    CheckBox step_checkbox = findViewById(R.id.steps_check);
                                    step_checkbox.setChecked(true);
                                }
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

    public void httpWeb(){

    }

    public void printToast(String rtn) {
        Toast.makeText(FitTab.this, rtn, Toast.LENGTH_SHORT).show();
    }


    public void abc(){

    }
    View popupView;
    PopupWindow helpPopup;

    class helpListener implements View.OnClickListener {

        @Override
        public void onClick(View helpicon) {
            Toast.makeText(getApplicationContext(),"are you clicked?",Toast.LENGTH_LONG).show();
            popupView = getLayoutInflater().inflate(R.layout.help_popup_activity,null);
            helpPopup= new PopupWindow(popupView, 1000, 1000,true);
            helpPopup.setAnimationStyle(-1);
            helpPopup.showAtLocation(popupView, Gravity.CENTER, 0,0);

            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        popupView.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
        }


    }

    public void getPastSteps(ArrayList<StepVO> arry) {

    }


 /*   protected void updateSteps() {

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {

                            @Override
                            public void onSuccess(DataSet dataSet) {

                                total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();




                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });

    }*/


    private void updateData() {
        Log.d("fit","in readdata");
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {

                            @Override
                            public void onSuccess(DataSet dataSet) {
                                total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();




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


