package hong.heeda.hira.spotifystreamer;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;

public class ArtistsFragment extends ListFragment {

    private final String LOG_TAG = ArtistsFragment.class.getSimpleName();

    OnArtistSelectedListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListAdapter artistAdapter = new ArtistAdapter(
                getActivity(),
                R.layout.artist_list_item,
                new ArrayList<Artist>() {
                });

        setListAdapter(artistAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l,
                                View v,
                                int position,
                                long id) {
        mCallback.OnArtistSelected(position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnArtistSelectedListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onArtistSearchListener");
        }
    }

    public interface OnArtistSelectedListener {
        void OnArtistSelected(int artist);
    }

    public void startSearch(String artist) {
        new FetchArtistTask().execute(artist);
    }

    private void onTaskComplete(List<Artist> result) {
        if (result.size() == 0) {
            Toast.makeText(getActivity(), "Artist not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<Artist> artistAdapter = (ArrayAdapter<Artist>) getListAdapter();
        artistAdapter.clear();

        artistAdapter.addAll(result);
    }

    private class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();

        protected List<Artist> doInBackground(String... params) {
            //TODO: handle retrofit exceptions
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            return spotifyService.searchArtists(params[0]).artists.items;
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            super.onPostExecute(result);
            onTaskComplete(result);
        }
    }
}
