package com.example.hanjun.testbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hanjun on 2016/4/22.
 */
public class stopIntentService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ((MyService)context).onDestroy();
    }
}
