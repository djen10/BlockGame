package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by 박영은 on 2016-12-10.
 */
public class Main extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    public void mClick(View v){
        switch (v.getId()){
            case R.id.m_start:
            {
                Intent intent=new Intent(this,GameActivity.class);
                startActivity(intent);
            }
        }
    }
}
