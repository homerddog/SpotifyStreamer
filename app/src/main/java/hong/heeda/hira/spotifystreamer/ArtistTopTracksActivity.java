package hong.heeda.hira.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

public class ArtistTopTracksActivity extends AppCompatActivity
        implements ArtistTracksFragment.Callback {

    private final String LOG_TAG = ArtistTopTracksActivity.class.getSimpleName();
    private ArtistInfo mArtist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        Intent intent = getIntent();
        mArtist = intent.getParcelableExtra(MainActivity.ARTIST_INFO);

        if (savedInstanceState == null) {
            ArtistTracksFragment fragment = new ArtistTracksFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_main, fragment)
                    .commit();

            fragment.retrieveTracks(mArtist.getId());
        }
        getSupportActionBar().setSubtitle(mArtist.getName());
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p/>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p/>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p/>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p/>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_search);
        return true;
    }

    @Override
    public void onTrackSelected(TrackInfo track) {
        Intent intent = new Intent(this, TrackPlayerActivity.class)
                .putExtra(TrackInfo.TRACK_INFO, track);
        startActivity(intent);
    }
}
