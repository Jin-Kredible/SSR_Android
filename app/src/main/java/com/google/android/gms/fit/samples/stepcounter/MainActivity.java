
package com.google.android.gms.fit.samples.stepcounter;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fit.samples.backgroundgps.LocationManage;
import com.google.android.gms.fit.samples.backgroundgps.RealService;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.common.logger.LogWrapper;
import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.shin.ssr.vo.LocationVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity   {

  private Intent serviceIntent;
  private LocationVO locationVO = new LocationVO();
  LocationManage locationManage = new LocationManage();
  public static final int NOTIFICATION_ID = 888;

  private NotificationManagerCompat mNotificationManagerCompat;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

  ///////////////////////////////////////////////////////////////////
  private boolean isScanning;
  private MinewBeaconManager mMinewBeaconManager;
  private static final int REQUEST_ENABLE_BT = 2;
  //비콘 관련 변수
  //////////////////////////////////////////////////////////////////
  /**
   * Tracks whether the user requested to add or remove geofences, or to do neither.
   */
  private enum PendingGeofenceTask {
    ADD, REMOVE, NONE
  }

  /**
   * Provides access to the Geofencing API.
   */
  private GeofencingClient mGeofencingClient;

  /**
   * The list of geofences used in this sample.
   */
  private ArrayList<Geofence> mGeofenceList;

  /**
   * Used when requesting to add or remove geofences.
   */
  private PendingIntent mGeofencePendingIntent;

  // Buttons for kicking off the process of adding or removing geofences.
  private Button mAddGeofencesButton;
  private Button mRemoveGeofencesButton;

  private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

  private Button btnFinance, btnPayment, btnLife;
  private ImageView imgMain;
  private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;


  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // This method sets up our custom logger, which will print all log messages to the device
    // screen, as well as to adb logcat.
    initializeLogging();
    Log.d("geo","oncreate");

    initManager();
    initListener();
   // mMinewBeaconManager.startScan();

    /*
    /** 기존의 위치 받아오는 로직 **/
    /////////////////////////////////////////////////////////////
    if (RealService.serviceIntent == null) {
      serviceIntent = new Intent(this, RealService.class);
      startService(serviceIntent);
    } else {
      serviceIntent = RealService.serviceIntent;//getInstance().getApplication();
      Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
    }//백그라운드 실행
    ////////////////////////////////////////////////////////////////
    final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    locationManage.onLocation(lm);
    locationVO =locationManage.getVoData(); //gps 위치 받아오기
    Log.d("geo", "Long" + locationManage.getVoData().getLongitude() + " " + locationManage.getVoData().getLatitude());
    /////////////////////////////////////////////////////////////////


  btnFinance = findViewById(R.id.finance);
  btnPayment = findViewById(R.id.payment);
  btnLife = findViewById(R.id.lifestyle);
  imgMain = findViewById(R.id.imgMain);

  int getButtonNum = getIntent().getIntExtra("buttonNum",2);
  switch (getButtonNum){
    case 1: btnFinance.callOnClick();  break;
    case 2: btnPayment.callOnClick();  break;
    case 3: btnLife.callOnClick();     break;
  }

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
      android.util.Log.d("fit","in Fitness regist1");
    } else {
      android.util.Log.d("fit","in Fitness regist2");
      subscribe();

    }



  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    Log.d("fit", "in activity result");
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
        subscribe();
      }
    }
  }
  public void subscribe() {
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
                          /*Log.i(TAG, "Successfully subscribed!");*/
                        } else {
                          Log.w("fit", "There was a problem subscribing.", task.getException());
                        }
                        Log.d("readdata","before read data - in subscribe");

                      }
                    });
  }
  //////////////////////////////////////////////////////////////////////////////////
  private void initManager() {
    mMinewBeaconManager = MinewBeaconManager.getInstance(this);
  } // 비콘사용 초기화

  private void initListener() {

        if (mMinewBeaconManager != null) {
          BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
          switch (bluetoothState) {
            case BluetoothStateNotSupported:
              Toast.makeText(MainActivity.this, "Not Support BLE", Toast.LENGTH_SHORT).show();
              finish();
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
    mMinewBeaconManager.startScan();

    mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {
      /**
       *   새로운 비컨을 발견하면 메소드를 다시 호출
       *
       *  관리자가 스캔 한 @param minewBeacons 새로운 비컨
       */
      @Override
      public void onAppearBeacons(List<MinewBeacon> minewBeacons) {

        for (MinewBeacon minewBeacon : minewBeacons) {
          String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();


          if(deviceName.equals("MiniBeacon_21907")) {
            Log.d("beacon1", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : ");
            Log.d("beacon1", ">>>>Name : " + deviceName);
            Log.d("beacon1", ">>>>UUID : " + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_UUID).getStringValue());
            Log.d("beacon1", ">>>>Major : " + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Major).getStringValue());
            Log.d("beacon1", ">>>>Minor : " + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Minor).getStringValue());
            Log.d("beacon1", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : ");
            mMinewBeaconManager.stopScan();
          }
          Log.d("beacon1", "굿" + minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue());
        }

        //String stringValue = minewBeacons.get(0).getBeaconValue.getStringValue;
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
       /* runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Collections.sort(minewBeacons, comp);
            android.util.Log.e("tag", state + "");
            if (state == 1 || state == 2) {
            } else {
              mAdapter.setItems(minewBeacons);
            }

          }
        });*/
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
        finish();
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
    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
  }
  // 비콘 제어 부
  /////////////////////////////////////////////////////////////////////////////////////





  @Override
  public void onStart() {
    Log.d("geo","onStart");
    super.onStart();
  }



  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (serviceIntent!=null) {
      stopService(serviceIntent);
      serviceIntent = null;
    }
  }//서비스를 사용하기 위함


  protected  void onResume(){
    super.onResume();
    SystemRequirementsChecker.checkWithDefaultDialogs(this);//블루투스 권한 및 활성화
  }

  protected void onPause(){
    super.onPause();

  }

  public void sendToFinance(View view) {
    imgMain.setBackgroundResource(R.drawable.ssg_finance2);
    ButtonReset();
    btnFinance.setBackgroundResource(R.drawable.tap_bg_on);
    btnFinance.setTextColor(Color.parseColor("#d94d32"));
  }
  public void sendToPay(View view) {
    imgMain.setBackgroundResource(R.drawable.ssg_payment2);
    ButtonReset();
    btnPayment.setBackgroundResource(R.drawable.tap_bg_on);
    btnPayment.setTextColor(Color.parseColor("#d94d32"));
  }
  public void sendToLife(View view) {
    imgMain.setBackgroundResource(R.drawable.ssg_lifestyle2);
    ButtonReset();
    btnLife.setBackgroundResource(R.drawable.tap_bg_on);
    btnLife.setTextColor(Color.parseColor("#d94d32"));
  }

  public void sendToFit(View view) {
    Intent intent = new Intent(MainActivity.this, FitTab.class);
    startActivity(intent);
    finish();
  }

  public void ButtonReset(){
    btnFinance.setBackgroundResource(R.drawable.tap_bg_off);
    btnPayment.setBackgroundResource(R.drawable.tap_bg_off);
    btnLife.setBackgroundResource(R.drawable.tap_bg_off);
    btnFinance.setTextColor(Color.parseColor("#000000"));
    btnPayment.setTextColor(Color.parseColor("#000000"));
    btnLife.setTextColor(Color.parseColor("#000000"));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //return super.onCreateOptionsMenu(menu);
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.menu, menu);
    return true;
  }

  //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  /*@Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    android.util.Log.i("geo", "onRequestPermissionResult");
    if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length <= 0) {
        // If user interaction was interrupted, the permission request is cancelled and you
        // receive empty arrays.
        android.util.Log.i("geo", "User interaction was cancelled.");
      } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        android.util.Log.i("geo", "Permission granted.");
        performPendingGeofenceTask();
      } else {
        // Permission denied.

        // Notify the user via a SnackBar that they have rejected a core permission for the
        // app, which makes the Activity useless. In a real app, core permissions would
        // typically be best requested during a welcome-screen flow.

        // Additionally, it is important to remember that a permission might have been
        // rejected without asking the user for permission (device policy or "Never ask
        // again" prompts). Therefore, a user interface affordance is typically implemented
        // when permissions are denied. Otherwise, your app could appear unresponsive to
        // touches or interactions which have required permissions.
        showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    // Build intent that displays the App settings screen.
                    Intent intent = new Intent();
                    intent.setAction(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                  }
                });
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
      }
    }
  }*/

  public void generateBigPictureStyleNotification() {

    android.util.Log.d("geo", "generateBigPictureStyleNotification()");


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





  /** Records step data by requesting a subscription to background step data. */


  /**
   * Reads the current daily step total, computed from midnight of the current day on the device's
   * current timezone.
   */


/*  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the main; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }*/

 /* @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_read_data) {
      readData();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }*/

  /** Initializes a custom log class that outputs both to in-app targets and logcat. */
  @RequiresApi(api = Build.VERSION_CODES.M)
  private void initializeLogging() {
    // Wraps Android's native log framework.
    LogWrapper logWrapper = new LogWrapper();
    // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
    Log.setLogNode(logWrapper);
    // Filter strips out everything except the message text.
    MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
    logWrapper.setNext(msgFilter);
    // On screen logging via a customized TextView.
   /* LogView logView = (LogView) findViewById(R.id.sample_logview);

    // Fixing this lint error adds logic without benefit.
    // noinspection AndroidLintDeprecation
    logView.setTextAppearance(R.style.Log);

    logView.setBackgroundColor(Color.WHITE);
    msgFilter.setNext(logView);*/
    /*Log.i(TAG, "Ready");*/
  }






}
