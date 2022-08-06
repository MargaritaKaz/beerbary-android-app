package com.example.beerbrary.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.beerbrary.R;

public class LoadingActivity extends AppCompatActivity {
    MediaPlayer music;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ImageView load = (ImageView) findViewById(R.id.loadingGif);
        Glide.with(this).load(R.drawable.loading).into(load);

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(6000);
                } catch (Exception e) {

                } finally {

                    Intent i = new Intent(LoadingActivity.this,
                            LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        Thread audioThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    music = MediaPlayer.create(LoadingActivity.this, R.raw.pouring);
                    music.start();
                } catch (Exception e) {

                }
            }
        };
        audioThread.start();
        welcomeThread.start();
    }
}