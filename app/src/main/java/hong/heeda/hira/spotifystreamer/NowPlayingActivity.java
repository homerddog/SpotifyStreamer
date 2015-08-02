package hong.heeda.hira.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import hong.heeda.hira.spotifystreamer.models.Playlist;

public class NowPlayingActivity extends AppCompatActivity {

    private final String LOG_TAG = NowPlayingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);

        Playlist playlist = getIntent().getParcelableExtra(Playlist.PLAYLIST);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            PlayerFragment fragment = new PlayerFragment();
            Bundle arguments = new Bundle();
            arguments.putParcelable(Playlist.PLAYLIST, playlist);

            fragment.setArguments(arguments);

            transaction.add(android.R.id.content, fragment).commit();
        }
    }
}
