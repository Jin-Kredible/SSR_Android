package com.shin.ssr.layout.point;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.fit.samples.stepcounter.R;

import static com.google.android.gms.fit.samples.stepcounter.MainActivity.user_id;
import static com.shin.ssr.layout.tab.FitTab.SERVER_URL;


public class Point extends AppCompatActivity {

    private ImageView imgCon, imgPro, imgGetPro, imgHelp;
    private TextView txtTodayPoint, txtTotalPoint;
    private ImageView imgCart, imgAd;

    //광고 이미지 배열
    int[] imgs = {R.drawable.ad_1, R.drawable.ad_2, R.drawable.ad_3, R.drawable.ad_4, R.drawable.ad_5,
                    R.drawable.ad_6, R.drawable.ad_7, R.drawable.ad_8, R.drawable.ad_9};
    int[] imgsCon = {R.drawable.belt6, R.drawable.belt5, R.drawable.belt4, R.drawable.belt3, R.drawable.belt2, R.drawable.belt1};


    private float dx = 25;          //Product 이동 정도
    private float dy = 30;
    private float dr = 20;
    private float hx = 0;           //Product 현재 위치
    private float hy = 0;
    private float hr = 0;
    private float resetX = 0;       //Product 처음 위치
    private float resetY = 0;
    private float resetR = 0;

    int i = 1;
    int walk = 0;              //DB에서 가져오는 걸음 수
    int totalwalk = 0;         //DB에서 가져오는 걸음 수 , Point적립 최대치 제한두기 위한 변수
    int numPoint = 0;           //얻는 point
    boolean none = true;       //DB에서 가져오는 걸음 수 여부(true = 걸음 수 0)
    int total = 0;


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
        imgHelp = findViewById(R.id.help);
        txtTodayPoint = findViewById(R.id.Point);
        txtTotalPoint = findViewById(R.id.txtTotalPoint);


        HttpUtil_P hu = new HttpUtil_P(Point.this);
        String[] params = {SERVER_URL + "walkToGoods.do", "steps:" + 1, "user_id:" + user_id};
        Log.d("pointy", Boolean.toString(Thread.currentThread().isInterrupted()));
        hu.execute(params);
        Log.d("pointy", "inside Try");
        Log.d("pointy", "inside Try after get" + walk);


        HttpUtil_P_TOTAL hu_total = new HttpUtil_P_TOTAL(Point.this);
        String[] params_total = {SERVER_URL + "totalSavings.do", "steps:" + 1, "user_id:" + user_id};
        Log.d("pointy", Boolean.toString(Thread.currentThread().isInterrupted()));
        hu_total.execute(params_total);


        hx = imgPro.getTranslationX();
        hy = imgPro.getTranslationY();
        resetX = imgPro.getTranslationX();
        resetY = imgPro.getTranslationY();
        resetR = imgPro.getRotation();

        imgHelp.setOnClickListener(new helpListener());

        final AnimationDrawable drawable = (AnimationDrawable) imgCon.getBackground();  //Conveyor belt animation
        //Touch event
        imgCon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("pointy", Integer.toString(walk) + "inside on touch listeneer");
                        Log.d("pointy", "onTouch: none" + none);
                        if (none) {
                            drawable.start();
                            /*imgCon.setBackgroundResource(imgsCon[(i / 6) % 6]);
                            i++;*/
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(motionEvent.getX()>=0 && motionEvent.getX()<=700 &&
                            motionEvent.getY()>=0 && motionEvent.getY()<= 250) {
                            if (!none) {
                                if (hx < 700) {
                                    move();
                                } else if (hx >= 700 && hx < 880) {
                                    rotate();
                                } else if (hx >= 880 && hx <= 940) {
                                    Get();
                                }
                            } else {

                                //imgCon.setBackgroundResource(imgsCon[(i / 6) % 6]);
                                imgCon.setBackgroundResource(imgsCon[i % 6]);
                                i++;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("pointy", "onTouch: up!!!!!!!!!!!!!!");
                        if (none) drawable.stop();
                        break;
                }
                return false;
            }
        });

        Log.d("pointy", "after: setOnTouchListener");
    }

    public void move() {
        imgPro.setTranslationX(hx + dx);
        hx = imgPro.getTranslationX();
        imgCon.setBackgroundResource(imgsCon[i % 6]);
        i++;
    }

    public void rotate() {
        dx = 30;
        imgPro.setTranslationX(hx + dx);
        imgPro.setTranslationY(hy + dy);
        imgPro.setRotation(hr + dr);

        hx = imgPro.getTranslationX();
        hy = imgPro.getTranslationY();
        hy = imgPro.getTranslationY();
        hr = imgPro.getRotation();
        dx=25;

    }


    public void Get() {
        Log.d("pointy", "Get: numPoint : " + numPoint + "/ walk : " + walk + "totlal walk : " + totalwalk);
        numPoint += 10;
        total += 10;
        if (numPoint > totalwalk * 10) {
            numPoint = totalwalk;
        }
        walk--;
        if (walk == 0) {
            none = true;
        }
        Log.d("pointy", "Get: none" + none);
        txtTodayPoint.setText(Integer.toString(walk*10));
        txtTotalPoint.setText(Integer.toString(total));
        Log.d("pointy", "Get: numPoint2nd : " + numPoint + "/ walk : " + walk + "totlal walk : " + totalwalk);


        imgAd.setBackgroundResource(imgs[(numPoint / 10) % imgs.length]);

        imgGetPro.setTranslationX(imgPro.getTranslationX());
        imgGetPro.setTranslationY(imgPro.getTranslationY());
        imgGetPro.setRotation(imgPro.getRotation());
        imgGetPro.setVisibility(View.VISIBLE);

        imgCart.bringToFront();
        setViewInvalidate(imgCart, imgGetPro);

        imgPro.setTranslationX(0);
        imgPro.setTranslationY(0);
        imgPro.setRotation(0);
        hx = 0;
        hy = 0;
        hr = 0;

        if (walk <= 0) {
            imgPro.setVisibility(View.GONE);
        }

        HttpUtil_P_UPDATE hu = new HttpUtil_P_UPDATE(Point.this);
        String[] params = {SERVER_URL + "goodsToSavings.do", "numPoint:" + 10, "userid:" + user_id};
        Log.d("NUM", "toFit: NUMPOINT  " + numPoint);
        hu.execute(params);

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
        if (walk <= 0) {
            imgPro.setVisibility(View.GONE);
            txtTodayPoint.setText("00");
            none = true;
        } else {
            none = false;
            txtTodayPoint.setText(Integer.toString(walk*10));
        }
        Log.d("pointy", "getPoints: " + none);
    }
    public void getTotalPoints(String totalPoint){
        Log.d("pointy", "getTotalPoints: "+totalPoint);
        this.total  = Integer.parseInt(totalPoint);
        txtTotalPoint.setText(Integer.toString(total));
    }

    PopupWindow helpPopup;
    View popupView;
    class helpListener implements View.OnClickListener {
        @Override
        public void onClick(View helpicon) {
            switch (helpicon.getId()) {
                case R.id.help:
                    popupView = getLayoutInflater().inflate(R.layout.help_popup_activity2, null);
                    helpPopup = new PopupWindow(popupView,
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    helpPopup.setAnimationStyle(-1);
                    helpPopup.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                popupView.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }
}
