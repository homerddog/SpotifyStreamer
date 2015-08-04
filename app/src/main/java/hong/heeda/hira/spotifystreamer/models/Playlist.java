package hong.heeda.hira.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * A wrapper class for an Artist's Top Ten tracks.
 */
public class Playlist implements Parcelable {
    private ArtistInfo mArtist;
    private ArrayList<TrackInfo> mTracks;
    private int mCurrentTrackPosition;
    private int mCurrentPositionInTrack;

    public static final String PLAYLIST = "Playlist";

    public Playlist(ArtistInfo artist, ArrayList<TrackInfo> tracks, int currentTrackPosition) {
        mArtist = artist;
        mTracks = tracks;
        mCurrentTrackPosition = currentTrackPosition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,
                              int flags) {
        dest.writeParcelable(mArtist, 0);
        dest.writeTypedList(mTracks);
        dest.writeInt(mCurrentTrackPosition);
        dest.writeInt(mCurrentPositionInTrack);
    }

    public ArrayList<TrackInfo> getTracks() {
        return mTracks;
    }

    public ArtistInfo getArtist() {
        return mArtist;
    }

    public TrackInfo getTrack() {
        return mTracks.get(mCurrentTrackPosition);
    }

    protected Playlist(Parcel in) {
        mArtist = in.readParcelable(ArtistInfo.class.getClassLoader());
        mTracks = in.createTypedArrayList(TrackInfo.CREATOR);
        mCurrentTrackPosition = in.readInt();
        mCurrentPositionInTrack = in.readInt();
    }

    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public int getCurrentTrackPosition() {
        return mCurrentTrackPosition;
    }

    public void setCurrentTrackPosition(int position) {
        mCurrentTrackPosition = position;
    }

    public int getCurrentPositionInTrack() {
        return mCurrentPositionInTrack;
    }

    public void setCurrentPositionInTrack(int position) {
        mCurrentTrackPosition = position;
    }
}
