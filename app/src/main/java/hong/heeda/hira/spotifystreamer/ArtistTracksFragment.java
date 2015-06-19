package hong.heeda.hira.spotifystreamer;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class ArtistTracksFragment extends ListFragment {

    private static final String LOG_TAG = ArtistTracksFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListAdapter trackAdapter = new TrackAdapter(
                getActivity(),
                R.layout.artist_list_item,
                new ArrayList<Track>()
        );

        setListAdapter(trackAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l,
                                View v,
                                int position,
                                long id) {
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

        ArrayAdapter<Track> trackAdapter = (ArrayAdapter<Track>) getListAdapter();
        trackAdapter.clear();
        trackAdapter.addAll(result);

        setListAdapter(trackAdapter);
    }

    private class FetchArtistTracks extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... params) {
            List<Track> tracks = new ArrayList<Track>();

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotifyService = api.getService();

                Map<String, Object> queryMap = new HashMap<String, Object>() {
                };

                queryMap.put("country", "US");

                tracks = spotifyService.getArtistTopTrack(params[0], queryMap).tracks;
            } catch (Exception e) {
                Toast.makeText(getActivity(),
                        getString(R.string.load_tracks_error),
                        Toast.LENGTH_LONG).show();

                Log.e(LOG_TAG, e.getMessage());
            }

            return tracks;
        }

        @Override
        protected void onPostExecute(List<Track> result) {
            super.onPostExecute(result);
            onTaskComplete(result);
        }
    }
}