package com.example.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


/**
 * Created by 박영은 on 2016-12-01.
 */
public class Block implements DrawableItem{
    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int mHard;

    private int mColor;

    private boolean mIsCollision=false;
    private boolean mIsExist=true;


    public Block(float top, float left, float bottom, float right, int color){
        mTop=top;
        mLeft=left;
        mBottom=bottom;
        mRight=right;
        mHard=1;
        mColor=color;

    }

    public void draw(Canvas canvas, Paint paint){
        if(mIsExist){
            if (mIsCollision) {
                mHard--;
                mIsCollision=false;
                if(mHard<=0){
                    mIsExist=false;
                    return;
                }
            }
            if(mColor==0) {
                paint.setColor(Color.BLUE);
            }
            else if(mColor==1){
                paint.setColor(Color.RED);
            }
            else{
                paint.setColor(Color.GREEN);
            }
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mLeft,mTop,mRight,mBottom,paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            canvas.drawRect(mLeft,mTop,mRight,mBottom,paint);
        }
    }

    public void collision()
    {
        mIsCollision=true;
    }
    public boolean isExist()
    {
        return mIsExist;
    }
    public int getcolor(){return mColor;}


}
