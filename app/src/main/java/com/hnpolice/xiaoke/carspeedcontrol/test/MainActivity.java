package com.hnpolice.xiaoke.carspeedcontrol.test;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hnpolice.xiaoke.carspeedcontrol.R;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private int tvLeft, tvRight, tvTop, tvBottom;
    private int Left, Right, Top, Bottom;
    private boolean first = true;
    private int startX, startY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.test);


        if (first){
            textView.post(new Runnable() {
                @Override
                public void run() {
                    tvLeft = textView.getLeft();
                    tvTop = textView.getTop();
                    tvRight = textView.getRight();
                    tvBottom = textView.getBottom();
                    first = false;
                    textView.setText(tvLeft + "," + tvTop + "," + tvRight + "," + tvBottom);
                }
            });
        }


        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        Left = textView.getLeft();
                        Top = textView.getTop();
                        Right = textView.getRight();
                        Bottom = textView.getBottom();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        textView.layout(
                                (int) (Left + event.getRawX() - startX),
                                (int) (Top + event.getRawY() - startY),
                                (int) (Right + event.getRawX() - startX),
                                (int) (Bottom + event.getRawY() - startY));
                        break;
                    case MotionEvent.ACTION_UP:
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(
                                ObjectAnimator.ofFloat(textView, "translationX", tvLeft - textView.getLeft()),
                                ObjectAnimator.ofFloat(textView, "translationY", tvTop - textView.getTop())
                        );
                        set.setDuration(2000).start();

                        textView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textView.layout(textView.getLeft(), textView.getTop(), textView.getRight(), textView.getBottom());
                            }
                        }, 2000);

                        break;
                }
                return true;
            }
        });

    }
}
