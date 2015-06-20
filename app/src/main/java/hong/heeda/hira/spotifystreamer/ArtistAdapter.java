package hong.heeda.hira.spotifystreamer;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtistAdapter extends ArrayAdapter<ArtistInfo> {

    private static final String LOG_TAG = ArtistAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private Context mContext;

    public ArtistAdapter(Context context,
                         int resource,
                         ArrayList<ArtistInfo> objects) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
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

        ArtistInfo artist = getItem(position);
        artistTextView.setText(artist.name);

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int size = Math.round(86 * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        artistImage.setImageResource(R.mipmap.ic_launcher);

        if (!TextUtils.isEmpty(artist.imageUrl)) {
            Picasso.with(mContext)
                    .load(artist.imageUrl)
                    .resize(size, size)
                    .into(artistImage);
        }

        return view;
    }
}