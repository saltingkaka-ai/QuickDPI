package com.example.quickdpi;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startServiceBtn = findViewById(R.id.startServiceBtn);
        Button stopServiceBtn = findViewById(R.id.stopServiceBtn);

        startServiceBtn.setOnClickListener(v -> checkPermissionsAndStart());
        stopServiceBtn.setOnClickListener(v -> stopService());
    }

    private void checkPermissionsAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 
                    PERMISSION_REQUEST_CODE);
            } else {
                createNotificationChannel();
                startDPIService();
            }
        } else {
            createNotificationChannel();
            startDPIService();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "dpi_channel",
                "DPI Control",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Quick DPI Changer Notification");
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void startDPIService() {
        Intent serviceIntent = new Intent(this, DPIForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, "Sticky DPI Notification Active", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, DPIForegroundService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createNotificationChannel();
                startDPIService();
            } else {
                Toast.makeText(this, "Notification permission required", Toast.LENGTH_LONG).show();
            }
        }
    }
}
