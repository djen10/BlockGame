package com.example.game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity{

    private GameView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView=new GameView(this);
        setContentView(mView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
       mView.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
