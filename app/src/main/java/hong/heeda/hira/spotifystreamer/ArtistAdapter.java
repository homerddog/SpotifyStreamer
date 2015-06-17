package hong.heeda.hira.spotifystreamer;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistAdapter implements ListAdapter {

    private List<Artist> artists;
    private LayoutInflater mInflater;

    public ArtistAdapter(Context context) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        if (this.artists != null) {
            return this.artists.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (this.artists != null) {
            return this.artists.get(position);
        }

        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = mInflater.inflate(R.layout.artist_list_item, parent);
        }

        TextView artistTextView = (TextView) view.findViewById(R.id.artist_text_view);
        
        Artist artist = (Artist)getItem(position);
        artistTextView.setText(artist.name);

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}