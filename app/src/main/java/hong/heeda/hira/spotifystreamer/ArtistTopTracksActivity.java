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

        Intent intent = getIntent();

        mArtistId = intent.getStringExtra(Intent.EXTRA_TEXT);
        String artistName = intent.getStringExtra(MainActivity.ARTIST_NAME);

        getSupportActionBar().setSubtitle(artistName);
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
