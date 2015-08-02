package hong.heeda.hira.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import hong.heeda.hira.spotifystreamer.models.Playlist;
import hong.heeda.hira.spotifystreamer.models.TrackInfo;

public class MusicService extends Service implements
        OnPreparedListener, OnErrorListener, OnCompletionListener {

    private static final String TAG = MusicService.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private Playlist mPlaylist;
    private int mTrackPosition;
    private final IBinder mMusicBinder = new MusicBinder();

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
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp,
                           int what,
                           int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mTrackPosition = 0;
        mMediaPlayer = new MediaPlayer();

        initializeMediaPlayer();
    }

    /**
     * Set the current playlist for the MediaPlayer.
     * @param playlist
     */
    public void setPlaylist(Playlist playlist) {
        mPlaylist = playlist;
    }

    public void playTrack() {
        mMediaPlayer.reset();
        TrackInfo track = mPlaylist.getTracks().get(mPlaylist.getCurrentTrackPosition());

        try {
            mMediaPlayer.setDataSource(track.getPreviewUrl());
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Error setting data source", e);
        }
    }

    public void skipToNext() {
        if (mMediaPlayer != null) {
            try {
                mPlaylist.setCurrentTrackPosition(mPlaylist.getCurrentTrackPosition() + 1);
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mPlaylist.getTrack().getPreviewUrl());
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {

            }
        }
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public interface PlaybackListener {
        void onPlay(TrackInfo track);
        void onStop();
        void onSkipNext(TrackInfo track);
        void onSkipPrevious(TrackInfo track);
    }

    private boolean isInSafeState() {
        return false;
    }

    private void initializeMediaPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }
}