package hong.heeda.hira.spotifystreamer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener,
        MenuItemCompat.OnActionExpandListener, ArtistsFragment.Callback {

    public final static String ARTIST_NAME = "ARTIST_NAME";

    private ArtistsFragment artistsFragment;
    private SearchView mSearchView;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_tracks) != null) {
            //tablet view, or smallest width of 600
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_tracks,
                                new ArtistTracksFragment(),
                                ArtistTracksFragment.TRACKSFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if (mSearchView != null) {
            mSearchView.setQueryHint(getString(R.string.action_search));
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                if (!new NetworkManager().hasNetworkConnection(this)) {
                    MenuItemCompat.collapseActionView(item);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        artistsFragment = ((ArtistsFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_artist));

        artistsFragment.startSearch(query);
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        if (!new NetworkManager().hasNetworkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_network),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        if (artistsFragment != null) {
            artistsFragment.resetAdapter();
        }
        return true;
    }

    @Override
    public void onItemSelected(ArtistInfo artist) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putString(MainActivity.ARTIST_NAME, artist.getId());

            ArtistTracksFragment fragment = new ArtistTracksFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_tracks, fragment, ArtistsFragment.ARTISTFRAGMENT_TAG)
                    .commit();

            fragment.retrieveTracks(artist.getId());
        } else {
            Intent intent = new Intent(this, ArtistTopTracksActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, artist.getId())
                    .putExtra(MainActivity.ARTIST_NAME, artist.getName());
            startActivity(intent);
        }
    }
}
