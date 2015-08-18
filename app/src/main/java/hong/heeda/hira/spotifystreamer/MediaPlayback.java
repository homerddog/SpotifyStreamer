package hong.heeda.hira.spotifystreamer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.session.PlaybackState;
import android.os.PowerManager;
import android.util.Log;

import hong.heeda.hira.spotifystreamer.models.TrackInfo;
import hong.heeda.hira.spotifystreamer.service.MusicService;

public class MediaPlayback implements Playback, OnCompletionListener, OnErrorListener,
        OnPreparedListener, OnSeekCompleteListener {

    private Callback mCallback;
    private MusicService mMusicService;
    private MediaPlayer mMediaPlayer;
    private int mState;

    public MediaPlayback(MusicService musicService) {
        mMusicService = musicService;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop(boolean notifyListeners) {
        mState = PlaybackState.STATE_STOPPED;
        if (notifyListeners && mCallback != null) {
            mCallback.onPlaybackStatusChanged(mState);
        }

        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void pause() {

    }

    @Override
    public void play(TrackInfo track) {
        try {
            initializeMediaPlayer();

            mState = PlaybackState.STATE_BUFFERING;

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(track.getPreviewUrl());
            mMediaPlayer.prepareAsync();

            //notify listener
            if (mCallback != null) {
                mCallback.onPlaybackStatusChanged(mState);
            }

        } catch (Exception e) {
            Log.d("MediaPlayback", "Exception playing song");
        }
    }

    @Override
    public boolean isPlaying() {
        return true;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mState = PlaybackState.STATE_STOPPED;
        if (mCallback != null) {
            mCallback.onCompletion();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp,
                           int what,
                           int extra) {
        if (mCallback != null) {
            mCallback.onError("MediaPlayer Error" + what + "(" + extra + ")");
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mState = PlaybackState.STATE_PLAYING;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        // TODO: implement in the case of continuous top ten tracks playback
    }

    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setWakeMode(mMusicService.getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);

            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
        } else {
            mMediaPlayer.reset();
        }
    }
}
