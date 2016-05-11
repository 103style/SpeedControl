package com.hnpolice.xiaoke.carspeedcontrol.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * create by luoxiaoke on 2016/5/10 9:07.
 * use for 速度展示view
 */
public class SpeedControlView extends View implements Runnable {

    //画笔
    private Paint mPaint, textPaint, speedAreaPaint;
    private Context mContext;
    //屏幕宽高
    private int screenWidth, screenHeight;
    //仪表盘圆的半径
    private int raduis, sRaduis;
    //圆心
    private int pointX, pointY;
    //文字的偏移量
    private float textScale;
    //速度
    private int speed;
    //速度范围的2个扇形外切矩形
    private RectF speedRectF, speedRectFInner;
    //速度控制模式  1 加速  2 减速  3 手刹
    private int type;

    // 速度文字 绘制的XY坐标
    private int baseX, baseY;

    //设置速度控制模式
    public void setType(int type) {
        this.type = type;
    }

    // 设置速度 并重绘视图
    public void setSpeed(int speed) {
        this.speed = speed;
        postInvalidate();
    }


    public SpeedControlView(Context context) {
        this(context, null);
    }

    public SpeedControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        //获取屏幕宽高
        screenWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();

        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //设置抗锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        //设置画笔样式
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);

        //初始化  圆心左边 和 半径
        raduis = screenWidth / 3;
        pointX = screenWidth / 2;
        pointY = screenHeight / 4;

        //设置抗锯齿
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        //设置画笔颜色
        textPaint.setColor(Color.WHITE);
        // 获取字体并设置画笔字体
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "kt.ttf");
        textPaint.setTypeface(typeface);
        //设置抗锯齿
        speedAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        speedAreaPaint.setAntiAlias(true);
        //设置画笔样式
        speedAreaPaint.setStyle(Paint.Style.FILL);
        // 设置速度范围扇形的渐变颜色
        Shader mShader = new LinearGradient(0, 0, 100, 100,
                new int[]{0x7001EC9, 0xBF001EC9, 0xFF001EC9}, null, Shader.TileMode.CLAMP);
        speedAreaPaint.setShader(mShader);
        // 初始化速度范围的2个扇形外切矩形
        speedRectF = new RectF(pointX - raduis + 10, pointY - raduis + 10, pointX + raduis - 10, pointY + raduis - 10);
        speedRectFInner = new RectF(pointX - raduis / 2, pointY - raduis / 2, pointX + raduis / 2, pointY + raduis / 2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        //绘制速度范围扇形区域
        speedAreaPaint.setColor(0x7E3F51B5);
        drawSpeedArea(canvas);

        //绘制外层圆
        drawCicle(canvas);

        //变换画笔颜色 绘制刻度
        mPaint.setColor(0xBF3F6AB5);
        drawScale(canvas);

        //变换画笔颜色 绘制速度标识文字
        textPaint.setTextSize(25);
        mPaint.setColor(Color.WHITE);
        sRaduis = raduis - 50;
        textScale = Math.abs(textPaint.descent() + textPaint.ascent()) / 2;
//        Log.e("textScale", textScale + "");
        for (int i = 0; i < 8; i++) {
            drawText(canvas, 30 * i);
        }

        //绘制中间内容
        drawCenter(canvas);

    }

    /**
     * 绘制外层圆
     */
    private void drawCicle(Canvas canvas) {

        mPaint.setColor(0xFF343434);
        canvas.drawCircle(pointX, pointY, raduis, mPaint);

        //外圈2个圆
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xBF3F6AB5);
        mPaint.setStrokeWidth(4);
        canvas.drawCircle(pointX, pointY, raduis, mPaint);
        mPaint.setStrokeWidth(3);
        canvas.drawCircle(pointX, pointY, raduis - 10, mPaint);

        //内圈2个圆
        mPaint.setStrokeWidth(5);
        mPaint.setColor(0xE73F51B5);
        canvas.drawCircle(pointX, pointY, raduis / 2, mPaint);
        mPaint.setColor(0x7E3F51B5);
        canvas.drawCircle(pointX, pointY, raduis / 2 + 5, mPaint);
        mPaint.setStrokeWidth(3);

    }

    /**
     * 绘制刻度
     */
    private void drawScale(Canvas canvas) {
        for (int i = 0; i < 60; i++) {
            if (i % 6 == 0) {
                canvas.drawLine(pointX - raduis + 10, pointY, pointX - raduis + 50, pointY, mPaint);
            } else {
                canvas.drawLine(pointX - raduis + 10, pointY, pointX - raduis + 30, pointY, mPaint);
            }
            canvas.rotate(6, pointX, pointY);
        }
    }

    /**
     * 绘制速度标识文字
     */
    private void drawText(Canvas canvas, int value) {
        String TEXT = String.valueOf(value);
        switch (value) {
            case 0:
                // 计算Baseline绘制的起点X轴坐标
                baseX = (int) (pointX - sRaduis * Math.cos(Math.PI / 5) + textPaint.measureText(TEXT) / 2 + textScale / 2);
                // 计算Baseline绘制的Y坐标
                baseY = (int) (pointY + sRaduis * Math.sin(Math.PI / 5) + textScale / 2);
                break;
            case 30:
                baseX = (int) (pointX - raduis + 50 + textPaint.measureText(TEXT) / 2);
                baseY = (int) (pointY + textScale);
                break;
            case 60:
                baseX = (int) (pointX - sRaduis * Math.cos(Math.PI / 5) + textScale);
                baseY = (int) (pointY - sRaduis * Math.sin(Math.PI / 5) + textScale * 2);
                break;
            case 90:
                baseX = (int) (pointX - sRaduis * Math.cos(2 * Math.PI / 5) - textScale / 2);
                baseY = (int) (pointY - sRaduis * Math.sin(2 * Math.PI / 5) + 2 * textScale);
                break;
            case 120:
                baseX = (int) (pointX + sRaduis * Math.sin(Math.PI / 10) - textPaint.measureText(TEXT) / 2);
                baseY = (int) (pointY - sRaduis * Math.cos(Math.PI / 10) + 2 * textScale);
                break;
            case 150:
                baseX = (int) (pointX + sRaduis * Math.cos(Math.PI / 5) - textPaint.measureText(TEXT) - textScale / 2);
                baseY = (int) (pointY - sRaduis * Math.sin(Math.PI / 5) + textScale * 2);
                break;
            case 180:
                baseX = (int) (pointX + sRaduis - textPaint.measureText(TEXT) - textScale / 2);
                baseY = (int) (pointY + textScale);
                break;
            case 210:
                baseX = (int) (pointX + sRaduis * Math.cos(Math.PI / 5) - textPaint.measureText(TEXT) - textScale / 2);
                baseY = (int) (pointY + sRaduis * Math.sin(Math.PI / 5) - textScale / 2);
                break;

        }
        canvas.drawText(TEXT, baseX, baseY, textPaint);
    }

    /**
     * 绘制中间内容
     */
    private void drawCenter(Canvas canvas) {
        //速度
        textPaint.setTextSize(60);
        float tw = textPaint.measureText(String.valueOf(speed));
        baseX = (int) (pointX - tw / 2);
        baseY = (int) (pointY + Math.abs(textPaint.descent() + textPaint.ascent()) / 4);
        canvas.drawText(String.valueOf(speed), baseX, baseY, textPaint);

        //单位
        textPaint.setTextSize(20);
        tw = textPaint.measureText("km/h");
        baseX = (int) (pointX - tw / 2);
        baseY = (int) (pointY + raduis / 4 + Math.abs(textPaint.descent() + textPaint.ascent()) / 4);
        canvas.drawText("km/h", baseX, baseY, textPaint);
    }

    /**
     * 绘制速度区域扇形
     */
    private void drawSpeedArea(Canvas canvas) {
        int degree;
        if (speed < 210) {
            degree = speed * 36 / 30;
        } else {
            degree = 210 * 36 / 30;
        }

        canvas.drawArc(speedRectF, 144, degree, true, speedAreaPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawArc(speedRectFInner, 144, degree, true, mPaint);

    }


    @Override
    public void run() {
        int speedChange;
        while (true) {
            switch (type) {
                case 1://油门
                    speedChange = 3;
                    break;
                case 2://刹车
                    speedChange = -5;
                    break;
                case 3://手刹
                    speed = 0;
                default:
                    speedChange = -1;
                    break;
            }
            speed += speedChange;
            if (speed < 1) {
                speed = 0;
            }
            try {
                Thread.sleep(50);
                setSpeed(speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
