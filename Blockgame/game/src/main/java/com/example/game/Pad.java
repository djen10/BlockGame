package com.example.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by 박영은 on 2016-12-01.
 */
public class Pad implements DrawableItem{
    private final float mTop;
    private float mLeft;
    private final float mBottom;
    private float mRight;

    private int mcolor;

    public int getColor(){return mcolor;}
    public void setColor(int color){mcolor=color;}

    public Pad(float top, float bottom,int color)
    {
        mTop=top;
        mBottom=bottom;
        mcolor=color;
    }
    public void setLeftRight(float left, float right)
    {
        mLeft=left;
        mRight=right;
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
        canvas.drawRect(mLeft,mTop,mRight,mBottom,paint);
    }

    public float getTop()
    {
        return mTop;
    }
}
