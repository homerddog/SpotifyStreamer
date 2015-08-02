package hong.heeda.hira.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import hong.heeda.hira.spotifystreamer.models.Playlist;
import hong.heeda.hira.spotifystreamer.models.TrackInfo;
import hong.heeda.hira.spotifystreamer.service.MusicService;

public class PlayerFragment extends DialogFragment {

    public static final String FRAGMENT_TAG = "PFTAG";

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

    private MusicService mMusicService;
    private boolean mIsMusicBound;
    private Intent mPlayIntent;

    private MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            super.onPlaybackStateChanged(state);
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

            mMusicService.setPlaylist(mPlaylist);
            mIsMusicBound = true;
            mMusicService.playTrack();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsMusicBound = false;
        }
    };

    public PlayerFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPlaylist = arguments.getParcelable(Playlist.PLAYLIST);
        }
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

        mSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicService.skipToNext();
            }
        });

        TrackInfo trackInfo = mPlaylist.getTrack();

        mArtist.setText(mPlaylist.getArtist().getName());
        mAlbum.setText(trackInfo.getAlbum());
        mTrack.setText(trackInfo.getName());

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int size = Math.round(86 * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        mAlbumImage.setImageResource(R.mipmap.ic_launcher);

        if (!TextUtils.isEmpty(trackInfo.getImageUrl())) {
            Picasso.with(getActivity())
                    .load(trackInfo.getImageUrl())
                    .resize(size, size)
                    .into(mAlbumImage);
        }
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
//        if (!MainActivity.isLargeLayout()) {
//            getActivity().finish();
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayIntent == null) {
            MediaController controller = getActivity().getMediaController();
            if (controller != null) {
                controller.registerCallback(mCallback);
            }
            mPlayIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(mPlayIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(mPlayIntent);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(mPlayIntent);
        mMusicService = null;
        getActivity().unbindService(serviceConnection);
        super.onDestroy();
    }


}
