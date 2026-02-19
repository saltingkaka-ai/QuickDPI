package com.example.quickdpi;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class DPIForegroundService extends Service {
    
    public static final String ACTION_APPLY_DPI = "com.example.quickdpi.APPLY_DPI";
    public static final String EXTRA_DPI_VALUE = "dpi_value";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_APPLY_DPI.equals(intent.getAction())) {
            String dpiValue = intent.getStringExtra(EXTRA_DPI_VALUE);
            if (dpiValue != null && !dpiValue.isEmpty()) {
                applyDPI(dpiValue);
            }
        }
        
        startForeground(NOTIFICATION_ID, buildStickyNotification());
        return START_STICKY;
    }

    private Notification buildStickyNotification() {
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_dpi);
        
        notificationLayout.setTextViewText(R.id.dpiInput, "400");
        
        Intent applyIntent = new Intent(this, DPIForegroundService.class);
        applyIntent.setAction(ACTION_APPLY_DPI);
        
        PendingIntent applyPendingIntent = PendingIntent.getService(
            this, 
            0, 
            applyIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );
        
        notificationLayout.setOnClickPendingIntent(R.id.applyBtn, applyPendingIntent);

        Intent closeIntent = new Intent(this, DPICloseReceiver.class);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(
            this, 
            1, 
            closeIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        notificationLayout.setOnClickPendingIntent(R.id.closeBtn, closePendingIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "dpi_channel")
            .setSmallIcon(android.R.drawable.ic_menu_preferences)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        }

        return builder.build();
    }

    private void applyDPI(String dpiValue) {
        try {
            int dpi = Integer.parseInt(dpiValue);
            if (dpi < 100 || dpi > 1000) {
                return;
            }
            
            Process process = Runtime.getRuntime().exec("su");
            java.io.DataOutputStream os = new java.io.DataOutputStream(process.getOutputStream());
            os.writeBytes("wm density " + dpi + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent restartIntent = new Intent(this, DPIForegroundService.class);
        startService(restartIntent);
    }
}
