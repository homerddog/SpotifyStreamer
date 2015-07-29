package hong.heeda.hira.spotifystreamer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import hong.heeda.hira.spotifystreamer.models.TrackInfo;

public class TrackPlayerActivity extends AppCompatActivity
    implements PlayerFragment.OnFragmentInteractionListener {

    private final String LOG_TAG = TrackPlayerActivity.class.getSimpleName();
    private boolean mLargeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);

        Intent intent = getIntent();
        TrackInfo track = intent.getParcelableExtra(TrackInfo.TRACK_INFO);

        mLargeLayout = getResources().getBoolean(R.bool.large_layout);

        if (mLargeLayout) {

        } else {
            if (savedInstanceState == null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                PlayerFragment fragment = new PlayerFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable(TrackInfo.TRACK_INFO, track);

                fragment.setArguments(arguments);

                transaction.add(android.R.id.content, fragment).commit();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
