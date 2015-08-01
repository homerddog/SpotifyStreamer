package hong.heeda.hira.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

public class TrackInfo implements Parcelable {
    public static final Parcelable.Creator<TrackInfo> CREATOR = new Parcelable.Creator<TrackInfo>() {
        public TrackInfo createFromParcel(Parcel source) {
            return new TrackInfo(source);
        }

        public TrackInfo[] newArray(int size) {
            return new TrackInfo[size];
        }
    };

    private String mName;
    private String mArtist;
    private String mAlbum;
    private String mImageUrl;
    private String mPreviewUrl;
    private String mUri;
    private String mId;

    public final static String TRACK_INFO = "TrackInfo";

    public TrackInfo(Track track) {

        if (track == null) {
            throw new IllegalArgumentException("track cannot be null");
        }

        setName(track.name);
        setArtist(track.artists.get(0).name);
        setAlbum(track.album.name);
        setImageUrl(track.album.images.size() > 0 ? track.album.images.get(0).url : "");
        setPreviewUrl(track.preview_url);
        setUri(track.uri);
        setId(track.id);
    }

    protected TrackInfo(Parcel in) {
        mName = in.readString();
        mArtist = in.readString();
        mAlbum = in.readString();
        mImageUrl = in.readString();
        mPreviewUrl = in.readString();
        mUri = in.readString();
        mId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,
                              int flags) {
        dest.writeString(mName);
        dest.writeString(mArtist);
        dest.writeString(mAlbum);
        dest.writeString(mImageUrl);
        dest.writeString(mPreviewUrl);
        dest.writeString(mUri);
        dest.writeString(mId);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        mPreviewUrl = previewUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }
}
