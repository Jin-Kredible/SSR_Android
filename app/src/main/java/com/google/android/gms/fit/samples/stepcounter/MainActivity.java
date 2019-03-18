/*
 * Copyright (C) 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.fit.samples.stepcounter;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.fit.samples.backgroundgps.LocationManage;
import com.google.android.gms.fit.samples.backgroundgps.RealService;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.common.logger.LogWrapper;
import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shin.ssr.layout.notification.GlobalNotificationBuilder;
import com.shin.ssr.layout.notification.handlers.BigPictureSocialIntentService;
import com.shin.ssr.layout.notification.handlers.BigPictureSocialMainActivity;
import com.shin.ssr.layout.notification.handlers.MockDatabase;
import com.shin.ssr.layout.notification.handlers.NotificationUtil;
import com.shin.ssr.layout.tab.FitTab;
import com.shin.ssr.vo.LocationVO;

import java.util.ArrayList;
import java.util.Map;



public class MainActivity extends AppCompatActivity   {

  private Intent serviceIntent;
  private LocationVO locationVO = new LocationVO();
  LocationManage locationManage = new LocationManage();
  public static final int NOTIFICATION_ID = 888;

  private NotificationManagerCompat mNotificationManagerCompat;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

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


  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // This method sets up our custom logger, which will print all log messages to the device
    // screen, as well as to adb logcat.
    initializeLogging();
    Log.d("geo","oncreate");

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
/*
    // Get the UI widgets.
    mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
    mRemoveGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);

    // Empty list for storing geofences.
    mGeofenceList = new ArrayList<>();

    // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
    mGeofencePendingIntent = null;

    setButtonsEnabledState();

    // Get the geofences used. Geofence data is hard coded in this sample.
    populateGeofenceList();

    mGeofencingClient = LocationServices.getGeofencingClient(this);


    Log.d("geo","inside oncreate result requesting permission");
    if (!checkPermissions()) {
      mPendingGeofenceTask = PendingGeofenceTask.ADD;
      requestPermissions();
      return;
    }
    Log.d("geo","inside oncreate result requesting permission2");
    addGeofences();*/


    locationManage.onLocation(lm);
    locationVO =locationManage.getVoData(); //gps 위치 받아오기
    /////////////////////////////////////////////////////////////////*/

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



  }

 /* private void populateGeofenceList() {
    Log.d("geo","populateGeofenceList");
    for (Map.Entry<String, LatLng> entry : Constants.Emart_Loc.entrySet()) {

      mGeofenceList.add(new Geofence.Builder()
              // Set the request ID of the geofence. This is a string to identify this
              // geofence.
              .setRequestId(entry.getKey())

              // Set the circular region of this geofence.
              .setCircularRegion(
                      entry.getValue().latitude,
                      entry.getValue().longitude,
                      Constants.GEOFENCE_RADIUS_IN_METERS
              )

              // Set the expiration duration of the geofence. This geofence gets automatically
              // removed after this period of time.
              .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

              // Set the transition types of interest. Alerts are only generated for these
              // transition. We track entry and exit transitions in this sample.
              .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                      Geofence.GEOFENCE_TRANSITION_EXIT)

              // Create the geofence.
              .build());
    }


  }
  public void addGeofencesButtonHandler(View view) {

    android.util.Log.d("geo","addGeofencesButtonHandler");
    if (!checkPermissions()) {
      mPendingGeofenceTask = PendingGeofenceTask.ADD;
      requestPermissions();
      return;
    }
    addGeofences();
  }*/



  @Override
  public void onStart() {
    Log.d("geo","onStart");
    super.onStart();


 /*   if (!checkPermissions()) {
      requestPermissions();
    } else {
      performPendingGeofenceTask();
    }*/
  }
/*
  @SuppressWarnings("MissingPermission")
  private void addGeofences() {
    Log.d("geo","addGeofences");
    if (!checkPermissions()) {
      showSnackbar("insufficient permission");
      return;
    }


    mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
            .addOnCompleteListener(this);
  }

  @SuppressWarnings("MissingPermission")
  private void removeGeofences() {

    Log.d("geo","removing geofence");
    if (!checkPermissions()) {
      showSnackbar(getString(R.string.insufficient_permissions));
      return;
    }

    mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
  }

  public void removeGeofencesButtonHandler(View view) {
    if (!checkPermissions()) {
      mPendingGeofenceTask = PendingGeofenceTask.REMOVE;
      requestPermissions();
      return;
    }
    removeGeofences();
  }

  private void showSnackbar(final String text) {
    View container = findViewById(android.R.id.content);
    if (container != null) {
      Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
    }
  }

  private void showSnackbar(final int mainTextStringId, final int actionStringId,
                            View.OnClickListener listener) {
    Snackbar.make(
            findViewById(android.R.id.content),
            getString(mainTextStringId),
            Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(actionStringId), listener).show();
  }

  private PendingIntent getGeofencePendingIntent() {

    Log.d("geo","getGeofencePendingIntent");
    // Reuse the PendingIntent if we already have it.
    if (mGeofencePendingIntent != null) {
      return mGeofencePendingIntent;
    }
    Log.d("geo","getGeofencePendingIntent2");
    Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
    // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
    // addGeofences() and removeGeofences().
    mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    return mGeofencePendingIntent;
  }

  private GeofencingRequest getGeofencingRequest() {

    Log.d("geo","getGeofencingRequest");
    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

    // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
    // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
    // is already inside that geofence.
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

    // Add the geofences to be monitored by geofencing service.
    builder.addGeofences(mGeofenceList);

    // Return a GeofencingRequest.
    return builder.build();
  }


  private boolean checkPermissions() {
    int permissionState = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION);
    return permissionState == PackageManager.PERMISSION_GRANTED;
  }*/

  /*private void requestPermissions() {

    Log.d("geo","request permission");
    boolean shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

    // Provide an additional rationale to the user. This would happen if the user denied the
    // request previously, but didn't check the "Don't ask again" checkbox.
    if (shouldProvideRationale) {
      android.util.Log.i("rationale", "Displaying permission rationale to provide additional context.");
      showSnackbar(R.string.permission_rationale, android.R.string.ok,
              new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  // Request permission
                  ActivityCompat.requestPermissions(MainActivity.this,
                          new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                          REQUEST_PERMISSIONS_REQUEST_CODE);
                }
              });
    } else {
      android.util.Log.i("rationale", "Requesting permission");
      // Request permission. It's possible this can be auto answered if device policy
      // sets the permission in a given state or the user denied the permission
      // previously and checked "Never ask again".
      ActivityCompat.requestPermissions(MainActivity.this,
              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
              REQUEST_PERMISSIONS_REQUEST_CODE);
    }
  }

  private void performPendingGeofenceTask() {
    if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
      addGeofences();
    } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
      removeGeofences();
    }
  }

  @Override
  public void onComplete(@NonNull Task<Void> task) {
    Log.d("geo", "oncomplete");
    mPendingGeofenceTask = PendingGeofenceTask.NONE;
    if (task.isSuccessful()) {
      updateGeofencesAdded(!getGeofencesAdded());
      setButtonsEnabledState();

      int messageId = getGeofencesAdded() ? R.string.geofences_added :
              R.string.geofences_removed;
      Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    } else {
      // Get the status code for the error and log it using a user-friendly message.

    }
  }
  private void setButtonsEnabledState() {

    Log.d("geo","setButtonsEnabledState");
    if (getGeofencesAdded()) {
      mAddGeofencesButton.setEnabled(false);
      mRemoveGeofencesButton.setEnabled(true);
    } else {
      mAddGeofencesButton.setEnabled(true);
      mRemoveGeofencesButton.setEnabled(false);
    }
  }

  private boolean getGeofencesAdded() {

    Log.d("geo","getGeofencesAdded");
    return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
            Constants.GEOFENCES_ADDED_KEY, false);
  }
  private void updateGeofencesAdded(boolean added) {
    Log.d("geo","updateGeofencesAdded");
    PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
            .apply();
  }*/


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
    imgMain.setBackgroundResource(R.drawable.ssg_finance);
    ButtonReset();
    btnFinance.setBackgroundResource(R.drawable.tap_bg_on);
    btnFinance.setTextColor(Color.parseColor("#d94d32"));
  }
  public void sendToPay(View view) {
    imgMain.setBackgroundResource(R.drawable.ssg_payment);
    ButtonReset();
    btnPayment.setBackgroundResource(R.drawable.tap_bg_on);
    btnPayment.setTextColor(Color.parseColor("#d94d32"));
  }
  public void sendToLife(View view) {
    imgMain.setBackgroundResource(R.drawable.ssg_lifestyle);
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
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);



   /* Log.d("geo","inside activity result requesting permission");
    if (!checkPermissions()) {
      mPendingGeofenceTask = PendingGeofenceTask.ADD;
      requestPermissions();
      return;
    }
    Log.d("geo","inside activity result requesting permission2");
    addGeofences();*/
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
