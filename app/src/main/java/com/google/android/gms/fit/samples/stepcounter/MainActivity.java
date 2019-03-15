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

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.google.android.gms.fit.samples.backgroundgps.LocationManage;
import com.google.android.gms.fit.samples.backgroundgps.RealService;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.common.logger.LogWrapper;
import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.shin.ssr.layout.tab.FinanceTab;
import com.shin.ssr.layout.tab.FitTab;
import com.shin.ssr.layout.tab.LifeTab;
import com.shin.ssr.layout.tab.PaymentTab;
import com.shin.ssr.vo.LocationVO;


/**
 * This sample demonstrates combining the Recording API and History API of the Google Fit platform
 * to record steps, and display the daily current step count. It also demonstrates how to
 * authenticate a user with Google Play Services.
 */
public class MainActivity extends AppCompatActivity {

  private Intent serviceIntent;
  private LocationVO locationVO = new LocationVO();
  LocationManage locationManage = new LocationManage();

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // This method sets up our custom logger, which will print all log messages to the device
    // screen, as well as to adb logcat.
    initializeLogging();

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
    /////////////////////////////////////////////////////////////////
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
