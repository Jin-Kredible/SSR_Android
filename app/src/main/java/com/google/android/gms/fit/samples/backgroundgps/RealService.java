package com.google.android.gms.fit.samples.backgroundgps;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.shin.ssr.vo.LocationVO;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RealService  extends Service {
    private Thread mainThread;
    public static Intent serviceIntent = null;
    LocationManage locationManage = new LocationManage();
    private LocationVO locationVO = new LocationVO();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        showToast(getApplication(), "Start Service");


        mainThread = new Thread(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("aa hh:mm");

                boolean run = true;
                while (run) {
                    try {
                        Thread.sleep(1000 * 1 * 1); // 1 minute
                        locationVO = locationManage.getVoData();
                        showToast(getApplication(),Double.toString(locationVO.getLongitude())+" , "+Double.toString(locationVO.getLatitude()) + " , " + locationVO.getProvider());
                        Log.d("RealSerVo", "경도 : " + locationVO.getLongitude());
                        Log.d("RealSerVo", "위도 : " + locationVO.getLatitude());
                    } catch (InterruptedException e) {
                        run = false;
                        e.printStackTrace();
                    }
                }
            }
        });
        mainThread.start();

        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceIntent = null;
        setAlarmTimer();
        Thread.currentThread().interrupt();

        if (mainThread != null) {
            mainThread.interrupt();
            mainThread = null;
        }
    }

    @Override
    public void onCreate() {

        super.onCreate();
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //백그라운드에서 gps 실행
        locationManage.onLocation(lm); // gps 실행
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void showToast(final Application application, final String msg) {
        Handler h = new Handler(application.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(application, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, AlarmRecever.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }

}
