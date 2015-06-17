package hong.heeda.hira.spotifystreamer;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by heeda on 6/16/15.
 */
public class ArtistsFragment extends Fragment {

    private final String LOG_TAG = ArtistsFragment.class.getSimpleName();

    private static ArtistAdapter mArtistAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mArtistAdapter = new ArtistAdapter(getActivity());

        ListView artistsView = (ListView)rootView.findViewById(R.id.artist_list_view);
        artistsView.setAdapter(mArtistAdapter);

        return rootView;
    }

    public static class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();

        protected List<Artist> doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            return spotifyService.searchArtists(params[0]).artists.items;
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            if (result != null) {
                mArtistAdapter.setArtists(result);
            }
        }
    }
}
