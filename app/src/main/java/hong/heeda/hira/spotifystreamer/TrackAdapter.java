package hong.heeda.hira.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class TrackAdapter extends ArrayAdapter<Track>{

    private Context context;
    private List<Track> artists;
    private LayoutInflater mInflater;

    public TrackAdapter(Context context,
                         int resource,
                         List<Track> artists) {
        super(context, resource, artists);

        this.context = context;
        this.artists = artists;

        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = mInflater.inflate(R.layout.artist_list_item, parent, false);
        }

        TextView artistTextView = (TextView) view.findViewById(R.id.artist_text_view);

        Track track = getItem(position);
        artistTextView.setText(track.name);

        return view;
    }
}
