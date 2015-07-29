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

import java.util.List;

import hong.heeda.hira.spotifystreamer.models.TrackInfo;

public class TrackAdapter extends ArrayAdapter<TrackInfo> {

    private static final String LOG_TAG = TrackAdapter.class.getSimpleName();

    private Context context;
    private LayoutInflater mInflater;

    public TrackAdapter(Context context,
                        int resource,
                        List<TrackInfo> artists) {
        super(context, resource, artists);
        this.context = context;
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

        TrackInfo track = getItem(position);

        TextView trackTextView = (TextView) view.findViewById(R.id.track_text_view);
        TextView albumTextView = (TextView) view.findViewById(R.id.album_text_view);
        ImageView trackImage = (ImageView) view.findViewById(R.id.track_image);

        trackTextView.setText(track.getName());
        albumTextView.setText(track.getAlbum());

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int size = Math.round(86 * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        trackImage.setImageResource(R.mipmap.ic_launcher);

        if (!TextUtils.isEmpty(track.getImageUrl())) {
            Picasso.with(context)
                    .load(track.getImageUrl())
                    .resize(size, size)
                    .into(trackImage);
        }

        return view;
    }
}
