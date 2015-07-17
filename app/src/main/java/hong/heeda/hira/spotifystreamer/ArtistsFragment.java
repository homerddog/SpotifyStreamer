package hong.heeda.hira.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import retrofit.RetrofitError;

public class ArtistsFragment extends Fragment {

    public static final String ARTISTFRAGMENT_TAG = "AF_TAG";
    private final String LOG_TAG = ArtistsFragment.class.getSimpleName();
    private final String ARTIST_QUERY_KEY = "ARTIST_KEY";

    private ArtistAdapter mArtistAdapter;
    private ListView mArtistListView;
    private ArrayList<ArtistInfo> mArtists;

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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mArtistListView = (ListView) rootView.findViewById(R.id.list_view);

        mArtistListView.setAdapter(mArtistAdapter);
        mArtistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                ((Callback)getActivity())
                        .onItemSelected(((ArtistInfo)parent.getAdapter().getItem(position)));
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(ARTIST_QUERY_KEY)) {
            mArtists = (ArrayList<ArtistInfo>) savedInstanceState.get(ARTIST_QUERY_KEY);
            setArtistAdapter();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mArtists = (ArrayList<ArtistInfo>) savedInstanceState.get(ARTIST_QUERY_KEY);
        } else {
            mArtists = new ArrayList<>();
        }

        setArtistAdapter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARTIST_QUERY_KEY, mArtists);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mArtistAdapter = null;
    }

    public void startSearch(String artist) {
        if (artist != null) {
            if (new NetworkManager().hasNetworkConnection(getActivity())) {
                new FetchArtistTask().execute(artist);
            } else {
                Toast.makeText(getActivity(),
                        getString(R.string.no_network),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void resetAdapter() {
        mArtistAdapter.clear();
        mArtistAdapter.notifyDataSetChanged();
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement.  This allows activities to be notified of item selections.
     */
    public interface Callback {
        /**
         * ArtistsFragment callback, for when an item is selected.
         *
         * @param artist The selected Artist.
         */
        void onItemSelected(ArtistInfo artist);
    }

    private void setArtistAdapter() {
        mArtistAdapter = new ArtistAdapter(
                getActivity(),
                R.layout.artist_list_item,
                mArtists
        );

        mArtistAdapter.notifyDataSetChanged();
        mArtistListView.setAdapter(mArtistAdapter);
    }

    private class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();

        private RetrofitError exception = null;

        protected List<Artist> doInBackground(String... params) {

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotifyService = api.getService();

                return spotifyService.searchArtists(params[0]).artists.items;

            } catch (RetrofitError e) {
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

            for (Artist artist : result) {
                String url = artist.images.size() > 0 ? artist.images.get(0).url : "";
                mArtists.add(new ArtistInfo(artist.id, artist.name, url));
            }
        }
    }
}
