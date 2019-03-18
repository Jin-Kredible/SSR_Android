package com.shin.ssr.layout.point;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.fit.samples.stepcounter.R;
import com.google.android.gms.fitness.data.Field;
import com.google.gson.JsonObject;
import com.shin.ssr.layout.tab.FitTab;
import com.shin.ssr.layout.tab.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.shin.ssr.layout.tab.FitTab.SERVER_URL;

public class Point extends AppCompatActivity {
    Thread thread;      //Prodoct Move Control
    Thread2 thread2;    //Prodoct Rotate Control


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        imgCon = findViewById(R.id.imgConveyor);
        imgPro = findViewById(R.id.imgProduct);
        imgGetPro = findViewById(R.id.imgGetProduct);
        imgCart = findViewById(R.id.imgCartF);
        imgAd = findViewById(R.id.imgAd);

        getPoint = findViewById(R.id.Point);
        getPoint.setText(Integer.toString(numPoint));

        resetX = imgPro.getTranslationX();
        resetY = imgPro.getTranslationY();
        resetR = imgPro.getRotation();

        int total= 0;
        HttpUtil hu = new HttpUtil(Point.this);
        String[] params = {SERVER_URL+"walkToGoods.do", "steps:"+total , "userno:"+ 1} ;
        hu.execute(params);
        String result;
        try {
            // resutl가 JSON 객체를 서버로부터 받아옴
            result = hu.get();
            //JSONArray object = null;
            android.util.Log.d("log","result from spring" + result);
            walk = Integer.parseInt(result);
            totalwalk = walk*10;
            /*try {
                // object =  new JSONArray(result);
                Log.d("NUM", "onCreate: ObjectNum"+result);
                //walk = object.getInt(Integer.parseInt("goods"));
                Log.d("NUM", "onCreate: WalkNum"+walk);
                Log.d("NUM", "onCreate: TotalWalkNum"+walk);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("NUM", "onCreate: WalkNum"+walk);
        Log.d("NUM", "onCreate: TotalWalkNum"+walk);



        final AnimationDrawable drawable = (AnimationDrawable) imgCon.getBackground();  //Conveyor belt animation
        //Touch event
        imgCon.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN :
                        //Log.d("check", "onTouch: productMove" + hx);
                        drawable.start();
                        if(walk > 0) {
                            thread = new Thread();
                            thread.start();
                        }else{
                            imgPro.setVisibility(View.GONE);
                            imgGetPro.setVisibility(View.GONE);
                        }
                        break;
                    case MotionEvent.ACTION_UP :
                        Log.d("check", "onTouch: up!!!!!!!!!!!!!!");
                        drawable.stop();
                        handler.sendEmptyMessage(SEND_STOP);
                        break;
                }
                return false;
            }
        });
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
                    if(done) {
                        Get();
                    }
                    thread2.stopThread();
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
            super.run();
            while(stopped == false){
                i++;
                move();
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
            super.run();
            while(stopped == false){
                i++;
                move2();
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
                    if(imgPro.getTranslationX()==900){
                        done = true;
                        handler.sendEmptyMessage(SEND_STOP_ROTATION);
                    }
                    sleep(30);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void move(){
        //Log.d("check", "onTouch: productMove" + hx);
        hx = (int) imgPro.getTranslationX();
        hr = (int) imgPro.getRotation();
        if(hx<700){
            imgPro.setTranslationX(hx + dx);
        }
        hx = (int) imgPro.getTranslationX();
        hy = (int)imgPro.getTranslationY();
    }
    public void move2(){
        dx = 10;
        imgPro.setTranslationY(hy + dy);
        imgPro.setTranslationX(hx + dx);
        imgPro.setRotation(hr + dr);
        hx = (int) imgPro.getTranslationX();
        hy = (int)imgPro.getTranslationY();
    }
    public void Get(){
        Log.d("checkGet", "Get: numPoint : "+numPoint+"// walk : "+walk);
        numPoint= numPoint+10;
        if(numPoint>totalwalk){
            numPoint = totalwalk;
        }
        walk--;
        imgAd.setBackgroundResource(imgs[(numPoint/10)%imgs.length]);
        getPoint.setText(Integer.toString(numPoint));
        imgGetPro.setVisibility(View.VISIBLE);
        imgGetPro.setTranslationX(imgPro.getTranslationX());
        imgGetPro.setTranslationY(imgPro.getTranslationY());
        imgGetPro.setRotation(imgPro.getRotation());
        imgPro.setTranslationX(resetX);
        imgPro.setTranslationY(resetY);
        imgPro.setRotation(resetR);
        imgCart.bringToFront();
        setViewInvalidate(imgCart,imgGetPro);
        if(walk <= 0) {
            imgPro.setVisibility(View.GONE);
            imgGetPro.setVisibility(View.GONE);
        }
        HttpUtil hu = new HttpUtil(Point.this);;
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
        Toast.makeText(getApplicationContext(),"CLOSE",Toast.LENGTH_LONG).show();
       /* HttpUtil hu = new HttpUtil(Point.this);;
        String[] params = {SERVER_URL+"goodsToSavings.do", "numPoint:"+numPoint, "userid:"+1} ;
        Log.d("NUM", "toFit: NUMPOINT  "+numPoint);
        hu.execute(params);*/
        Intent intent = new Intent(Point.this,FitTab.class);
        startActivity(intent);
        finish();
    }
}
