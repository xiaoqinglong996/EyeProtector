package com.yourname.eyeprotector;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class TimerReceiver extends BroadcastReceiver {
    
    private static final String CHANNEL_ID = "EyeProtectorNotification";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        
        if ("WORK_TIMER_COMPLETE".equals(action)) {
            // 工作时间结束，提醒休息
            showRestNotification(context);
            
            // 设置休息结束的定时器
            SharedPreferences prefs = context.getSharedPreferences(
                "EyeProtectorPrefs", Context.MODE_PRIVATE
            );
            int restTime = prefs.getInt("restTime", 5);
            long restMillis = restTime * 60 * 1000L;
            long triggerTime = System.currentTimeMillis() + restMillis;
            
            Intent restIntent = new Intent(context, TimerReceiver.class);
            restIntent.setAction("REST_TIMER_COMPLETE");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 1, restIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
            
        } else if ("REST_TIMER_COMPLETE".equals(action)) {
            // 休息时间结束，提醒继续工作
            showWorkNotification(context);
            
            // 设置下一个工作定时器
            SharedPreferences prefs = context.getSharedPreferences(
                "EyeProtectorPrefs", Context.MODE_PRIVATE
            );
            int workTime = prefs.getInt("workTime", 20);
            long workMillis = workTime * 60 * 1000L;
            long triggerTime = System.currentTimeMillis() + workMillis;
            
            Intent workIntent = new Intent(context, TimerReceiver.class);
            workIntent.setAction("WORK_TIMER_COMPLETE");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, workIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }
    
    private void showRestNotification(Context context) {
        createNotificationChannel(context);
        
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("护眼提醒")
            .setContentText("工作时间结束，请休息5分钟")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build();
        
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(100, notification);
        }
    }
    
    private void showWorkNotification(Context context) {
        createNotificationChannel(context);
        
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("护眼提醒")
            .setContentText("休息时间结束，请继续工作")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build();
        
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(101, notification);
        }
    }
    
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "护眼提醒",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("护眼定时器提醒通知");
            
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
