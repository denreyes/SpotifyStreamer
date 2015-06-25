package com.example.android.spotifystreamer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.fragments.PlayerFragment;

/**
 * Created by DJ on 6/21/2015.
 */
public class PlayerActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_alpha, new PlayerFragment()).commit();
        }
    }
}