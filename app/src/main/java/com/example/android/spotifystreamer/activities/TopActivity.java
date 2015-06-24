package com.example.android.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.fragments.TopFragment;

/**
 * Created by DJ on 6/12/2015.
 */
public class TopActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString("SPOTIFY_ID", getIntent().getStringExtra("SPOTIFY_ID"));
            TopFragment topFragment = new TopFragment();
            topFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.container_alpha, topFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}