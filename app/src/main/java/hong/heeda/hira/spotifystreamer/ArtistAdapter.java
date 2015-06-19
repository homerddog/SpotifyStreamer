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

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

public class ArtistAdapter extends ArrayAdapter<Artist> {

    private static final String LOG_TAG = ArtistAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private ArrayList<Artist> artists;

    public ArtistAdapter(Context context,
                         int resource,
                         ArrayList<Artist> artists) {
        super(context, resource, artists);
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
        ImageView artistImage = (ImageView) view.findViewById(R.id.artist_image);

        Artist artist = getItem(position);
        artistTextView.setText(artist.name);

        try {

            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            int size = Math.round(86 * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));

            Image image = artist.images.get(0);
            Picasso.with(getContext())
                    .load(image.url)
                    .resize(size, size)
                    .into(artistImage);

        } catch (IndexOutOfBoundsException e) {
            Log.i(LOG_TAG, "Unable to load image for " + artist.name);
        }
        return view;
    }

    public ArrayList<Artist> getArtists() {
        return this.artists;
    }
}