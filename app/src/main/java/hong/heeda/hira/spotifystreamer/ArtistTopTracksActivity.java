package hong.heeda.hira.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

public class ArtistTopTracksActivity extends AppCompatActivity {

    private String mArtistId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle arguments = new Bundle();
            String artistName = intent.getStringExtra(MainActivity.ARTIST_NAME);
            mArtistId = intent.getStringExtra(Intent.EXTRA_TEXT);

            arguments.putString(MainActivity.ARTIST_NAME, artistName);
            arguments.putString(Intent.EXTRA_TEXT, mArtistId);

            getSupportActionBar().setSubtitle(artistName);

            ArtistTracksFragment fragment = new ArtistTracksFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_main, fragment)
                    .commit();

            fragment.retrieveTracks(mArtistId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_search);

        return true;
    }

    public String getArtistId() {
        return mArtistId;
    }
}
