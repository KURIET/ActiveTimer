package com.bizzarestudy.activetimer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /**
     * 들어갈 기능
     * <p>
     * 1. 앱이 실행된 시간을 기준으로 얼마 지났는지 체크하기
     * 2. android.intent.action.SCREEN_ON / OFF에 따라 시간이 멈추고 시작하는 것을 구현
     * -------------------------------- 이하 아이디어
     * 3. 일별 사용기록 측정
     * 4. 이용자 평균 이용시간 백분율로 표시 (헤비 유저인가 / 라이트 유저인가)
     * 5.
     */

    TextView active_Time;

    private static Handler mHandler;

    long start_Time = System.currentTimeMillis();
    long store_Time;

    Thread t, t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        active_Time = findViewById(R.id.active_time);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        tagHere("Current time : " + start_Time);

        mHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                store_Time = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("HH : mm : ss");
                String diff_Time = sdf.format(store_Time - start_Time - 32400000);
                active_Time.setText(diff_Time);
            }
        };

        class NewRunnable implements Runnable {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(0);
                }
            }
        }

        NewRunnable nr = new NewRunnable();
        t = new Thread(nr);
        t.start();


        BroadcastReceiver screenOnOff = new BroadcastReceiver() {

            public static final String ScreenOn = "android.intent.action.SCREEN_ON";
            public static final String ScreenOff = "android.intent.action.SCREEN_OFF";

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ScreenOff)) {
                    //time stops
                    t.interrupt();

                } else if (intent.getAction().equals(ScreenOn)) {
                    //time goes on

                }
            }
        };

        this.registerReceiver(screenOnOff, intentFilter);
    }

    public void timerStop() {

    }

    public void timerStart() {

    }

    public void tagHere(String message) {
        Log.i("sj", message);
    }

}
