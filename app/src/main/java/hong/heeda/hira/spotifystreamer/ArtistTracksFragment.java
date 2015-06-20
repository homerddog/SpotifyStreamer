package hong.heeda.hira.spotifystreamer;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class ArtistTracksFragment extends Fragment {

    private static final String LOG_TAG = ArtistTracksFragment.class.getSimpleName();

    private ArrayAdapter<Track> trackAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        trackAdapter = new TrackAdapter(
                getActivity(),
                R.layout.artist_list_item,
                new ArrayList<Track>()
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.artist_list_view);

        lv.setAdapter(trackAdapter);

        return rootView;
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

        trackAdapter.clear();
        trackAdapter.addAll(result);
    }

    private class FetchArtistTracks extends AsyncTask<String, Void, List<Track>> {

        private Exception exception = null;

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
                exception = e;
                Log.e(LOG_TAG, e.getMessage());
            }

            return tracks;
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