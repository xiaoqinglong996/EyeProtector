package com.yourname.eyeprotector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class TimerService extends Service {
    
    private static final String CHANNEL_ID = "EyeProtectorChannel";
    private static final int NOTIFICATION_ID = 1;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 返回START_STICKY确保服务被杀死后重启
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "护眼定时器",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("护眼定时器正在运行");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("护眼定时器")
            .setContentText("定时提醒服务运行中")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
