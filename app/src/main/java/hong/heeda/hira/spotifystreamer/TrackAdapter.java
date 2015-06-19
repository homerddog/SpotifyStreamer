package hong.heeda.hira.spotifystreamer;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class TrackAdapter extends ArrayAdapter<Track>{

    private static final String LOG_TAG = TrackAdapter.class.getSimpleName();

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
            view = mInflater.inflate(R.layout.track_list_item, parent, false);
        }

        Track track = getItem(position);

        TextView trackTextView = (TextView) view.findViewById(R.id.track_text_view);
        TextView albumTextView = (TextView) view.findViewById(R.id.album_text_view);
        ImageView trackImage = (ImageView) view.findViewById(R.id.track_image);

        trackTextView.setText(track.name);
        albumTextView.setText(track.album.name);

        try {

            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            int size = Math.round(86 * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));

            Image image = track.album.images.get(0);
            Picasso.with(context)
                    .load(image.url)
                    .resize(size, size)
                    .into(trackImage);

        } catch (IndexOutOfBoundsException e) {
            Log.i(LOG_TAG, "Unable to load image for " + track.name);
        }
        return view;
    }
}
