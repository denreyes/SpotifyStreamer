package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_alpha, new SearchFragment()).commit();
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

    public static class SearchFragment extends Fragment {
        @InjectView(R.id.edtSearch) SearchView edtSearch;
        @InjectView(R.id.listSearch) ListView listSearch;
        private final String LOG_TAG = SearchFragment.class.getSimpleName();
        static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
        final Bundle bundle = new Bundle();

        public SearchFragment(){

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            final View rootView = inflater.inflate(R.layout.fragment_search,container,false);
            if(savedInstanceState==null) {
                ButterKnife.inject(this, rootView);
                edtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        SpotifyApi api = new SpotifyApi();
                        final SpotifyService spotify = api.getService();
                        final String searchedName = edtSearch.getQuery().toString();
                        spotify.searchArtists(searchedName, new Callback<ArtistsPager>() {
                            @Override
                            public void success(ArtistsPager artistsPager, Response response) {
                                int artistSize = artistsPager.artists.items.size();

                                if (artistSize != 0) {
                                    String[] artistNames = new String[artistSize];
                                    String[] artistImages = new String[artistSize];
                                    String[] spotifyId = new String[artistSize];
                                    for (int x = 0; x < artistsPager.artists.items.size(); x++) {
                                        artistNames[x] = artistsPager.artists.items.get(x).name;
                                        spotifyId[x] = artistsPager.artists.items.get(x).id;
                                        if (artistsPager.artists.items.get(x).images.toString() != "[]") {
                                            artistImages[x] = artistsPager.artists.items.get(x).images.get(0).url;
                                        }
                                    }
                                    bundle.putString("SEARCH_NAME",searchedName);
                                    bundle.putStringArray("NAMES", artistNames);
                                    bundle.putStringArray("IMAGES", artistImages);
                                    bundle.putStringArray("SPOTIFY_ID", spotifyId);

                                    MAIN_THREAD.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.v(LOG_TAG, bundle.getStringArray("NAMES").length + "");
                                            String[] artistNames = bundle.getStringArray("NAMES");
                                            String[] artistImages = bundle.getStringArray("IMAGES");
                                            final String[] spotifyId = bundle.getStringArray("SPOTIFY_ID");

                                            SearchAdapter adapter = new SearchAdapter(artistNames, artistImages, getActivity());
                                            listSearch.setAdapter(adapter);

                                            listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    Intent i = new Intent(getActivity(), TopActivity.class).putExtra("SPOTIFY_ID", spotifyId[position]);
                                                    getActivity().startActivity(i);
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    MAIN_THREAD.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "There are no Artist with that name", Toast.LENGTH_LONG).show();
                                            listSearch.setAdapter(null);
                                        }
                                    });
                                }
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
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });

                super.onSaveInstanceState(bundle);
            }
            return rootView;
        }
    }
}
