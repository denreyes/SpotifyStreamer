package com.example.android.spotifystreamer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by DJ on 6/12/2015.
 */
public class TopActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container_alpha, new TopFragment()).commit();
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

    public static class TopFragment extends Fragment {
        @InjectView(R.id.listTop) ListView listTop;
        static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
        ArrayList<TopObject> list;

        public TopFragment() {

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_top, container, false);
            ButterKnife.inject(this, rootView);
            list = new ArrayList<>();

            if(savedInstanceState!=null) {
                list = savedInstanceState.getParcelableArrayList("TOP_OBJECT");
                TopAdapter adapter = new TopAdapter(getActivity(),list);
                listTop.setAdapter(adapter);
            }
            else
                fetchSpotify();
            return rootView;
        }

        private void fetchSpotify() {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String country = sharedPrefs.getString(getActivity().getString(R.string.pref_country_key),
                    getActivity().getString(R.string.pref_country_default));

            Map<String, Object> map = new HashMap<>();
            map.put("country", country);
            spotify.getArtistTopTrack(getActivity().getIntent().getStringExtra("SPOTIFY_ID"), map,
                    new Callback<Tracks>() {
                        @Override
                        public void success(Tracks tracks, Response response) {
                            querySuccess(tracks);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            MAIN_THREAD.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Can't access the web", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
        }

        private void querySuccess(Tracks tracks) {
            int size = tracks.tracks.size();

            if (size != 0) {
                String trackTitle, trackAlbum, trackImage="";
                for (int x = 0; x < size; x++) {
                    trackTitle = tracks.tracks.get(x).name;
                    trackAlbum = tracks.tracks.get(x).album.name;
                    if (tracks.tracks.get(x).album.images.toString() != "[]") {
                        trackImage = tracks.tracks.get(x).album.images.get(0).url;
                    }
                    list.add(new TopObject(trackTitle,trackAlbum,trackImage));
                }

                MAIN_THREAD.post(new Runnable() {
                    @Override
                    public void run() {
                        TopAdapter topAdapter = new TopAdapter(getActivity(), list);
                        listTop.setAdapter(topAdapter);
                    }
                });
            } else {
                MAIN_THREAD.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "There are no top Tracks for this Artist", Toast.LENGTH_LONG).show();
                        listTop.setAdapter(null);
                    }
                });
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelableArrayList("TOP_OBJECT",list);
        }
    }
}