package hong.heeda.hira.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer = null;

    public MusicService() {
    }

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * MediaPlayer.OnPreparedListener implementation.  A Callback to notify
     * implementing classes that the MediaPlayer is prepared.
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
    }
}
