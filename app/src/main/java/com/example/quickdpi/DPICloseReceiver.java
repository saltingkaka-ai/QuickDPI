package com.example.quickdpi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DPICloseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, DPIForegroundService.class);
        context.stopService(serviceIntent);
        
        android.app.NotificationManager manager = 
            (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1001);
    }
}
