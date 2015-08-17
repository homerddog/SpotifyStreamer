package hong.heeda.hira.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity{
    private static boolean mLargeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLargeLayout = getResources().getBoolean(R.bool.large_layout);
    }

    public static boolean isLargeLayout() {
        return mLargeLayout;
    }
}
