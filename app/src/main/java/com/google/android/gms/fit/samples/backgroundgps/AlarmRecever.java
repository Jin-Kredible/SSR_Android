package com.google.android.gms.fit.samples.backgroundgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmRecever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, RestartService.class);
            context.startForegroundService(in); // 버전 8.0 이상일때
        } else {
            Intent in = new Intent(context, RealService.class);
            context.startService(in); //버전 8.0 이하 일때
        }
    }

}