package hong.heeda.hira.spotifystreamer;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity
        implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    public final static String ARTIST_NAME = "ARTIST_NAME";

    private ArtistsFragment artistsFragment;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setOnActionExpandListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setQueryHint(getString(R.string.action_search));
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                if (!new NetworkManager().hasNetworkConnection(this)) {
                    item.collapseActionView();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        artistsFragment = (ArtistsFragment)
                getFragmentManager().findFragmentById(R.id.fragment_main);

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
}
