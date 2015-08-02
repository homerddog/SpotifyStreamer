package hong.heeda.hira.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hong.heeda.hira.spotifystreamer.models.ArtistInfo;
import hong.heeda.hira.spotifystreamer.models.Playlist;
import hong.heeda.hira.spotifystreamer.models.TrackInfo;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

public class ArtistTracksFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TRACKSFRAGMENT_TAG = "ATTAG";
    private static final String LOG_TAG = ArtistTracksFragment.class.getSimpleName();
    private static final String TRACK_LIST = "TRACK_LIST";

    private ArrayAdapter<TrackInfo> mTrackAdapter;
    private ListView mTrackListView;
    private ArrayList<TrackInfo> mTracks;
    private ArtistInfo mArtistInfo;

    public ArtistTracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mArtistInfo = args.getParcelable(MainActivity.ARTIST_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);
        mTrackListView = (ListView) rootView.findViewById(R.id.list_view);
        mTrackListView.setOnItemClickListener(this);
        mTrackListView.setAdapter(mTrackAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mTracks = (ArrayList<TrackInfo>) savedInstanceState.get(TRACK_LIST);
        } else {
            mTracks = new ArrayList<>();

            if (mArtistInfo != null) {
                retrieveTracks(mArtistInfo.getId());
            }
        }
        mTrackAdapter = new TrackAdapter(
                getActivity(),
                R.layout.artist_list_item,
                mTracks
        );
        mTrackAdapter.notifyDataSetChanged();
        mTrackListView.setAdapter(mTrackAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TRACK_LIST, mTracks);
    }

    public void retrieveTracks(String artistId) {
        new FetchArtistTracks().execute(artistId);
    }

    private void onTaskComplete(List<Track> result) {
        if (result.size() == 0) {
            Toast.makeText(getActivity(),
                    getString(R.string.no_tracks),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mTrackAdapter.clear();

        for (Track track : result) {
            mTrackAdapter.add(new TrackInfo(track));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
        Playlist playlist = new Playlist(mArtistInfo, mTracks, position);

        if (MainActivity.isLargeLayout()) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            PlayerFragment fragment = new PlayerFragment();
            Bundle arguments = new Bundle();
            arguments.putParcelable(Playlist.PLAYLIST, playlist);

            fragment.setArguments(arguments);
            fragment.show(fragmentManager, "PlayerFragment");

        } else {
            Intent intent = new Intent(getActivity(), NowPlayingActivity.class)
                    .putExtra(Playlist.PLAYLIST, playlist);
            startActivity(intent);
        }
    }

    private class FetchArtistTracks extends AsyncTask<String, Void, List<Track>> {

        private RetrofitError exception = null;

        @Override
        protected List<Track> doInBackground(String... params) {
            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotifyService = api.getService();

                Map<String, Object> queryMap = new HashMap<String, Object>() {
                };

                queryMap.put("country", "US");

                return spotifyService.getArtistTopTrack(params[0], queryMap).tracks;
            } catch (RetrofitError e) {
                exception = e;
                Log.e(LOG_TAG, e.getMessage());
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Track> result) {
            super.onPostExecute(result);

            if (exception != null) {
                Toast.makeText(getActivity(),
                        getString(R.string.load_tracks_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            onTaskComplete(result);
        }
    }
}