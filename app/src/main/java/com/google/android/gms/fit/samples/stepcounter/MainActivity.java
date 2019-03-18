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
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.google.android.gms.fit.samples.backgroundgps.LocationManage;
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
import com.shin.ssr.layout.tab.FinanceTab;
import com.shin.ssr.layout.tab.FitTab;
import com.shin.ssr.layout.tab.LifeTab;
import com.shin.ssr.layout.tab.PaymentTab;
import com.shin.ssr.vo.LocationVO;

import java.util.ArrayList;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements OnCompleteListener<Void>  {

  private Intent serviceIntent;
  private LocationVO locationVO = new LocationVO();
  LocationManage locationManage = new LocationManage();
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

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // This method sets up our custom logger, which will print all log messages to the device
    // screen, as well as to adb logcat.
    initializeLogging();
    Log.d("geo","oncreate");

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
    addGeofences();

  /** 기존의 위치 받아오는 로직 **/
  /*  /////////////////////////////////////////////////////////////
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
    /////////////////////////////////////////////////////////////////*/
  }

  private void populateGeofenceList() {
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
  }

  @Override
  public void onStart() {
    Log.d("geo","onStart");
    super.onStart();

    if (!checkPermissions()) {
      requestPermissions();
    } else {
      performPendingGeofenceTask();
    }
  }

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
  }

  private void requestPermissions() {

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
    Intent intent = new Intent(MainActivity.this, FinanceTab.class);
    startActivity(intent);
  }

  public void sendToFit(View view) {
    Intent intent = new Intent(MainActivity.this, FitTab.class);
    startActivity(intent);
  }
  public void sendToLife(View view) {
    Intent intent = new Intent(MainActivity.this,LifeTab.class);
    startActivity(intent);
  }

  public void sendToPay(View view) {
    Intent intent = new Intent(MainActivity.this, PaymentTab.class);
    startActivity(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    Log.d("geo","inside activity result requesting permission");
    if (!checkPermissions()) {
      mPendingGeofenceTask = PendingGeofenceTask.ADD;
      requestPermissions();
      return;
    }
    Log.d("geo","inside activity result requesting permission2");
    addGeofences();
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


  @Override
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
