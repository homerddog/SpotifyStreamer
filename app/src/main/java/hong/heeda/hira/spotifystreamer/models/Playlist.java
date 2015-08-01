package hong.heeda.hira.spotifystreamer.models;

import java.util.ArrayList;

/**
 * A wrapper class for an Artist's Top Ten tracks.
 */
public class Playlist {
    private ArtistInfo mArtist;
    private ArrayList<TrackInfo> mTracks;
    private int mCurrentTrackPosition;

    public Playlist(ArtistInfo artist, ArrayList<TrackInfo> tracks, int currentTrackPosition) {
        mArtist = artist;
        mTracks = tracks;
        mCurrentTrackPosition = currentTrackPosition;
    }
}
