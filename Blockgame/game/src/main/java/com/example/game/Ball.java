package com.example.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by 박영은 on 2016-12-01.
 */
public class Ball implements DrawableItem{
    private float mX;
    private float mY;
    private float mSpeedX;
    private float mSpeedY;
    private final float mRadius;

    private final float mInitialSpeedX;
    private final float mInitialSpeedY; // 처음 속도

    private final float mInitialX;
    private final float mInitialY; // 처음 위치

    private int mcolor;

    public Ball(float radius, float initialX, float initialY,int color)
    {
        mRadius=radius;
        mSpeedX=radius/5;
        mSpeedY=radius/5;
        mX=initialX;
        mY=initialY;
        mInitialSpeedX=mSpeedX;
        mInitialSpeedY=mSpeedY;
        mInitialX=mX;
        mInitialY=mY;
        mcolor=color;
    }
    public float getSpeedX()
    {
        return mSpeedX;
    }
    public float getSpeedY() {return mSpeedY;}
    public float getY() {return mY;}
    public float getX() {return mX;}
    public int getColor(){return mcolor;}


    public void setSpeedX(float speedX) {mSpeedX=speedX;}
    public void setSpeedY(float speedY)
    {
        mSpeedY=speedY;
    }
    public void setColor(int color){mcolor=color;}

    public void move(){
        mX+=mSpeedX;
        mY+=mSpeedY;
    }

    public void draw(Canvas canvas, Paint paint)
    {
        if(mcolor==0){
            paint.setColor(Color.BLUE);
        }
        else if(mcolor==1){
            paint.setColor(Color.RED);
        }
        else{
            paint.setColor(Color.GREEN);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mX,mY,mRadius,paint);
    }

    public void reset(){
        mX=mInitialX;
        mY=mInitialY;
        mSpeedX=mInitialSpeedX * ((float)Math.random() - 0.5f);
        mSpeedY=mInitialSpeedY;
    }
}
