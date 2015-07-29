package hong.heeda.hira.spotifystreamer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {
    public boolean hasNetworkConnection(Context context) {
        if (context == null) {
            return false;
        }

        NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
