package com.example.hanjun.testbook;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hanjun on 2016/4/22.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isServiceRunning = false;


        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {

            //检查Service状态

            ActivityManager manager = (ActivityManager)(context.getSystemService(Context.ACTIVITY_SERVICE));
            for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) {
                String classname = service.service.getClassName();
                if("com.example.hanjun.testbook.MyService".equals(classname))
                {
                    isServiceRunning = true;
                }

            }
            if (!isServiceRunning) {
                Intent i = new Intent(context, MyService.class);
                context.startService(i);
            }


        }

    }
}
