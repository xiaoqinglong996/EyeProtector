package com.yourname.eyeprotector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // 检查用户设置是否开启自启动
            SharedPreferences prefs = context.getSharedPreferences(
                "EyeProtectorPrefs", Context.MODE_PRIVATE
            );
            boolean autoStart = prefs.getBoolean("autoStart", true);
            
            if (autoStart) {
                // 启动服务
                Intent serviceIntent = new Intent(context, TimerService.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            }
        }
    }
}
