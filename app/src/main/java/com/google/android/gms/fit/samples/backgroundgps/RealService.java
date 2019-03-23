package com.google.android.gms.fit.samples.backgroundgps;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.recognition.packets.Beacon;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fit.samples.stepcounter.MainActivity;
import com.google.android.gms.fit.samples.stepcounter.R;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.shin.ssr.vo.LocationVO;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.shin.ssr.layout.tab.FitTab.SERVER_URL;


public class RealService  extends Service {
    public static Thread mainThread;
    public static Intent serviceIntent = null;
    LocationManage locationManage = new LocationManage();
    private LocationVO locationVO = new LocationVO();
    public static final int NOTIFICATION_ID = 888;

    private NotificationManagerCompat mNotificationManagerCompat;
    private ArrayList<MallsVO> mTemp = new ArrayList<>();
    private double distance;
    private int result2;
    private Context context;
    public static boolean insideMall = false;


    ///////////////////////////////////////////////////////////////////
    private boolean isScanning = false; // 방문 완료
    private MinewBeaconManager mMinewBeaconManager;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isBeaconOn = false;     // 비콘 검색 기능 활성화
    private int mall_ID = 1000;
    private boolean isBeaconSerUpdate = false; //서버에 mall_id를 전송해 비콘 정보를 받아옴 체크
    Date date = new Date();
    SimpleDateFormat get_Day = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private String vi_Start;
    private String vi_End;
    ArrayList<MallsVO> bCon_List = new ArrayList<MallsVO>();
    private boolean isGpsOn = false;
    private String mall_Name = new String(); //방문 매장이름
    private int vi_WalkStart; // 매장방문, 걸음 체크
    private int vi_WalkEnd; // 매장방문 걸음 종료 체크
    private int mWalk_check; // 걸음수 체크
    //비콘 관련 변수
    //////////////////////////////////////////////////////////////////

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        showToast(getApplication(), "Start Service");
        mTemp.add(new MallsVO("이마트 청계천점", 37.571079, 127.029903));
        mTemp.add(new MallsVO("이마트 성수점", 37.539673, 127.053375));
        mTemp.add(new MallsVO("이마트 용산점", 37.529456, 126.965545));
        mTemp.add(new MallsVO("이마트 아이앤씨점", 37.559805, 126.983122));
        mTemp.add(new MallsVO("이마트 SD아카데미점", 37.502366, 127.023680));

        Log.d("real", "RealService start");
        Log.d("real", Integer.toString(result2));

        mainThread = new Thread(new Runnable() {


            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                // SimpleDateFormat sdf = new SimpleDateFormat("aa hh:mm");
                boolean run = true;

                while (run) {

                    if (Thread.interrupted()) {
                        break;
                    }
                    try {
                        Thread.sleep(1000 * 5); // 1 minute
                        HttpUtil_GPS hu = new HttpUtil_GPS(RealService.this);

                        String[] params = {SERVER_URL + "checkPush.do", "user_id:" + 1, "dummy2:" + 1};

                        hu.execute(params);
                        String pushyn;

                        if (isBeaconOn == true) {
                            initListener();
                            HttpUtil_Beacon be = new HttpUtil_Beacon(RealService.this);
                            readData(); // 걸음수 데이터 불러오기
                            if (!isBeaconSerUpdate) {
                                mMinewBeaconManager.startScan();
                                String[] params2 = {SERVER_URL + "beaconInfo.do", "mall_id:" + mall_ID, "uuid:" + 1}; // visit.do에 mall_ID를 전송시켜준다.
                                be.execute(params2);
                                isBeaconSerUpdate = true;
                            }
                        }


                        try {

                            pushyn = hu.get();

                            Log.d("real", "pushyn : " + Integer.parseInt(pushyn) + "result2" + Integer.toString(result2));


                            locationVO = locationManage.getVoData();

                          /*  showToast(getApplication(), Double.toString(locationVO.getLongitude()) + " , " + Double.toString(locationVO.getLatitude()) + " , " + locationVO.getProvider());*/
                            Location location = new Location(locationVO.getProvider());

                            Log.d("geo", "loc vo long" + locationVO.getLongitude());
                            Log.d("geo", "loc vo lat" + locationVO.getLatitude());

                            location.setLatitude(locationVO.getLatitude());
                            location.setLongitude(locationVO.getLongitude());

                            Location locationPoint = new Location("Mall");

                            if (isGpsOn == true && location.getProvider() != null) {
                                Log.d("gps2", ">>>>>>>> GpsOn 입장");
                                Log.d("gps2", "if문 들어가기전 이름 : " + location.getProvider());
                                readData(); // 걸음수 데이터 불러오기
                                if (location.getProvider().equals("gps")) {
                                    Log.d("gps2", "if문 진입 : " + location.getProvider());
                                    isGpsOn = false;
                                    vi_End = get_Day.format(date); //매장 방문 시간
                                    /*HttpUtil_BeaconUpdate becon_update = new HttpUtil_BeaconUpdate(RealService.this);
                                    String[] params3 = {SERVER_URL+"visit.do", "mall_id:" + mall_ID, "user_id:" + 1, "vi_start:" + 1, "vi_end:" + 1} ;
                                    becon_update.execute(params3);*/ // 지우면 안됨!! 매장퇴장시 유저 정보 넘겨주는 부분
                                    vi_WalkEnd = getWalkCheck(); // 끝났을때 걸음수 체크
                                    showToast(getApplication(), "감사합니다 다음에 또 방문해주세요!" + vi_WalkStart + "/" + vi_WalkEnd);
                                    Log.d("beacon1", "방문 끝 워크 체크 " + vi_WalkStart + " / " + vi_WalkEnd);
                                } else {
                                    Log.d("gps2", "gps 이름" + location.getProvider());
                                    showToast(getApplication(), "네트워크 접속완료");
                                }
                            }//매장 퇴장 체크

                            if (!isScanning && !isBeaconOn) { // 사용자가 매장에 방문하지 않았을때 사용
                                Log.d("gps2", ">>>>>> 점포 스캐닝 입장");
                               /* showToast(getApplication(), "점포 스캐닝 입장");*/
                                for (int i = 0; i < mTemp.size(); i++) {
                                    locationPoint.setLatitude(mTemp.get(i).getMall_la());
                                    locationPoint.setLongitude(mTemp.get(i).getMall_long());
                                    distance = locationPoint.distanceTo(location);
                                    Log.d("geo", Double.toString(distance));

                                    if (Integer.parseInt(pushyn) == 1) {
                                        // 지점과 지금 거리가 100m 이내일떄
                                        if (distance < 100) {

                                            showToast(getApplication(), "100미터 이내 입장!");
                                            Log.d("geo", "inside distance for loop");
                                            mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                            StepVO vo = new StepVO();
                                            vo.setAge(20);
                                            vo.setGender(1);
                                            vo.setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                                            generateBigPictureStyleNotification(vo);
                                            isBeaconOn = true; // 비콘 켜준다.

                                            HttpUtil_PUSH hu2 = new HttpUtil_PUSH(RealService.this);

                                            String[] params2 = {SERVER_URL + "updatePush.do", "user_id:" + 1, "dummy2:" + 1};

                                            hu2.execute(params2);
                                            Log.d("beacon1", ">>>>>>>>>>> 100m 진입 비콘 검색을 실행합니다.");
                                        }

                                        Log.d("mall", mTemp.get(i).getMall_nm());
                                        Log.d("mall", "현재 위도 " + location.getLatitude());
                                        Log.d("mall", "현재 경도 " + location.getLongitude());
                                        Log.d("mall", "성수점 위도 " + mTemp.get(i).getMall_la());
                                        Log.d("mall", "성수점 경도 " + mTemp.get(i).getMall_long());
                                        Log.d("mall", "위치 : " + distance);
                                    }
                                }

                            }


                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            Log.d("pointy", "inside interrupted Exception of real service");
                            e.printStackTrace();
                        }

                        //////////////http connection
                        /*  String SERVER_URL="http://192.168.219.108:8088/product.do"; // 서버 주소*/

                    } catch (InterruptedException e) {
                        Log.d("pointy", "inside interrupted Exception of real service");
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
        initManager(); // 비콘 매니저 초기화
        initListener(); //비콘 매니저 실행
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
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }

    public void generateBigPictureStyleNotification(StepVO vo) {

        String gender = null;
        String time = null;
        if (vo.getGender() == 1) {
            gender = "남성";
        } else {
            gender = "여성";
        }

        if (vo.getTime() > 7 && vo.getTime() <= 10) {

            time = "아침";
        } else if (vo.getTime() > 10 && vo.getTime() <= 13) {
            time = "나른한 점심";
        } else if (vo.getTime() > 13 && vo.getTime() <= 17) {
            time = "오후";
        } else if (vo.getTime() > 17 && vo.getTime() <= 20) {
            time = "저녁";
        } else {
            time = "야심한 밤";
        }


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
                .setBigContentTitle("100M 내에 위치한 EMART에서 지금 GET")
                // Summary line after the detail section in the big form of the template.
                .setSummaryText(time + "에 " + vo.getAge() + "대 " + gender + "에게 추천하는 이마트 제품");

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
                .setContentTitle("↓↓↓↓ 근처 EMART 추천 제품")
                // Content for API <24 (7.0 and below) devices.
                .setContentText(time + "에 " + vo.getAge() + "대 " + gender + "에게 추천하는 이마트 제품")
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
        Log.d("push", Integer.toString(pushYN));
        result2 = pushYN;
    }

    //////////////////////////////////////////////////////////////////////////////////
    private void initManager() {
        Log.d("beacon1", "이닛 매니저 입장!!");
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);


    } // 비콘사용 초기화

    private void initListener() {
        Log.d("beacon1", "이닛 리스너 입장!!");
        if (mMinewBeaconManager != null) {
            BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
            switch (bluetoothState) {
                case BluetoothStateNotSupported:

                    break;
                case BluetoothStatePowerOff:
                    showBLEDialog();
                    return;
                case BluetoothStatePowerOn:
                    break;
            }
        }


       /* if (isScanning) {
          isScanning = false;
          if (mMinewBeaconManager != null) {
            mMinewBeaconManager.stopScan();
          }
        } else {
          isScanning = true;
          try {
            mMinewBeaconManager.startScan();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }//수정 요망블투 버튼으로 끄고 켰을때 사용되는 부분*/

        mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {
            /**
             *   새로운 비컨을 발견하면 메소드를 다시 호출
             *
             *  관리자가 스캔 한 @param minewBeacons 새로운 비컨
             */
            @Override
            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {
                if (!isScanning) {
                    Log.d("beacon1", "onAppearBeacons 입장!");
                    Log.d("beacon1", ">>>>>>>>>>>>>>>>>>>>>>>onRangeBeacons for문 입장!");
                    Log.d("beacon1", "비콘 리스트 사이즈 : " + bCon_List.size());

                    for (int i = 0; i < bCon_List.size(); i++) {
                        Log.d("beacon1", " ");
                        Log.d("beacon1", ">>>>>>>>>>>>>>>>>>>> " + i + "번째 for문");
                                    Log.d("beacon1", "서버정보 : " + "UUID - " + bCon_List.get(i).getUuid() + " Major - " + Integer.toString(bCon_List.get(i).getMajor()) + " minor - " + Integer.toString(bCon_List.get(i).getMinor()));
                            Log.d("beacon1", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                            readData(); // 걸음수 데이터 불러오기
                            for (MinewBeacon minewBeacon : minewBeacons) {
                                String deviceUUID = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_UUID).getStringValue();
                                String deviceMajor = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Major).getStringValue();
                                String deviceMinor = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Minor).getStringValue();
                                String serverUUID = bCon_List.get(i).getUuid();
                                String serverMajor = Integer.toString(bCon_List.get(i).getMajor());
                                String serverMinor = Integer.toString(bCon_List.get(i).getMinor());
                            vi_WalkStart = getWalkCheck(); // 걸음수 받아오기
                            Log.d("beacon1", "검색정보 : " + "UUID - " + deviceUUID + " Major - " + deviceMajor + " minor - " + deviceMinor);
                            if (deviceUUID != null && deviceMajor != null && deviceMinor != null && serverUUID != null && serverMajor != null && serverMinor != null &&
                                    deviceUUID.equals(serverUUID) && deviceMajor.equals(serverMajor) && deviceMinor.equals(serverMinor)) {
                                Log.d("beacon1", "MINOR 통과");
                                showToast(getApplication(),   "매장방문을 환영합니다!");
                                com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : ");
                                com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>Name : " + deviceUUID);
                                com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>UUID : " + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_UUID).getStringValue());
                                com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>Major : " + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Major).getStringValue());
                                com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>Minor : " + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Minor).getStringValue());
                                com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : ");
                                mMinewBeaconManager.stopScan();
                                isScanning = true; // 매장 방문 체크
                                isBeaconOn = false; // 비콘 off
                                i = bCon_List.size(); // for문 종료
                                vi_Start = get_Day.format(date); //매장 방문 시간
                               /* HttpUtil_BeaconUpdate becon_update = new HttpUtil_BeaconUpdate(RealService.this);
                                String[] params = {SERVER_URL + "visit.do", "mall_id:" + mall_ID, "user_id:" + 1, "vi_start:" + 1, "vi_end:" + 1};
                                becon_update.execute(params);*/
                                isGpsOn = true; //매장 방문시 Gps를 잡아 매장을 나간것을 체크해주기 위함
                                vi_WalkStart = getWalkCheck(); //매장 방문했을때 걸음 수


                                insideMall= true;
                               /* LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                                View view = inflater.inflate(R.layout.fit_tab_activity,null);


                              ImageView button =  view.findViewById(R.id.mission_day);
                                Log.d("beacon1","view button" + button.toString());

                                *//*Button buton = (Button) ((FitTab)context).findViewById(R.id.button3);*//*
                                button.setBackgroundResource(R.drawable.cart_y);
                                button.setImageResource(R.drawable.cart_y);*/



                                break;
                            }
                        }
                    }
                }
            }


            /**
             *   * 신호가 10 초 이내에 데이터를 업데이트하지 않으면이 신호가 울리지 않았다고 관리자가이 방법을 다시 호출합니다.
             *     * @param minewBeacons 비컨 범위를 벗어났습니다.
             */
            @Override
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                /*for (MinewBeacon minewBeacon : minewBeacons) {
                    String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    Toast.makeText(getApplicationContext(), deviceName + "  out range", Toast.LENGTH_SHORT).show();
                }*/
            }

            /**
             *    * 관리자가 1 초마다이 방법을 호출하면 모든 스캔 된 신호를 얻을 수 있습니다.
             * @param minewBeacons 모든 스캔 된 비컨
             */
            @Override
            public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
               /* for (MinewBeacon minewBeacon : minewBeacons) {
                    String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();


                    if(deviceName.equals("MiniBeacon_21907")) {
                        com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : ");
                        com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>Name : " + deviceName);
                        com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>UUID : " + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_UUID).getStringValue());
                        com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>Major : " + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Major).getStringValue());
                        com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>Minor : " + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Minor).getStringValue());
                        com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : ");
                        //  mMinewBeaconManager.stopScan();
                    }
                    com.google.android.gms.fit.samples.common.logger.Log.d("beacon1", "굿" + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue());
                }*/
            }

            /**
             *   * 관리자가 BluetoothStateChanged를 호출 할 때이 메소드를 다시 호출합니다.
             *              *
             *
             *  @param state BluetoothState
             */
            @Override
            public void onUpdateState(BluetoothState state) {
                switch (state) {
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


    /**
     * 블루투스 상태 확인
     */
    private void checkBluetooth() {
        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                //  finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                break;
        }
    }

    private void showBLEDialog() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }
    // 비콘 제어 부
    /////////////////////////////////////////////////////////////////////////////////////

    public void get_BaeconList(ArrayList<MallsVO> result) {

        for (int i = 0; i < result.size(); i++) {
            bCon_List.add(result.get(i));

        }

    }// beacon을 수신 받는

    private void readData() {
        com.google.android.gms.fit.samples.common.logger.Log.d("fit", "in readdata");

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {

                            @Override
                            public void onSuccess(DataSet dataSet) {
                                mWalk_check =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();


                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
    }

    public int getWalkCheck() {
        return mWalk_check;
    }
    /*public boolean getInsideMall() { return insideMall;}*/
}

