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
    private int mCurrentPosition;

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
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
        }
        mState = PlaybackState.STATE_PAUSED;
        notifyListener();
    }

    @Override
    public void play(TrackInfo track) {
        if (mState == PlaybackState.STATE_PAUSED && mMediaPlayer != null) {
            configureMediaPlayer();
        } else {
            try {
                initializeMediaPlayer();

                mState = PlaybackState.STATE_BUFFERING;

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(track.getPreviewUrl());
                mMediaPlayer.prepareAsync();

                notifyListener();

            } catch (Exception e) {
                Log.d("MediaPlayback", "Exception playing song" + e);
            }
        }
    }

    @Override
    public boolean isPlaying() {
        return true;
    }

    @Override
    public void setState(int state) {
        mState = state;
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
        configureMediaPlayer();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mCurrentPosition = mMediaPlayer.getCurrentPosition();
        if (mState == PlaybackState.STATE_BUFFERING) {
            mMediaPlayer.start();
            mState = PlaybackState.STATE_PLAYING;
        }
        notifyListener();
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

    private void configureMediaPlayer() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            if (mCurrentPosition == mMediaPlayer.getCurrentPosition()) {
                mMediaPlayer.start();
                mState = PlaybackState.STATE_PLAYING;
            } else {
                mMediaPlayer.seekTo(mCurrentPosition);
                mState = PlaybackState.STATE_BUFFERING;
            }
        }
        notifyListener();
    }

    private void notifyListener() {
        if (mCallback != null) {
            mCallback.onPlaybackStatusChanged(mState);
        }
    }
}
