package hong.heeda.hira.spotifystreamer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import hong.heeda.hira.spotifystreamer.models.Playlist;
import hong.heeda.hira.spotifystreamer.models.TrackInfo;
import hong.heeda.hira.spotifystreamer.service.MusicService;

public class PlayerFragment extends DialogFragment {

    public static final String FRAGMENT_TAG = "PFTAG";
    private static final String TAG = PlayerFragment.class.getSimpleName();

    private Playlist mPlaylist;
    private TextView mArtist;
    private TextView mAlbum;
    private TextView mTrack;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private ImageView mAlbumImage;
    private ImageView mSkipNext;
    private ImageView mSkipPrev;
    private ImageView mPlayPause;
    private SeekBar mSeekBar;
    private TextView mCurrentPosition;
    private TextView mTrackLength;

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;
    private final Handler mHandler = new Handler();

    private MusicService mMusicService;
    private MediaSession mSession;
    private boolean mIsMusicBound;
    private Intent mPlayIntent;
    private PlaybackState mLastPlaybackState;

    private Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            Log.i(TAG, "PlaybackState changed " + state);
            updateViews(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            super.onMetadataChanged(metadata);
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mMusicService = binder.getService();

            if (mSession != null && !mSession.isActive()) {

            } else {
                mSession = mMusicService.getSession();
                mMusicService.setPlaylist(mPlaylist);
                mIsMusicBound = true;

                connectToSession(mSession.getSessionToken());
                //when the service is connected, begin playback?
                getActivity().getMediaController().getTransportControls().play();
                startSeekbarUpdate();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsMusicBound = false;
        }
    };

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media_player, container, false);
        mPauseDrawable = getActivity().getDrawable(android.R.drawable.ic_media_pause);
        mPlayDrawable = getActivity().getDrawable(android.R.drawable.ic_media_play);
        mArtist = (TextView) rootView.findViewById(R.id.artist_text_view);
        mAlbum = (TextView) rootView.findViewById(R.id.album_text_view);
        mTrack = (TextView) rootView.findViewById(R.id.track_text_view);
        mAlbumImage = (ImageView) rootView.findViewById(R.id.album_image_view);
        mSkipNext = (ImageView) rootView.findViewById(R.id.next_image_button);
        mSkipPrev = (ImageView) rootView.findViewById(R.id.previous_image_button);
        mPlayPause = (ImageView) rootView.findViewById(R.id.play_image_button);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.track_seekbar);
        mTrackLength = (TextView) rootView.findViewById(R.id.text_track_length);
        mCurrentPosition = (TextView) rootView.findViewById(R.id.text_current_position);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean mFromUser = false;

            @Override
            public void onProgressChanged(SeekBar seekBar,
                                          int progress,
                                          boolean fromUser) {
                mCurrentPosition.setText(String.valueOf(progress / 1000));
                mFromUser = fromUser;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mFromUser = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mFromUser = false;
            }
        });

        mSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaController.TransportControls controls =
                        getActivity().getMediaController().getTransportControls();

                controls.skipToNext();
            }
        });

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaController mediaController = getActivity().getMediaController();
                PlaybackState state = mediaController.getPlaybackState();

                if (state != null) {
                    MediaController.TransportControls controls =
                            mediaController.getTransportControls();

                    switch (state.getState()) {
                        case PlaybackState.STATE_PAUSED:
                        case PlaybackState.STATE_STOPPED:
                            if (controls != null) {
                                controls.play();
                            }
                            startSeekbarUpdate();
                            break;
                        case PlaybackState.STATE_PLAYING:
                        case PlaybackState.STATE_BUFFERING:
                            stopSeekbarUpdate();
                            controls.pause();
                            break;
                    }
                }
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(Playlist.PLAYLIST)) {
            mPlaylist = savedInstanceState.getParcelable(Playlist.PLAYLIST);
        }

        TrackInfo trackInfo = mPlaylist.getTrack();

        mArtist.setText(mPlaylist.getArtist().getName());
        mAlbum.setText(trackInfo.getAlbum());
        mTrack.setText(trackInfo.getName());

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int size = Math.round(86 * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        mAlbumImage.setImageResource(R.mipmap.ic_launcher);

        // TODO: load image in background
        if (!TextUtils.isEmpty(trackInfo.getImageUrl())) {
            Picasso.with(getActivity())
                    .load(trackInfo.getImageUrl())
                    .resize(size, size)
                    .into(mAlbumImage);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().bindService(mPlayIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate PlayerFragment");
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPlaylist = arguments.getParcelable(Playlist.PLAYLIST);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayIntent == null) {
            Activity activity = getActivity();
            mPlayIntent = new Intent(activity, MusicService.class);
            activity.startService(mPlayIntent);
            activity.bindService(mPlayIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity().getMediaController() != null) {
            getActivity().getMediaController().unregisterCallback(mCallback);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Playlist.PLAYLIST, mPlaylist);
    }

    private void startSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    /**
     * Update the Seekbar
     */
    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() != PlaybackState.STATE_PAUSED) {
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        mSeekBar.setProgress((int) currentPosition);
    }

    private void connectToSession(MediaSession.Token token) {
        MediaController controller = new MediaController(getActivity(), token);
        getActivity().setMediaController(controller);
        controller.registerCallback(mCallback);
        PlaybackState state = controller.getPlaybackState();
        updateViews(state);
        updateProgress();

        // TODO: move to private method

        mTrackLength.setText("30");
        mSeekBar.setMax(30000);
    }

    private void updateViews(PlaybackState state) {
        if (state == null) {
            return;
        }

        mLastPlaybackState = state;
        switch (state.getState()) {
            case PlaybackState.STATE_STOPPED:
                mPlayPause.setImageDrawable(mPlayDrawable);
                stopSeekbarUpdate();
                break;
            case PlaybackState.STATE_PLAYING:
            case PlaybackState.STATE_BUFFERING:
                mPlayPause.setImageDrawable(mPauseDrawable);
                startSeekbarUpdate();
                break;
            case PlaybackState.STATE_PAUSED:
                mPlayPause.setImageDrawable(mPlayDrawable);
                break;
        }
    }
}
