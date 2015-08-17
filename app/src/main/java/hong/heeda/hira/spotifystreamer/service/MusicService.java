package hong.heeda.hira.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import hong.heeda.hira.spotifystreamer.MediaPlayback;
import hong.heeda.hira.spotifystreamer.Playback;
import hong.heeda.hira.spotifystreamer.models.Playlist;

public class MusicService extends Service {

    private static final String TAG = MusicService.class.getSimpleName();

    private MediaPlayback mMediaPlayback;

    private Playlist mPlaylist;
    private final IBinder mMusicBinder = new MusicBinder();
    private MediaSession mSession;

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSession = new MediaSession(getApplicationContext(), "MusicService");
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setActive(true);

        mMediaPlayback = new MediaPlayback(this);
        mMediaPlayback.setCallback(mCallback);
        mMediaPlayback.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSession.release();
    }

    /**
     * Set the current playlist for the MediaPlayer.
     * @param playlist
     */
    public void setPlaylist(Playlist playlist) {
        mPlaylist = playlist;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public MediaSession getSession() {
        return mSession;
    }

    private final class MediaSessionCallback extends MediaSession.Callback {
        @Override
        public void onCommand(String command,
                              Bundle args,
                              ResultReceiver cb) {
            super.onCommand(command, args, cb);
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
            return super.onMediaButtonEvent(mediaButtonIntent);
        }

        @Override
        public void onPlay() {
            //implement exception handling
            mMediaPlayback.play(mPlaylist.getTrack());
        }

        @Override
        public void onPlayFromMediaId(String mediaId,
                                      Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
        }

        @Override
        public void onPause() {
            mMediaPlayback.pause();
        }

        @Override
        public void onSkipToNext() {
            mMediaPlayback.skipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onStop() {
            mMediaPlayback.stop(true);
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }
    }

    private final Playback.Callback mCallback = new Playback.Callback() {
        @Override
        public void onCompletion() {

        }

        @Override
        public void onPlaybackStatusChanged(int state) {

        }

        @Override
        public void onError(String error) {

        }

        @Override
        public void onMetadataChanged(String mediaId) {

        }
    };
}