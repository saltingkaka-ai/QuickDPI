package com.example.quickdpi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class DPIChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.quickdpi.CHANGE_DPI".equals(intent.getAction())) {
            Bundle remoteInput = androidx.core.app.RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                String dpiValue = remoteInput.getString("dpi_input");
                if (dpiValue != null) {
                    applyDPI(context, dpiValue);
                }
            }
        }
    }
    
    private void applyDPI(Context context, String dpiValue) {
        try {
            int dpi = Integer.parseInt(dpiValue);
            Process process = Runtime.getRuntime().exec("su");
            java.io.DataOutputStream os = new java.io.DataOutputStream(process.getOutputStream());
            os.writeBytes("wm density " + dpi + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            
            Toast.makeText(context, "DPI changed to " + dpi, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
