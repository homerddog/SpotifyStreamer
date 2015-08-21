package hong.heeda.hira.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import hong.heeda.hira.spotifystreamer.MediaPlayback;
import hong.heeda.hira.spotifystreamer.Playback;
import hong.heeda.hira.spotifystreamer.models.Playlist;
import hong.heeda.hira.spotifystreamer.models.TrackInfo;

public class MusicService extends Service {

    private static final String TAG = MusicService.class.getSimpleName();
    private final IBinder mMusicBinder = new MusicBinder();
    private final Playback.Callback mCallback = new Playback.Callback() {
        @Override
        public void onCompletion() {
            mMediaPlayback.stop(true);
            updatePlaybackState();
            stopSelf();
        }

        @Override
        public void onPlaybackStatusChanged(int state) {
            updatePlaybackState();
        }

        @Override
        public void onError(String error) {

        }

        @Override
        public void onMetadataChanged(String mediaId) {

        }
    };
    private Playback mMediaPlayback;
    private Playlist mPlaylist;
    private MediaSession mSession;

    @Override
    public void onCreate() {
        super.onCreate();

        mSession = new MediaSession(getApplicationContext(), "MusicService");
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setActive(true);

        mMediaPlayback = new MediaPlayback(this);
        mMediaPlayback.setState(PlaybackState.STATE_NONE);
        mMediaPlayback.setCallback(mCallback);
        mMediaPlayback.start();
    }

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSession.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    /**
     * Set the current playlist for the MediaPlayer.
     *
     * @param playlist
     */
    public void setPlaylist(Playlist playlist) {
        mPlaylist = playlist;
    }

    public MediaSession getSession() {
        return mSession;
    }

    private void updatePlaybackState() {
        int currentState = mMediaPlayback.getState();
        long position = PlaybackState.PLAYBACK_POSITION_UNKNOWN;
        PlaybackState.Builder builder = new PlaybackState.Builder();

        if (mSession.getController().getPlaybackState() != null) {
            position = mSession.getController().getPlaybackState().getPosition();
        }

        builder.setState(currentState, position, 1.0f);
        mSession.setPlaybackState(builder.build());
    }

    private void playTrack() {
        TrackInfo track = mPlaylist.getTrack();
//        MediaMetadata metadata = new MediaMetadata();
//
//        mSession.setMetadata(metadata);
        //implement exception handling
        mMediaPlayback.play(track);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
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
            playTrack();
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
            mPlaylist.nextTrack();
            playTrack();
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
}