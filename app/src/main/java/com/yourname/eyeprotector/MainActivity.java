package com.yourname.eyeprotector;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private EditText workTimeEdit, restTimeEdit;
    private Button startButton, stopButton;
    private TextView statusText, timerText;
    private SharedPreferences prefs;
    private AlarmManager alarmManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化视图
        workTimeEdit = findViewById(R.id.workTimeEdit);
        restTimeEdit = findViewById(R.id.restTimeEdit);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        statusText = findViewById(R.id.statusText);
        timerText = findViewById(R.id.timerText);
        
        // 获取AlarmManager
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        // 获取SharedPreferences
        prefs = getSharedPreferences("EyeProtectorPrefs", MODE_PRIVATE);
        
        // 加载保存的设置
        workTimeEdit.setText(String.valueOf(prefs.getInt("workTime", 20)));
        restTimeEdit.setText(String.valueOf(prefs.getInt("restTime", 5)));
        
        // 启动按钮点击事件
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
        
        // 停止按钮点击事件
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });
        
        // 启动前台服务
        startService(new Intent(this, TimerService.class));
    }
    
    private void startTimer() {
        try {
            int workTime = Integer.parseInt(workTimeEdit.getText().toString());
            int restTime = Integer.parseInt(restTimeEdit.getText().toString());
            
            // 保存设置
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("workTime", workTime);
            editor.putInt("restTime", restTime);
            editor.apply();
            
            // 设置定时器
            long workMillis = workTime * 60 * 1000L;
            long triggerTime = System.currentTimeMillis() + workMillis;
            
            Intent intent = new Intent(this, TimerReceiver.class);
            intent.setAction("WORK_TIMER_COMPLETE");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            // 使用精确的闹钟
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent
                );
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
            
            statusText.setText("状态：工作中 (" + workTime + "分钟)");
            Toast.makeText(this, "定时器已启动", Toast.LENGTH_SHORT).show();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void stopTimer() {
        Intent intent = new Intent(this, TimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        alarmManager.cancel(pendingIntent);
        statusText.setText("状态：已停止");
        Toast.makeText(this, "定时器已停止", Toast.LENGTH_SHORT).show();
    }
}
