package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements SearchFragment.SearchCallback{
    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_alpha, new SearchFragment()).commit();
        }

        if (findViewById(R.id.container_beta) != null) {
            mTwoPane = true;
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.container_beta, new TopFragment()).commit();
//            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(String spotifyId) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putString("SPOTIFY_ID",spotifyId);

            TopFragment topFragment = new TopFragment();
            topFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.container_beta,topFragment).commit();
        }else{
            Intent i = new Intent(this,TopActivity.class);
            i.putExtra("SPOTIFY_ID",spotifyId);
            startActivity(i);
        }
    }
}
