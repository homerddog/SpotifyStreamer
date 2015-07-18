package hong.heeda.hira.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

public class ArtistTopTracksActivity extends AppCompatActivity {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_search);

        return true;
    }
}
