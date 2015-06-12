package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.container_alpha, new SearchFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SearchFragment extends Fragment {
        EditText edtSearch;
        ListView listSearch;
        private final String LOG_TAG = SearchFragment.class.getSimpleName();

        public SearchFragment(){

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_search,container,false);
            edtSearch = (EditText)rootView.findViewById(R.id.edtSearch);
            edtSearch.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        new SearchTask(getActivity(),rootView).execute(edtSearch.getText().toString());
                        return true;
                    }
                    return false;
                }
            });

            return rootView;
        }
    }

    public static class SearchTask extends AsyncTask<String,Void,Bundle>{
        private final String LOG_TAG = SearchTask.class.getSimpleName();
        Context context;
        View rootView;

        public SearchTask(Context context, View rootView){
            this.context = context;
            this.rootView = rootView;
        }

        @Override
        protected Bundle doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager results = spotify.searchArtists(params[0]);
            int artistSize = results.artists.items.size();

            if(artistSize != 0){
                String[] artistNames = new String[artistSize];
                String[] artistImages = new String[artistSize];
                String[] spotifyId = new String[artistSize];
                for (int x = 0; x < results.artists.items.size(); x++) {
                    artistNames[x] = results.artists.items.get(x).name;
                    spotifyId[x] = results.artists.items.get(x).id;
                    if (results.artists.items.get(x).images.toString() != "[]") {
                        artistImages[x] = results.artists.items.get(x).images.get(0).url;
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putStringArray("NAMES", artistNames);
                bundle.putStringArray("IMAGES", artistImages);
                bundle.putStringArray("SPOTIFY_ID", spotifyId);
                return bundle;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            if(bundle != null) {
                String[] artistNames = bundle.getStringArray("NAMES");
                String[] artistImages = bundle.getStringArray("IMAGES");
                final String[] spotifyId = bundle.getStringArray("SPOTIFY_ID");

                SearchAdapter adapter = new SearchAdapter(artistNames, artistImages, context);
                ListView listSearch = (ListView) rootView.findViewById(R.id.listSearch);
                listSearch.setAdapter(adapter);

                listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(context,TopActivity.class).putExtra("SPOTIFY_ID",spotifyId[position]);
                        context.startActivity(i);
                    }
                });
            }
            else{
                Toast.makeText(context,"There are no Artist with that name",Toast.LENGTH_LONG).show();
            }
        }
    }
}
