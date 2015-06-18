package hong.heeda.hira.spotifystreamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class ArtistTopTracksActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        //TODO: handle this more elegantly...
        Intent intent = getIntent();

        String artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
        String artistName = intent.getStringExtra(MainActivity.ARTIST_NAME);

        getActionBar().setSubtitle(artistName);

        ArtistTracksFragment fragment = (ArtistTracksFragment)
                getFragmentManager ().findFragmentById(R.id.fragment_main);

        fragment.retrieveTracks(artistId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
}
