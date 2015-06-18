package hong.heeda.hira.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistAdapter extends ArrayAdapter<Artist> {

    private Context context;
    private List<Artist> artists;
    private LayoutInflater mInflater;

    public ArtistAdapter(Context context,
                         int resource,
                         List<Artist> artists) {
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
        
        Artist artist = getItem(position);
        artistTextView.setText(artist.name);

        return view;
    }
}