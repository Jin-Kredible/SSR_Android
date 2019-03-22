package com.shin.ssr.layout.point;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.fit.samples.stepcounter.R;
import static com.shin.ssr.layout.tab.FitTab.SERVER_URL;

public class Point extends AppCompatActivity {
    ProductThread thread;      //Prodoct Move Control
    Thread2 thread2;    //Prodoct Rotate Control*/


    //Thread stop message
    public static final int SEND_INFORMATION = 0;
    public static final int SEND_STOP = 1;
    public static final int SEND_STOP_ROTATION = 2;

    private ImageView imgCon, imgPro, imgGetPro;
    private TextView getPoint;
    private ImageView imgCart, imgAd;

    //광고 이미지 배열
    int[] imgs = {R.drawable.ad_0, R.drawable.ad_1, R.drawable.ad_2, R.drawable.ad_3, R.drawable.ad_4};

    private float dx = 20;          //Product 이동 정도
    private float dy = 10;
    private float dr = 15;
    private float hx = 0;           //Product 현재 위치
    private float hy = 0;
    private float hr = 0;
    private float resetX = 0;       //Product 처음 위치
    private float resetY = 0;
    private float resetR = 0;

    int walk = 0;              //DB에서 가져오는 걸음 수
    int totalwalk = 0;         //DB에서 가져오는 걸음 수 , Point적립 최대치 제한두기 위한 변수
    int numPoint = 0;           //얻는 point
    boolean done = false;       //point 전환 끝 확인 변수
    boolean none = false;       //DB에서 가져오는 걸음 수 여부(true = 걸음 수 0)

    //DB 값 가져오기 보내기
//    String SERVER_URL="http://172.20.10.9:8088/walkToGoods.do"; // 서버 주소
//    HttpUtil hu = new HttpUtil(Point.this);


    @SuppressLint("ClickableViewAccessibility")
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

        HttpUtil_P hu = new HttpUtil_P(Point.this);
        String[] params = {SERVER_URL + "walkToGoods.do", "steps:" + 1, "userno:" + 1};
        Log.d("pointy", Boolean.toString(Thread.currentThread().isInterrupted()));
        hu.execute(params);
        Log.d("pointy", "inside Try");
        Log.d("pointy", "inside Try after get" + walk);

        //walk = getPoint();

        Log.d("pointy", "point" + walk);
        Log.d("pointy", "after try" + walk);
        Log.d("NUM", "onCreate: WalkNum" + walk);
        Log.d("NUM", "onCreate: TotalWalkNum" + walk);



        resetX = imgPro.getTranslationX();
        resetY = imgPro.getTranslationY();
        resetR = imgPro.getRotation();
        Log.d("pointy resetX", Float.toString(resetX));
        Log.d("pointy resetY", Float.toString(resetY));
        Log.d("pointy resetZ", Float.toString(resetR));


        final AnimationDrawable drawable = (AnimationDrawable) imgCon.getBackground();  //Conveyor belt animation

        //Touch event



        imgCon.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("pointy", Integer.toString(walk) + "inside on touch listeneer");

                        if (none) {
                            imgPro.setVisibility(View.GONE);

                        } else {
                            thread = new ProductThread();
                            System.out.println("alive   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+thread.isAlive());
                            if(thread.isAlive()) {

                                //thread.notify();
                            }else {

                                thread.start();
                                System.out.println("alive   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> start");

                            }
                        }
                        //Log.d("check", "onTouch: productMove" + hx);
                        drawable.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("pointy", "onTouch: up!!!!!!!!!!!!!!");
                        drawable.stop();
                        if(!none)   handler.sendEmptyMessage(SEND_STOP);
                        /*try {
                            System.out.println("alive   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  wait");
                            thread.stopThread();
                            //thread.wait();
                        }catch(Exception e) {
                            e.printStackTrace();
                        }*/
                        break;
                }
                return false;
            }
        });

        Log.d("pointy", "after: setOnTouchListener");
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

    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d("pointy", "onWindowFocusChanged");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        Log.d("pointy", "onPostResume");
    }

    //Thread Stop Handler
    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_INFORMATION:
                    break;
                case SEND_STOP: //Product Move Stop
                    thread.stopThread();
                    //Toast.makeText(getApplicationContext(), "Thread중지", Toast.LENGTH_LONG).show();
                    Log.d("pointy", "run: stop thread1");
                    break;
                case SEND_STOP_ROTATION:    //Product get Stop
                    thread2.stopThread();

                    if(done && !none) {

                        Get();


                        done = false;

                        HttpUtil_P_UPDATE hu = new HttpUtil_P_UPDATE(Point.this);;
                        String[] params = {SERVER_URL+"goodsToSavings.do", "numPoint:"+10, "userid:"+1} ;
                        Log.d("NUM", "toFit: NUMPOINT  "+numPoint);
                        hu.execute(params);

                    }

                    Log.d("pointy", "run: stop thread2");
                    //Toast.makeText(getApplicationContext(), "Thread2중지", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    class ProductThread extends java.lang.Thread {
        boolean stopped1;
        int i = 0;

        public ProductThread() {
            stopped1 = false;
        }

        public void stopThread() {
            stopped1 = true;
        }

        @Override
        public void run() {
            Log.d("pointy", "inside thread1 run");
            //super.run();
            //try {
                while (!stopped1) {
                    i++;
                    // 메시지 얻어오기
                    Message message = handler.obtainMessage();
                    // 메시지 ID 설정
                    message.what = SEND_INFORMATION;
                    // 메시지 내용 설정 (int)
                    message.arg1 = i;
                    // 메시지 내용 설정 (Object)
                    String information = new String("초 째 Thread 동작 중입니다.");
                    message.obj = information;
                    // 메시지 전
                    //handler.sendMessage(message);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (imgPro.getTranslationX() == 700) {

                                Log.d("pointy", "before stopping");
                                if(!none) {
                                    thread2 = new Thread2();
                                    thread2.start();
                                }
                            }
                            hx = (int) imgPro.getTranslationX();
                            hr = (int) imgPro.getRotation();


                            if (hx < 700) {
                                imgPro.setTranslationX(hx + dx);
                            }
                            hx = (int) imgPro.getTranslationX();
                            hy = (int) imgPro.getTranslationY();
                        }
                    });
                    SystemClock.sleep(20);
                }
            /*} catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

    class Thread2 extends java.lang.Thread {
        boolean stopped2;
        int i = 0;

        public Thread2() {
            stopped2 = false;
        }

        public void stopThread() {
            stopped2 = true;

        }

        @Override
        public void run() {
            Log.d("pointy", "inside thread2 run");
            //super.run();
           // try {
                while (!stopped2) {
                    i++;
                    // 메시지 얻어오기
                    Message message = handler.obtainMessage();
                    // 메시지 ID 설정
                    message.what = SEND_INFORMATION;
                    // 메시지 내용 설정 (int)
                    message.arg1 = i;
                    // 메시지 내용 설정 (Object)
                    String information = new String("초 째 Thread 동작 중입니다.");
                    message.obj = information;
                    // 메시지 전
                    handler.sendMessage(message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(imgPro.getTranslationX() == 900){
                            //if (imgPro.getTranslationX() >=  900 && imgPro.getTranslationX() <920) {
                                Log.d("pointy", "before rotation get translation x" + imgPro.getTranslationX());
                                Log.d("pointy", "before rotation");
                                done = true;
                                handler.sendEmptyMessage(SEND_STOP_ROTATION);
                                //stopThread();
                            }
                            dx = 10;
                            imgPro.setTranslationY(hy + dy);
                            imgPro.setTranslationX(hx + dx);
                            imgPro.setRotation(hr + dr);

                            hx = (int) imgPro.getTranslationX();
                            hy = (int) imgPro.getTranslationY();

                        }
                    });
                    //sleep(20);
                    SystemClock.sleep(20);
                }
            /*} catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //추가
            stopThread();
        }
    }

    public synchronized  void Get() {
        Log.d("pointy", "Get: numPoint : " + numPoint + "/ walk : " + walk +"totlal walk : " + totalwalk);
        numPoint += 10;
        if (numPoint > totalwalk * 10) {
            numPoint = totalwalk;
        }
        walk--;
        if(walk==0) none=true;
        getPoint.setText(Integer.toString(numPoint));
        Log.d("pointy", "Get: numPoint2nd : " + numPoint + "/ walk : " + walk +"totlal walk : " + totalwalk);


        imgAd.setBackgroundResource(imgs[(numPoint / 10) % imgs.length]);

        imgGetPro.setTranslationX(imgPro.getTranslationX());
        Log.d("pointy getX", Float.toString(imgPro.getTranslationX()));
        Log.d("pointy getXGet", Float.toString(imgGetPro.getTranslationX()));

        imgGetPro.setTranslationY(imgPro.getTranslationY());
        Log.d("pointy getY", Float.toString(imgPro.getTranslationY()));
        Log.d("pointy getYGet", Float.toString(imgGetPro.getTranslationY()));

        imgGetPro.setRotation(imgPro.getRotation());
        Log.d("pointy getZ", Float.toString(imgPro.getRotation()));
        Log.d("pointy getGetZ", Float.toString(imgGetPro.getRotation()));
        imgGetPro.setVisibility(View.VISIBLE);

        imgCart.bringToFront();
        setViewInvalidate(imgCart, imgGetPro);

        imgPro.setTranslationX(0);
        imgPro.setTranslationY(0);
        imgPro.setRotation(0);

        Log.d("pointy getX0", Float.toString(imgPro.getX()));
        Log.d("pointy getX0", Float.toString(imgPro.getTranslationX()));
        Log.d("pointy getY0", Float.toString(imgPro.getY()));
        Log.d("pointy getY0", Float.toString(imgPro.getTranslationY()));
        Log.d("pointy getZ0", Float.toString(imgPro.getRotation()));
        if (walk <= 0) {
            imgPro.setVisibility(View.GONE);
        }
        /*try{
            Thread.sleep(500);
        }catch(Exception e) {
            e.printStackTrace();
        }*/
    }

    private void setViewInvalidate(View... views) {
        for (View v : views) {
            v.invalidate();
        }
    }

    public void toFit(View view) {
        finish();
    }

    public void getPoints(String point) {
        Log.d("pointy", "inside get point" + point);
        this.walk = Integer.parseInt(point);
        totalwalk = walk;
        if(walk == 0){
            imgPro.setVisibility(View.GONE);
            none = true;
        }
    }

    public int getPoint() {
        return this.walk;
    }
}
