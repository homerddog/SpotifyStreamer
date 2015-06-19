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

    private ArtistAdapter artistAdapter;

    public ArtistsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ArrayList<Artist> savedArtists = (ArrayList<Artist>)
                    savedInstanceState.getSerializable("artistsKey");

            artistAdapter = new ArtistAdapter(
                    getActivity(),
                    R.layout.artist_list_item,
                    savedArtists
            );
        } else {
            artistAdapter = new ArtistAdapter(
                    getActivity(),
                    R.layout.artist_list_item,
                    new ArrayList<Artist>()
            );
        }

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

                Intent intent = new Intent(getActivity(), ArtistTopTracksActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, artist.id);
                intent.putExtra(MainActivity.ARTIST_NAME, artist.name);

                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("artistsKey", artistAdapter.getArtists());
    }

    public void startSearch(String artist) {
        new FetchArtistTask().execute(artist);
    }

    private class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();

        protected List<Artist> doInBackground(String... params) {
            List<Artist> artists = new ArrayList<Artist>();

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotifyService = api.getService();

                artists = spotifyService.searchArtists(params[0]).artists.items;

            } catch (Exception e) {
                Toast.makeText(getActivity(),
                        getString(R.string.load_artists_error),
                        Toast.LENGTH_LONG).show();

                Log.e(LOG_TAG, e.getMessage());
            }
            return artists;
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            super.onPostExecute(result);

            if (result.size() == 0) {
                Toast.makeText(getActivity(),
                        getString(R.string.no_artists),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            artistAdapter.clear();
            artistAdapter.addAll(result);
        }
    }
}
