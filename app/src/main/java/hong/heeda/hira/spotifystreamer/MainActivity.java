package hong.heeda.hira.spotifystreamer;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import kaaes.spotify.webapi.android.models.Artist;

public class MainActivity extends Activity implements
        ArtistsFragment.OnArtistSelectedListener {

    ArtistsFragment artistsFragment;
    public final static String ARTIST_NAME = "ARTIST_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search Artist");

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                artistsFragment = (ArtistsFragment)
                        getFragmentManager().findFragmentById(R.id.fragment_main);

                artistsFragment.startSearch(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnArtistSelected(int artistPosition) {
        Artist artist = (Artist) artistsFragment.getListAdapter().getItem(artistPosition);

        Intent intent = new Intent(this, ArtistTopTracksActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, artist.id);
        intent.putExtra(ARTIST_NAME, artist.name);

        startActivity(intent);
    }
}
