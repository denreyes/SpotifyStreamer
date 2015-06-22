package com.example.android.spotifystreamer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by DJ on 6/21/2015.
 */

public class SearchFragment extends Fragment {
    @InjectView(R.id.edtSearch) SearchView edtSearch;
    @InjectView(R.id.listSearch) ListView listSearch;
    private final String LOG_TAG = SearchFragment.class.getSimpleName();
    static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
    ArrayList<SearchObject> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, rootView);
        edtSearch.onActionViewExpanded();

        if(savedInstanceState!=null) {
            edtSearch.setQuery(savedInstanceState.getString("SEARCH_QUERY"), false);
            list = savedInstanceState.getParcelableArrayList("SEARCH_OBJECT");
            SearchAdapter adapter = new SearchAdapter(getActivity(),list);
            listSearch.setAdapter(adapter);
        }

        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(list.get(position)!=null){
                    ((SearchCallback)getActivity()).onItemSelected(list.get(position).spotifyId);
                }
//                Intent i = new Intent(getActivity(), TopActivity.class).putExtra("SPOTIFY_ID", list.get(position).spotifyId);
//                getActivity().startActivity(i);
            }
        });
        fetchSpotify();
        return rootView;
    }

    private void fetchSpotify(){
        edtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                SpotifyApi api = new SpotifyApi();
                final SpotifyService spotify = api.getService();
                final String searchedName = edtSearch.getQuery().toString();
                spotify.searchArtists(searchedName, new Callback<ArtistsPager>() {
                    @Override
                    public void success(ArtistsPager artistsPager, Response response) {
                        querySuccess(artistsPager);
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
    }

    private void querySuccess(ArtistsPager artistsPager) {
        int artistSize = artistsPager.artists.items.size();

        if (artistSize != 0) {
                String artistNames,artistImages,spotifyId;
            list = new ArrayList<SearchObject>();
            for (int x = 0; x < artistsPager.artists.items.size(); x++) {
                artistImages="";
                artistNames = artistsPager.artists.items.get(x).name;
                spotifyId = artistsPager.artists.items.get(x).id;
                if (artistsPager.artists.items.get(x).images.toString() != "[]") {
                    artistImages = artistsPager.artists.items.get(x).images.get(0).url;
                }
                list.add(new SearchObject(artistNames, artistImages, spotifyId));
            }

            MAIN_THREAD.post(new Runnable() {
                @Override
                public void run() {
                    SearchAdapter adapter = new SearchAdapter(getActivity(),list);
                    listSearch.setAdapter(adapter);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String searchQuery = edtSearch.getQuery().toString();
        outState.putString("SEARCH_QUERY",searchQuery);
        outState.putParcelableArrayList("SEARCH_OBJECT",list);
    }

    public interface SearchCallback {
        public void onItemSelected(String spotifyId);
    }
}