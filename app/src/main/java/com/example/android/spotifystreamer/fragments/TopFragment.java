package com.example.android.spotifystreamer.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.adapter.TopAdapter;
import com.example.android.spotifystreamer.activities.PlayerActivity;
import com.example.android.spotifystreamer.object.TopObject;

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
 * Created by DJ on 6/21/2015.
 */

public class TopFragment extends Fragment {
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

        Bundle arguments = getArguments();

        if(savedInstanceState!=null) {
            list = savedInstanceState.getParcelableArrayList("TOP_OBJECT");
            TopAdapter adapter = new TopAdapter(getActivity(),list);
            listTop.setAdapter(adapter);
        }
        if(arguments!=null) {
            fetchSpotify(getArguments().getString("SPOTIFY_ID"));
        }

        listTop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(),PlayerActivity.class);
                i.putParcelableArrayListExtra("TOP_OBJECT",list);
                i.putExtra("POSITION",position);
//                i.putExtra("TRACK_TITLE", list.get(position).trackTitle);
//                i.putExtra("TRACK_ALBUM", list.get(position).trackAlbum);
//                i.putExtra("TRACK_IMAGE", list.get(position).trackImage);
//                i.putExtra("TRACK_ARTIST", list.get(position).trackArtist);
//                i.putExtra("TRACK_PLAY", list.get(position).trackPlay);
                startActivity(i);
            }
        });

        return rootView;
    }

    private void fetchSpotify(String spotifyId) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String country = sharedPrefs.getString(getActivity().getString(R.string.pref_country_key),
                getActivity().getString(R.string.pref_country_default));

        Map<String, Object> map = new HashMap<>();
        map.put("country", country);
        spotify.getArtistTopTrack(spotifyId, map,
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
            String trackTitle, trackAlbum, trackPlay, trackImage="";
            StringBuffer trackArtist;
            double trackDuration;
            list = new ArrayList<>();

            for (int x = 0; x < size; x++) {
                    trackArtist = new StringBuffer();

                int trackSize = tracks.tracks.get(x).artists.size();
                for(int y=0; y < trackSize; y++)
                    trackArtist.append(tracks.tracks.get(x).artists.get(y).name + ", ");
                trackArtist.delete(trackArtist.length()-2,trackArtist.length()-1);
                trackPlay = tracks.tracks.get(x).preview_url;

                trackTitle = tracks.tracks.get(x).name;
                trackAlbum = tracks.tracks.get(x).album.name;
                trackImage = "";
                if (tracks.tracks.get(x).album.images.toString() != "[]") {
                    trackImage = tracks.tracks.get(x).album.images.get(0).url;
                }
                list.add(new TopObject(trackTitle,trackAlbum,trackImage,trackArtist.toString(),trackPlay));
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