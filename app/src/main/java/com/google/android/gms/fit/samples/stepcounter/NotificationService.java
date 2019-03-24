package com.google.android.gms.fit.samples.stepcounter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.android.gms.fit.samples.common.logger.Log;
import com.shin.ssr.layout.tab.FitTab;

import androidx.annotation.Nullable;

import static android.app.PendingIntent.getActivity;


public class NotificationService extends Service {

    Handler myHandler = new Handler();
    RemoteViews remoteViews;
    NotificationCompat.Builder builder;
    private static Notification mNotification;

    private int total;
    private static NotificationManager mNotificationManager;
    private static final int NOTIF_ID = 1234;

    /*Runnable runnable = new Runnable(){

        @Override
        public void run() {

            while(true) {

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startForegroundService();
                myHandler.postDelayed(runnable, 1000
                );

            }
        }

    };*/


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Log.d("noti", "Total:" + Integer.toString(total));

        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);


        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "snwodeer_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        Log.d("noti", "after (oncreate) read data total : " + total);


        total = MainActivity.getData();

        startForegroundService();





        /*     mNotificationManager.notify(1,builder.build());*/
        /*myHandler.post(runnable);*/


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("noti", "inside command" + Integer.toString(total));
        startForegroundService();
        getMyActivityNotification("");

        return super.onStartCommand(intent, flags, startId);


    }

    void startForegroundService() {


        Log.d("noti", "inside foreground");
        Intent notificationIntent = new Intent(this, FitTab.class);
        PendingIntent pendingIntent = getActivity(this, 0, notificationIntent, 0);

        builder.setSmallIcon(R.mipmap.ic_ssgpay_launch)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent);
        Log.d("noti", "inside foreground + total " + Integer.toString(total));


        remoteViews.setTextViewText(R.id.notif_content2, Integer.toString(total));
        startForeground(NOTIF_ID, builder.build());


    }

    private Notification getMyActivityNotification(String text) {

        Log.d("noti", "inside get my activity");
        // The PendingIntent to launch our activity if the user selects
        // this notification
        total = MainActivity.getData();
        Intent notificationIntent = new Intent(this, FitTab.class);
        PendingIntent pendingIntent = getActivity(this, 0, notificationIntent, 0);

        remoteViews.setTextViewText(R.id.notif_content2, Integer.toString(total));
        return new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_ssgpay_launch)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent).getNotification();
    }

    private void updateNotification() {
        String text = "Some text that will update the notification";

        Notification notification = getMyActivityNotification(text);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIF_ID, notification);
    }


}
