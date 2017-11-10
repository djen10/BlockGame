package com.example.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 박영은 on 2016-12-01.
 */
public class GameView extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener {

    private Thread mThread;
    volatile private boolean mIsRunnable; // 쓰레드의 값을 가져오는 것

    volatile private float mTouchedX;
    volatile private float mTouchedY; // 터치한곳의 x,y

    private ArrayList<DrawableItem> mItemList; // 블럭 저장할 arraylist
    private ArrayList<Block> mBlockList;

    private Pad mPad;
    private float mPadHalfWidth; // 패드

    private Ball mBall;
    private float mBallRadius; // 공

    private float mBlockWidth;
    private float mBlockHeight;

    private int mLife; //생명

    static final int BLOCK_COUNT = 80; // 블럭 갯수

    private long mGameStartTime; // 시간

    private Handler mHandler; // UI 그리기를 위한 핸들러

    private int hit = 0;


    boolean padflag = true;

    public GameView(final Context context) {
        super(context);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Intent intent = new Intent(context, ClearActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtras(msg.getData());
                context.startActivity(intent);
            }
        };
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        synchronized (this) {
            return true;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }

    public void start() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                int collisiontTime = 0;
                int soundIndex = 0;
                while (true) {
                    long startTime = System.currentTimeMillis();
                    synchronized (GameView.this) {
                        if (!mIsRunnable) {
                            break;
                        }
                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue;
                        }
                        if (mBall == null) {
                            continue;
                        }
                        canvas.drawColor(Color.BLACK);
                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);
                        mBall.move();
                        float ballTop = mBall.getY() - mBallRadius;
                        float ballLeft = mBall.getX() - mBallRadius;
                        float ballBottom = mBall.getY() + mBallRadius;
                        float ballRight = mBall.getX() + mBallRadius;

                        boolean sideflag = false;
                        if (ballLeft < 0 && mBall.getSpeedX() < 0 || ballRight >= getWidth() && mBall.getSpeedX() > 0) {
                            mBall.setSpeedX(-mBall.getSpeedX()); // 가로 부딪힘
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 10);
                            sideflag = true;
                        }
                        if (ballTop < 0) {
                            mBall.setSpeedY(-mBall.getSpeedY()); // 세로 부딪힘
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 10);
                            sideflag = true;
                        }
                        if (ballTop > getHeight()) {

                            if (mLife > 0) {
                                mLife--;
                                mBall.reset();
                            } else {
                                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE);
                                unlockCanvasAndPost(canvas);
                                Message message = Message.obtain();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR, false);
                                bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT, getBlockCount());
                                bundle.putLong(ClearActivity.EXTRA_TIME, System.currentTimeMillis() - mGameStartTime);
                                message.setData(bundle);
                                mHandler.sendMessage(message);
                                return;
                            }
                        }


                        Block leftBlock = getBlock(ballLeft, mBall.getY());
                        Block topBlock = getBlock(mBall.getX(), ballTop);
                        Block rightBlock = getBlock(ballRight, mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(), ballBottom);

                        boolean isCollision = false;

                        //여기가 충돌 조건
                        if (!sideflag) {
                            if (leftBlock != null) {
                                if (mBall.getColor() == leftBlock.getcolor()) {
                                    leftBlock.collision();
                                    isCollision = true;
                                }
                                int temp=(int)mBall.getSpeedX();
                                if(temp < 0){
                                    mBall.setSpeedX(-mBall.getSpeedX());
                                }
                                hit++;
                            }
                            if (topBlock != null) {
                                if (mBall.getColor() == topBlock.getcolor()) {
                                    topBlock.collision();
                                    isCollision = true;
                                }
                                int temp=(int)mBall.getSpeedY();
                                if(temp < 0 ){
                                    mBall.setSpeedY(-mBall.getSpeedY());
                                }
                                hit++;

                            }

                            if (rightBlock != null) {
                                if (mBall.getColor() == rightBlock.getcolor()) {
                                    rightBlock.collision();
                                    isCollision = true;
                                }
                                int temp=(int)mBall.getSpeedX();
                                if(temp > 0){
                                    mBall.setSpeedX(-mBall.getSpeedX());
                                }
                                hit++;
                            }
                            if (bottomBlock != null) {
                                if (mBall.getColor() == bottomBlock.getcolor()) {
                                    bottomBlock.collision();
                                    isCollision = true;
                                }
                                int temp=(int)mBall.getSpeedY();
                                if(temp > 0 ){
                                    mBall.setSpeedY(-mBall.getSpeedY());
                                }
                                hit++;
                            }

                            if (hit >= 2 && padflag) {
                                Random random = new Random();
                                int color = random.nextInt(3);
                                if (color == 0) {
                                    mPad.setColor(0);
                                } else if (color == 1) {
                                    mPad.setColor(1);
                                } else if (color == 2) {
                                    mPad.setColor(2);
                                }
                                padflag = false;
                            }

                            if (isCollision) {
                                if (collisiontTime > 0) {
                                    if (soundIndex < 15) {
                                        soundIndex++;
                                    } else {
                                        soundIndex = 1;
                                    }
                                }
                                collisiontTime = 10;
                                toneGenerator.startTone(soundIndex, 10);
                            } else if (collisiontTime > 0) {
                                collisiontTime--;
                            }
                        }
                        //여기는 패드랑 볼
                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();
                        if (ballBottom > padTop && ballBottom - ballSpeedY < padTop && padLeft < ballRight && padRight > ballLeft) {
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 10);
                            padflag = true;
                            if (ballSpeedY < mBlockHeight / 3) {
                                ballSpeedY *= -1.02f;
                            } else {
                                ballSpeedY = -ballSpeedY;
                            }
                            float ballSpeedX = mBall.getSpeedX() + (mBall.getX() - mTouchedX) / 10;
                            if (ballSpeedX > mBlockWidth / 5) {
                                ballSpeedX = mBlockWidth / 5;
                            }
                            mBall.setSpeedX(ballSpeedX);
                            mBall.setSpeedY(ballSpeedY);

                            if (hit >= 2) {
                                hit = 0;
                                mBall.setColor(mPad.getColor());
                            }
                        }
                        for (DrawableItem item : mItemList) {
                            item.draw(canvas, paint);
                        }

                        unlockCanvasAndPost(canvas);
                        if (isCollision && getBlockCount() == 0) {
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR, false);
                            bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT, getBlockCount());
                            bundle.putLong(ClearActivity.EXTRA_TIME, System.currentTimeMillis() - mGameStartTime);
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                        long sleepTime = 16 - System.currentTimeMillis() + startTime;
                        if (sleepTime > 0) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                            }
                        }

                    }
                }
                toneGenerator.release();
            }
        });
        mIsRunnable = true;
        mThread.start();
    }

    public void stop() {
        mIsRunnable = false;
    }

    public void readyObjects(int width, int height) {
        mBlockWidth = width / 10;
        mBlockHeight = height / 20;

        mItemList = new ArrayList<DrawableItem>();
        mBlockList = new ArrayList<Block>();

        for (int i = 0; i < BLOCK_COUNT; i++) {
            float blockTop = i / 10 * mBlockHeight;
            float blockLeft = i % 10 * mBlockWidth;
            float blockBottom = blockTop + mBlockHeight;
            float blockRight = blockLeft + mBlockWidth;
            Random random = new Random();
            int color = random.nextInt(3);
            mBlockList.add(new Block(blockTop, blockLeft, blockBottom, blockRight, color));
        }
        mItemList.addAll(mBlockList);


        mPadHalfWidth = width / 10;
        mBallRadius = width < height ? width / 40 : height / 40 ;
        Random random = new Random();
        int color = random.nextInt(3);
        mBall = new Ball(mBallRadius, width / 2 , height / 2 , color); // 여기가 공 그리는데 여기서 색깔 지정하자
        mItemList.add(mBall);
        mPad = new Pad(height * 0.8f, height * 0.85f, color);
        mItemList.add(mPad);
        mLife = 5;


        mGameStartTime = System.currentTimeMillis();
    }

    private Block getBlock(float x, float y) {
        int index = (int) (x / mBlockWidth) + (int) (y / mBlockHeight) * 10;
        if (0 <= index && index < BLOCK_COUNT) {
            Block block = (Block) mItemList.get(index);
            if (block.isExist()) {
                return block;
            }
        }
        return null;
    }

    private int getBlockCount() {
        int count = 0;
        for (Block block : mBlockList) {
            if (block.isExist()) {
                count++;
            }
        }
        return count;
    }
}
