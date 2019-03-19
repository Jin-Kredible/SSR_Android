package com.google.android.gms.fit.samples.backgroundgps;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.fit.samples.stepcounter.MainActivity;
import com.google.android.gms.fit.samples.stepcounter.R;
import com.shin.ssr.layout.notification.GlobalNotificationBuilder;
import com.shin.ssr.layout.notification.handlers.BigPictureSocialIntentService;
import com.shin.ssr.layout.notification.handlers.BigPictureSocialMainActivity;
import com.shin.ssr.layout.notification.handlers.MockDatabase;
import com.shin.ssr.layout.notification.handlers.NotificationUtil;
import com.shin.ssr.layout.tab.FitTab;
import com.shin.ssr.layout.tab.HttpUtil;
import com.shin.ssr.vo.LocationVO;
import com.shin.ssr.vo.MallsVO;
import com.shin.ssr.vo.ProductVO;
import com.shin.ssr.vo.StepVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import com.shin.ssr.vo.LocationVO;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.shin.ssr.layout.tab.FitTab.SERVER_URL;


public class RealService  extends Service {
    private Thread mainThread;
    public static Intent serviceIntent = null;
    LocationManage locationManage = new LocationManage();
    private LocationVO locationVO = new LocationVO();
    public static final int NOTIFICATION_ID = 888;

    private NotificationManagerCompat mNotificationManagerCompat;
    private ArrayList<MallsVO> mTemp = new ArrayList<>();
    private double distance;
    private int result2;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        showToast(getApplication(), "Start Service");
        mTemp.add(new MallsVO("이마트 청계천점",37.571079,127.029903));
        mTemp.add(new MallsVO("이마트 성수점", 37.539673, 127.053375));
        mTemp.add(new MallsVO("이마트 용산점", 37.529456, 126.965545));
        mTemp.add(new MallsVO("이마트 아이앤씨점", 37.559805, 126.983122));

        mainThread = new Thread(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
               // SimpleDateFormat sdf = new SimpleDateFormat("aa hh:mm");

                boolean run = true;
                while (run) {
                    try {
                        Thread.sleep(1000 * 10); // 1 minute
                        HttpUtil_GPS hu = new HttpUtil_GPS(RealService.this);

                        String[] params = {SERVER_URL+"checkPush.do", "dummy1:" + 1, "dummy2:" + 1} ;

                        hu.execute(params);


                        try {

                            hu.get();
                            if(result2==0) {

                                locationVO = locationManage.getVoData();
                                showToast(getApplication(), Double.toString(locationVO.getLongitude()) + " , " + Double.toString(locationVO.getLatitude()) + " , " + locationVO.getProvider());
                                Location location = new Location(locationVO.getProvider());
                                Log.d("geo", "loc vo long" + locationVO.getLongitude());
                                Log.d("geo", "loc vo lat" + locationVO.getLatitude());

                                location.setLatitude(locationVO.getLatitude());
                                location.setLongitude(locationVO.getLongitude());

                                Location locationPoint = new Location("Mall");

                                for (int i = 0; i < mTemp.size(); i++) {
                                    locationPoint.setLatitude(mTemp.get(i).getMall_la());
                                    locationPoint.setLongitude(mTemp.get(i).getMall_long());
                                    distance = locationPoint.distanceTo(location);
                                    Log.d("geo", Double.toString(distance));

                                    // 지점과 지금 거리가 100m 이내일떄
                                    if (distance < 100) {
                                        Log.d("geo", "inside distance for loop");
                                        mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                        generateBigPictureStyleNotification();
                                    }

                                    Log.d("mall", mTemp.get(i).getMall_nm());
                                    Log.d("mall", "현재 위도 " + location.getLatitude());
                                    Log.d("mall", "현재 경도 " + location.getLongitude());
                                    Log.d("mall", "성수점 위도 " + mTemp.get(i).getMall_la());
                                    Log.d("mall", "성수점 경도 " + mTemp.get(i).getMall_long());
                                    Log.d("mall", "위치 : " + distance);
                                }
                            }




                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //////////////http connection
                         /*  String SERVER_URL="http://192.168.219.108:8088/product.do"; // 서버 주소*/

                    } catch (InterruptedException e) {
                        run = false;
                        e.printStackTrace();
                    }

                    Log.d("RealSerVo", "경도 : " + locationVO.getLongitude());
                    Log.d("RealSerVo", "위도 : " + locationVO.getLatitude());
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

    public void generateBigPictureStyleNotification() {

        android.util.Log.d("geo", "generateBigPictureStyleNotification()");

        // Main steps for building a BIG_PICTURE_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the BIG_PICTURE_STYLE
        //      3. Set up main Intent for notification
        //      4. Set up RemoteInput, so users can input (keyboard and voice) from notification
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        MockDatabase.BigPictureStyleSocialAppData bigPictureStyleSocialAppData =
                MockDatabase.getBigPictureStyleData();

        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).

        String notificationChannelId =
                NotificationUtil.createNotificationChannel(this, bigPictureStyleSocialAppData);

        // 2. Build the BIG_PICTURE_STYLE.
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                // Provides the bitmap for the BigPicture notification.
                .bigPicture(
                        BitmapFactory.decodeResource(
                                getResources(),
                                bigPictureStyleSocialAppData.getBigImage()))
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle(bigPictureStyleSocialAppData.getBigContentTitle())
                // Summary line after the detail section in the big form of the template.
                .setSummaryText(bigPictureStyleSocialAppData.getSummaryText());

        // 3. Set up main Intent for notification.
        Intent mainIntent = new Intent(this, BigPictureSocialMainActivity.class);

        // When creating your Intent, you need to take into account the back state, i.e., what
        // happens after your Activity launches and the user presses the back button.

        // There are two options:
        //      1. Regular activity - You're starting an Activity that's part of the application's
        //      normal workflow.

        //      2. Special activity - The user only sees this Activity if it's started from a
        //      notification. In a sense, the Activity extends the notification by providing
        //      information that would be hard to display in the notification itself.

        // Even though this sample's MainActivity doesn't link to the Activity this Notification
        // launches directly, i.e., it isn't part of the normal workflow, a social app generally
        // always links to individual posts as part of the app flow, so we will follow option 1.

        // For an example of option 2, check out the BIG_TEXT_STYLE example.

        // For more information, check out our dev article:
        // https://developer.android.com/training/notify-user/navigation.html

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack.
        stackBuilder.addParentStack(BigPictureSocialMainActivity.class);
        // Adds the Intent to the top of the stack.
        stackBuilder.addNextIntent(mainIntent);
        // Gets a PendingIntent containing the entire back stack.
        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // 4. Set up RemoteInput, so users can input (keyboard and voice) from notification.

        // Note: For API <24 (M and below) we need to use an Activity, so the lock-screen presents
        // the auth challenge. For API 24+ (N and above), we use a Service (could be a
        // BroadcastReceiver), so the user can input from Notification or lock-screen (they have
        // choice to allow) without leaving the notification.

        // Create the RemoteInput.
        String replyLabel = getString(R.string.reply_label);
        RemoteInput remoteInput =
                new RemoteInput.Builder(BigPictureSocialIntentService.EXTRA_COMMENT)
                        .setLabel(replyLabel)
                        // List of quick response choices for any wearables paired with the phone
                        .setChoices(bigPictureStyleSocialAppData.getPossiblePostResponses())
                        .build();

        // Pending intent =
        //      API <24 (M and below): activity so the lock-screen presents the auth challenge
        //      API 24+ (N and above): this should be a Service or BroadcastReceiver
        PendingIntent replyActionPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(this, BigPictureSocialIntentService.class);
            intent.setAction(BigPictureSocialIntentService.ACTION_COMMENT);
            replyActionPendingIntent = PendingIntent.getService(this, 0, intent, 0);

        } else {
            replyActionPendingIntent = mainPendingIntent;
        }

        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply_white_18dp,
                        replyLabel,
                        replyActionPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // 5. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. Later, we use the same global builder to get back the notification
        // we built here for a comment on the post.

        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(getApplicationContext(), notificationChannelId);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);

        notificationCompatBuilder
                // BIG_PICTURE_STYLE sets title and content for API 16 (4.1 and after).
                .setStyle(bigPictureStyle)
                // Title for API <16 (4.0 and below) devices.
                .setContentTitle(bigPictureStyleSocialAppData.getContentTitle())
                // Content for API <24 (7.0 and below) devices.
                .setContentText(bigPictureStyleSocialAppData.getContentText())
                .setSmallIcon(R.drawable.ssgpaylogo2)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.ssg_pay_icon))
                .setContentIntent(mainPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)

                .setSubText(Integer.toString(1))
                .addAction(replyAction)
                .setCategory(Notification.CATEGORY_SOCIAL)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(bigPictureStyleSocialAppData.getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(bigPictureStyleSocialAppData.getChannelLockscreenVisibility());

        // If the phone is in "Do not disturb mode, the user will still be notified if
        // the sender(s) is starred as a favorite.
        for (String name : bigPictureStyleSocialAppData.getParticipants()) {
            notificationCompatBuilder.addPerson(name);
        }

        Notification notification = notificationCompatBuilder.build();

        mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    public void checkPush(int pushYN) {
        Log.d("push",Integer.toString(pushYN));
        result2 = pushYN;
    }

}
