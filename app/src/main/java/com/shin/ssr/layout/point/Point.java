package com.shin.ssr.layout.point;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.fit.samples.backgroundgps.RealService;
import com.google.android.gms.fit.samples.stepcounter.R;
import com.google.android.gms.fitness.data.Field;
import com.google.gson.JsonObject;
import com.shin.ssr.layout.tab.FitTab;
import com.shin.ssr.layout.tab.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.google.android.gms.fit.samples.backgroundgps.RealService.mainThread;
import static com.shin.ssr.layout.tab.FitTab.SERVER_URL;

public class Point extends AppCompatActivity {
    Thread thread;      //Prodoct Move Control
   Thread2 thread2;    //Prodoct Rotate Control*/


    //Thread stop message
    public static final int SEND_INFORMATION =0;
    public static final int SEND_STOP = 1;
    public static final int SEND_STOP_ROTATION = 2;

    private ImageView imgCon, imgPro, imgGetPro;
    private TextView getPoint;
    private ImageView   imgCart, imgAd;

    //광고 이미지 배열
    int[] imgs = {R.drawable.ad_0, R.drawable.ad_1, R.drawable.ad_2, R.drawable.ad_3, R.drawable.ad_4};

    private int dx = 20;        //Product 이동 정도
    private int dy = 10;
    private int dr = 15;
    private int hx = 0;         //Product 현재 위치
    private int hy = 0;
    private int hr = 0;
    private float resetX = 0;   //Product 처음 위치
    private float resetY = 0;
    private float resetR = 0;

    int walk = 0;              //DB에서 가져오는 걸음 수
    int totalwalk = 0;         //DB에서 가져오는 걸음 수 , Point적립 최대치 제한두기 위한 변수
    int numPoint = 0;           //얻는 point
    boolean done = false;       //point 전환 끝 확인 변수

    //DB 값 가져오기 보내기
//    String SERVER_URL="http://172.20.10.9:8088/walkToGoods.do"; // 서버 주소
//    HttpUtil hu = new HttpUtil(Point.this);


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        imgCon = findViewById(R.id.imgConveyor);
        imgPro = findViewById(R.id.imgProduct);
        imgGetPro = findViewById(R.id.imgGetProduct);
        imgCart = findViewById(R.id.imgCartF);
        imgAd = findViewById(R.id.imgAd);

        getPoint = findViewById(R.id.Point);

        resetX = imgPro.getTranslationX();
        resetY = imgPro.getTranslationY();
        resetR = imgPro.getRotation();




       /* Thread dataThread = new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        while(true) {*/

                                HttpUtil_P hu = new HttpUtil_P(Point.this);
                                String[] params = {SERVER_URL+"walkToGoods.do", "steps:"+1 , "userno:"+ 1} ;
                                Log.d("pointy", Boolean.toString(Thread.currentThread().isInterrupted()));
                                hu.execute(params);
                                Log.d("pointy", "inside Try");

                                Log.d("pointy", "inside Try after get" + walk );

                            walk = getPoint();

                            Log.d("pointy", "YO?");
                            Log.d("pointy", "YO?2");
                            Log.d("pointy", "point"+ walk);


                            Log.d("pointy", "after try" + walk);

                            Log.d("NUM", "onCreate: WalkNum"+walk);
                            Log.d("NUM", "onCreate: TotalWalkNum"+walk);


                     /*   }
                    }
                }
        );

       dataThread.start();*/




        final AnimationDrawable drawable = (AnimationDrawable) imgCon.getBackground();  //Conveyor belt animation

        //Touch event
        imgCon.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN :
                        //Log.d("check", "onTouch: productMove" + hx);
                        drawable.start();
                        Log.d("pointy", Integer.toString(walk) + "inside on touch listeneer");
                        if(walk > 0) {
                            thread = new Thread();
                            thread.start();
                        }else{
                            imgPro.setVisibility(View.GONE);
                            imgGetPro.setVisibility(View.GONE);
                        }
                        break;
                    case MotionEvent.ACTION_UP :
                        Log.d("pointy", "onTouch: up!!!!!!!!!!!!!!");
                        drawable.stop();
                        handler.sendEmptyMessage(SEND_STOP);
                        break;
                }
                return false;
            }
        });

        Log.d("pointy", "after: setOnTouchListener");

        getPoint.setText(Integer.toString(walk));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("pointy", "onresume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("pointy", "onStart");
    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.d("pointy", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("pointy", "onStop");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("pointy", "activity result");
    }


    @Override
    public void onPostCreate(@androidx.annotation.Nullable Bundle savedInstanceState, @androidx.annotation.Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        Log.d("pointy", "onPostCreate");
    }
    public void onWindowFocusChanged (boolean hasFocus) {
        Log.d("pointy", "onWindowFocusChanged");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        Log.d("pointy", "onPostResume");
    }

    //Thread Stop Handler
    final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_INFORMATION:
                    break;
                case SEND_STOP: //Product Move Stop
                    thread.stopThread();
                    //Toast.makeText(getApplicationContext(), "Thread중지", Toast.LENGTH_LONG).show();
                    break;
                case SEND_STOP_ROTATION:    //Product get Stop
                    thread2.stopThread();

                    Log.d("check", "run: stop thread2");
                    //Toast.makeText(getApplicationContext(), "Thread2중지", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    class Thread extends java.lang.Thread{
        boolean stopped = false;
        int i = 0;

        public Thread() {
            stopped = false;
        }


        public void stopThread(){
            stopped = true;
        }

        @Override
        public void run() {
            Log.d("pointy", "inside thread1 run");
            super.run();
            while(stopped == false){
                i++;
                // 메시지 얻어오기
                Message message = handler.obtainMessage();
                // 메시지 ID 설정
                message.what = SEND_INFORMATION;
                // 메시지 내용 설정 (int)
                message.arg1 = i;
                // 메시지 내용 설정 (Object)
                String information = new String("초 째 Thread 동작 중입니다."); message.obj = information;
                // 메시지 전
                handler.sendMessage(message);

                try{
                    if(imgPro.getTranslationX()==700) {
                        thread2 = new Thread2();
                        thread2.start();
                    }
                    sleep(20);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hx = (int) imgPro.getTranslationX();
                        hr = (int) imgPro.getRotation();
                        if(hx<700){
                            imgPro.setTranslationX(hx + dx);
                        }
                        hx = (int) imgPro.getTranslationX();
                        hy = (int)imgPro.getTranslationY();
                    }
                });

            }

        }
    }
    class Thread2 extends java.lang.Thread{
        boolean stopped = false;
        int i = 0;

        public Thread2() {
            stopped = false;
        }
        public void stopThread(){
            stopped = true;
        }

        @Override
        public void run() {
            Log.d("pointy", "inside thread2 run");
            super.run();
            while(stopped == false){
                i++;
                // 메시지 얻어오기
                Message message = handler.obtainMessage();
                // 메시지 ID 설정
                message.what = SEND_INFORMATION;
                // 메시지 내용 설정 (int)
                message.arg1 = i;
                // 메시지 내용 설정 (Object)
                String information = new String("초 째 Thread 동작 중입니다."); message.obj = information;
                // 메시지 전
                handler.sendMessage(message);

                try{
                    sleep(20);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(imgPro.getTranslationX()==900){
                            //done = true;
                            Get();
                            handler.sendEmptyMessage(SEND_STOP_ROTATION);
                        }
                        dx = 10;
                        imgPro.setTranslationY(hy + dy);
                        imgPro.setTranslationX(hx + dx);
                        imgPro.setRotation(hr + dr);
                        hx = (int) imgPro.getTranslationX();
                        hy = (int)imgPro.getTranslationY();
                    }
                });
            }
        }
    }
    public void Get(){
        Log.d("pointy", "Get: numPoint : "+numPoint+"/ walk : "+walk);
        numPoint+=10;
        if(numPoint>totalwalk*10){
            numPoint = totalwalk;
        }
        walk--;

        Log.d("pointy", "Get: numPoint2nd : "+numPoint+"/ walk : "+walk);
        imgAd.setBackgroundResource(imgs[(numPoint/10)%imgs.length]);
        Log.d("pointy", "Get:"+ imgs[(numPoint/10)%imgs.length]);


        getPoint.setText(Integer.toString(numPoint));
        imgGetPro.setVisibility(View.VISIBLE);
        imgGetPro.setTranslationX(imgPro.getTranslationX());
        Log.d("pointy", Float.toString(imgPro.getTranslationX()));

        imgGetPro.setTranslationY(imgPro.getTranslationY());
        Log.d("pointy", Float.toString(imgPro.getTranslationY()));

        imgGetPro.setRotation(imgPro.getRotation());
        Log.d("pointy", Float.toString(imgPro.getRotation()));
        
        imgPro.setTranslationX(resetX);
        Log.d("pointy", Float.toString(resetX));

        imgPro.setTranslationY(resetY);
        Log.d("pointy", Float.toString(resetX));

        imgPro.setRotation(resetR);
        imgCart.bringToFront();
        setViewInvalidate(imgCart,imgGetPro);
        if(walk <= 0) {
            imgPro.setVisibility(View.GONE);
        }

        HttpUtil_P_UPDATE hu = new HttpUtil_P_UPDATE(Point.this);;
        String[] params = {SERVER_URL+"goodsToSavings.do", "numPoint:"+numPoint, "userid:"+1} ;
        Log.d("NUM", "toFit: NUMPOINT  "+numPoint);
        hu.execute(params);
    }
    private void setViewInvalidate(View... views) {

        for (View v : views) {
            v.invalidate();
        }

    }
    public class MessageList{
        public static final int MSG_REFRESH = 1;
    }

    public void toFit(View view){

        finish();
    }


    public void getPoints(String point) {
        Log.d("pointy", "inside get point" + point);
        this.walk = Integer.parseInt(point);
        totalwalk = walk;
    }

    public int getPoint() {
        return this.walk;
    }
}
