package hong.heeda.hira.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import hong.heeda.hira.spotifystreamer.models.ArtistInfo;

public class ArtistTopTracksActivity extends AppCompatActivity {

    private final String LOG_TAG = ArtistTopTracksActivity.class.getSimpleName();
    private ArtistInfo mArtist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        mArtist = getIntent().getParcelableExtra(MainActivity.ARTIST_INFO);

        if (savedInstanceState == null) {
            ArtistTracksFragment fragment = new ArtistTracksFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_main, fragment)
                    .commit();

            fragment.retrieveTracks(mArtist.getId());
        }
        getSupportActionBar().setSubtitle(mArtist.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_search);
        return true;
    }
}
