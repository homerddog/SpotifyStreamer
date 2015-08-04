package hong.heeda.hira.spotifystreamer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.otto.Produce;

import hong.heeda.hira.spotifystreamer.NowPlayingActivity;
import hong.heeda.hira.spotifystreamer.events.ProgressChangedEvent;
import hong.heeda.hira.spotifystreamer.models.Playlist;
import hong.heeda.hira.spotifystreamer.models.TrackInfo;
import hong.heeda.hira.spotifystreamer.utils.BusProvider;

public class MusicService extends Service implements
        OnPreparedListener, OnErrorListener, OnCompletionListener {

    private static final String TAG = MusicService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    private MediaPlayer mMediaPlayer;
    private Playlist mPlaylist;
    private int mTrackPosition;
    private final IBinder mMusicBinder = new MusicBinder();

    private Handler mHandler = new Handler();
    private Runnable mProgressUpdate = new Runnable() {
        @Override
        public void run() {
            BusProvider.getInstance().post(publishProgressChange());

            mHandler.postDelayed(this, 1000);
        }
    };

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

        mProgressUpdate.run();

        Intent notIntent = new Intent(this, NowPlayingActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notIntent.putExtra(Playlist.PLAYLIST, mPlaylist);

        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        TrackInfo trackInfo = mPlaylist.getTrack();

        builder.setContentIntent(pendInt)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setTicker(trackInfo.getName())
                .setOngoing(true)
                .setContentTitle(trackInfo.getArtist() + " " + trackInfo.getAlbum())
        .setContentText(trackInfo.getName());
        Notification not = builder.build();

        startForeground(NOTIFICATION_ID, not);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTrackPosition = 0;
        mMediaPlayer = new MediaPlayer();

        BusProvider.getInstance().register(this);
        initializeMediaPlayer();
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        stopForeground(true);
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;

        super.onDestroy();
    }

    @Produce
    public ProgressChangedEvent publishProgressChange() {
        return new ProgressChangedEvent(mMediaPlayer.getCurrentPosition());
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
        mTrackPosition = mPlaylist.getCurrentTrackPosition();
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
                mTrackPosition++;
                if (mTrackPosition >= mPlaylist.getTracks().size()) {
                    mTrackPosition = 0;
                }
                mPlaylist.setCurrentTrackPosition(mTrackPosition);
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mPlaylist.getTrack().getPreviewUrl());
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {

            }
        }
    }

    public void skipToPrevious() {
        if (mMediaPlayer != null) {
            try {
                mTrackPosition--;
                if (mTrackPosition < 0) {
                    mTrackPosition = mPlaylist.getTracks().size() - 1;
                }
                mPlaylist.setCurrentTrackPosition(mPlaylist.getCurrentTrackPosition() + 1);
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mPlaylist.getTrack().getPreviewUrl());
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {

            }
        }
    }

    public int getPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    public void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
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