package hong.heeda.hira.spotifystreamer.events;

public class ProgressChangedEvent {
    private int mPosition;

    public ProgressChangedEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    @Override
    public String toString() {
        if (mPosition > 0) {
            return String.valueOf(mPosition / 1000);
        }
        return "";
    }
}
