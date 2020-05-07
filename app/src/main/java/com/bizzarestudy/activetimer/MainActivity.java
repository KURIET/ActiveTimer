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
import android.view.View;
import android.widget.Button;
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
    long store_Time, previous_diff_Time_long = 0, current_diff_Time_long = 0;
    boolean flag = true;

    Thread t;
    Button start, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Connect UI Components
        active_Time = findViewById(R.id.active_time);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);

        //Intent Filter to detect events
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        tagHere("===========App initalized time : " + start_Time);

        //Handler for working on Main Thread
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                store_Time = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("HH : mm : ss");
                // 68400000(USA) / 32400000(KOREA)
                // 우리 시간(UTF+09)에 맞추어진 숫자의 차이
                // 이전에 저장되었던 시간을 ------ diff_Time_long
                // 00:00:00(UTF+09) 기준으로 ------ diff_Time_long - 32400000
                // 딱 한번 더해준다.
                previous_diff_Time_long = store_Time - start_Time;
                tagHere("previous diff time long : " + previous_diff_Time_long);
                //그리고 시간을 표시한다.
                String diff_Time = sdf.format(current_diff_Time_long + store_Time - start_Time - 32400000);
                active_Time.setText(diff_Time);
            }
        };

        t = new customThread();
        t.start();

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.interrupt();
                current_diff_Time_long = current_diff_Time_long + previous_diff_Time_long;
                tagHere("stop button");
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_Time = System.currentTimeMillis();
                t = new customThread();
                tagHere("!t.isAlive() = " + !t.isAlive());
                if(!t.isAlive()) {
                    t.start();
                    tagHere("start button");
                }
            }
        });

        this.registerReceiver(screenOnOff, intentFilter);


    }

    class customThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                mHandler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    tagHere("interrupted");
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    BroadcastReceiver screenOnOff = new BroadcastReceiver() {

        public static final String ScreenOn = "android.intent.action.SCREEN_ON";
        public static final String ScreenOff = "android.intent.action.SCREEN_OFF";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ScreenOff)) {
                //time stops

                t.interrupt();
                current_diff_Time_long = current_diff_Time_long + previous_diff_Time_long;
                tagHere("SAME FUNCTION AS - stop button");

                tagHere("AFTER stop --t.isAlive() = "+t.isAlive());
                tagHere("t.isInterrupted() = "+t.isInterrupted());


            } else if (intent.getAction().equals(ScreenOn)) {
                //time goes on

                start_Time = System.currentTimeMillis();
                t = new customThread();
                tagHere("!t.isAlive() = " + !t.isAlive());
                if(!t.isAlive()) {
                    t.start();
                    tagHere("SAME FUNCTION AS - start button");
                }

                tagHere("go");
                tagHere("t.isInterrupted() = "+t.isInterrupted());
                tagHere("t.isAlive() = "+t.isAlive());
                flag = true;

            }
        }
    };

    public void tagHere(String message) {
        Log.i("sj", message);
    }

}
