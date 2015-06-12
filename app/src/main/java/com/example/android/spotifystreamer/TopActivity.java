package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by DJ on 6/12/2015.
 */
public class TopActivity extends ActionBarActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_alpha, new TopFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class TopFragment extends Fragment {

        public TopFragment(){

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_top,container,false);
            new TopTask(getActivity(),rootView)
                    .execute(getActivity().getIntent().getStringExtra("SPOTIFY_ID"));
            return rootView;
        }

        public static class TopTask extends AsyncTask<String,Void,Bundle>{
            private static final String LOG_TAG = TopTask.class.getSimpleName();
            Context context;
            View rootView;

            public TopTask(Context context, View rootView){
                this.context = context;
                this.rootView = rootView;
            }

            @Override
            protected Bundle doInBackground(String... params) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(context);
                String country = sharedPrefs.getString(context.getString(R.string.pref_country_key),
                        context.getString(R.string.pref_country_default));

                Map<String, Object> map = new HashMap<>();
                Log.v(LOG_TAG,country);
                map.put("country", country);
                Tracks tracks = spotify.getArtistTopTrack(params[0],map);
                int size = tracks.tracks.size();

                if(size != 0){
                    String[] trackTitle = new String[size];
                    String[] trackAlbum = new String[size];
                    String[] trackImage = new String[size];
                    for(int x=0;x<size;x++){
                        trackTitle[x]=tracks.tracks.get(x).name;
                        trackAlbum[x]=tracks.tracks.get(x).album.name;
                        if (tracks.tracks.get(x).album.images.toString() != "[]") {
                            trackImage[x]=tracks.tracks.get(x).album.images.get(0).url;
                        }
                    }

                    Bundle bundle = new Bundle();
                    bundle.putStringArray("TRACK_TITLE",trackTitle);
                    bundle.putStringArray("TRACK_ALBUM",trackAlbum);
                    bundle.putStringArray("TRACK_IMAGE",trackImage);

                    return bundle;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bundle bundle) {
                if(bundle != null) {
                    String[] trackTitle = bundle.getStringArray("TRACK_TITLE");
                    String[] trackAlbum = bundle.getStringArray("TRACK_ALBUM");
                    String[] trackImage = bundle.getStringArray("TRACK_IMAGE");

                    TopAdapter topAdapter = new TopAdapter(context,trackTitle,trackAlbum,trackImage);
                    ListView listTop = (ListView)rootView.findViewById(R.id.listTop);
                    listTop.setAdapter(topAdapter);
                }
                else{
                    Toast.makeText(context, "There are no Artist with that name", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
