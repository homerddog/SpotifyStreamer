package hong.heeda.hira.spotifystreamer;

public interface Playback {
    /**
     * Start the playback.
     */
    void start();

    void stop(boolean notifyListeners);

    void pause();

    boolean isPlaying();
}
