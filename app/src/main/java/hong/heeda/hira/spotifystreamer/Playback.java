package hong.heeda.hira.spotifystreamer;

import hong.heeda.hira.spotifystreamer.models.TrackInfo;

public interface Playback {
    /**
     * Start the playback.
     */
    void start();

    void stop(boolean notifyListeners);

    void pause();

    void skipToNext();

    void skipToPrev();

    void play(TrackInfo track);

    boolean isPlaying();

    interface Callback {
        /**
         * On current music completed.
         */
        void onCompletion();
        /**
         * on Playback status changed
         * Implementations can use this callback to update
         * playback state on the media sessions.
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error to be added to the PlaybackState
         */
        void onError(String error);

        /**
         * @param mediaId being currently played
         */
        void onMetadataChanged(String mediaId);
    }

    /**
     * @param callback to be called
     */
    void setCallback(Callback callback);
}
