package com.yourname.eyeprotector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ScreenReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(
            "EyeProtectorPrefs", Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = prefs.edit();
        
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            // 屏幕关闭，记录时间
            editor.putLong("screenOffTime", System.currentTimeMillis());
            editor.apply();
            
        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            // 屏幕打开，检查是否需要重置计时器
            long screenOffTime = prefs.getLong("screenOffTime", 0);
            long screenOnTime = System.currentTimeMillis();
            
            // 如果屏幕关闭超过30秒，重置计时器
            if (screenOffTime > 0 && (screenOnTime - screenOffTime) > 30000) {
                // 取消之前的定时器
                Intent timerIntent = new Intent(context, TimerReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, 0, timerIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
                
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                }
                
                // 重新开始计时
                int workTime = prefs.getInt("workTime", 20);
                long workMillis = workTime * 60 * 1000L;
                long triggerTime = screenOnTime + workMillis;
                
                timerIntent.setAction("WORK_TIMER_COMPLETE");
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }
}
