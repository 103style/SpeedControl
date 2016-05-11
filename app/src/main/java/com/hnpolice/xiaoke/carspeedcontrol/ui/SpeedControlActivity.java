package com.hnpolice.xiaoke.carspeedcontrol.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.hnpolice.xiaoke.carspeedcontrol.R;
import com.hnpolice.xiaoke.carspeedcontrol.view.SpeedControlView;

public class SpeedControlActivity extends AppCompatActivity{

    private SpeedControlView speedControlView;
    private Button speedUp;
    private Button speedDown;
    private Button shutDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_control);
        speedControlView = (SpeedControlView) findViewById(R.id.speed_control);

        new Thread(speedControlView).start();

        speedUp = (Button) findViewById(R.id.speed_up);
        speedDown = (Button) findViewById(R.id.speed_down);
        shutDown = (Button) findViewById(R.id.shut_down);

        speedUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speedControlView.setType(1);
                        break;
                    case MotionEvent.ACTION_UP:
                        speedControlView.setType(0);
                        break;
                }
                return true;
            }
        });
        speedDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speedControlView.setType(2);
                        break;
                    case MotionEvent.ACTION_UP:
                        speedControlView.setType(0);
                        break;
                }
                return true;
            }
        });
        shutDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speedControlView.setType(3);
                        break;
                    case MotionEvent.ACTION_UP:
                        speedControlView.setType(0);
                        break;
                }
                return true;
            }
        });


    }

}
