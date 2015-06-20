package hong.heeda.hira.spotifystreamer;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;

public class ArtistsFragment extends Fragment {

    private final String LOG_TAG = ArtistsFragment.class.getSimpleName();
    private final String ARTIST_QUERY_KEY = "ARTIST_KEY";

    private ArtistAdapter artistAdapter;
    private String artistToSearch;

    public ArtistsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            artistToSearch = savedInstanceState.getString(ARTIST_QUERY_KEY);
        }

        artistAdapter = new ArtistAdapter(
                getActivity(),
                R.layout.artist_list_item,
                new ArrayList<Artist>()
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView artistsListView = (ListView) rootView.findViewById(R.id.artist_list_view);

        artistsListView.setAdapter(artistAdapter);
        artistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                Artist artist = artistAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), ArtistTopTracksActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, artist.id)
                        .putExtra(MainActivity.ARTIST_NAME, artist.name);

                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        startSearch(artistToSearch);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARTIST_QUERY_KEY, artistToSearch);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        artistAdapter = null;
    }

    public void startSearch(String artist) {
        if (artist != null && !artist.isEmpty()) {
            artistToSearch = artist;
            if (new NetworkManager().hasNetworkConnection(getActivity())) {
                new FetchArtistTask().execute(artistToSearch);
            } else {
                Toast.makeText(getActivity(), "No Network Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void resetAdapter() {
        artistAdapter.clear();
    }

    private class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();

        private Exception exception = null;

        protected List<Artist> doInBackground(String... params) {

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotifyService = api.getService();

                return spotifyService.searchArtists(params[0]).artists.items;

            } catch (Exception e) {
                exception = e;
                Log.e(LOG_TAG, e.getMessage());
            }

            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            super.onPostExecute(result);

            if (exception != null) {
                Toast.makeText(getActivity(),
                        getString(R.string.load_artists_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (result.size() == 0) {
                Toast.makeText(getActivity(),
                        getString(R.string.no_artists),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            resetAdapter();
            artistAdapter.addAll(result);
        }
    }
}
