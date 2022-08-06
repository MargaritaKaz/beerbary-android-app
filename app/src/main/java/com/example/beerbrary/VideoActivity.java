package com.example.beerbrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    VideoView vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        vid = (VideoView) findViewById(R.id.videoView);
    }

    public void playVideo(View v) {
        MediaController m = new MediaController(this);
        vid.setMediaController(m);

        String path = "android.resource://com.example.beerbrary/"+R.raw.video;

        Uri u = Uri.parse(path);

        vid.setVideoURI(u);

        vid.start();

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.video, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){
            case R.id.search:
                startActivity(new Intent(this, ChooseTypeActivity.class));
                break;
            case R.id.home:
                startActivity(new Intent(this, BeerlistActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this, UserOwnProfileActivity.class));
                break;
            case R.id.video:
                Toast.makeText(this, "Already at video page", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}