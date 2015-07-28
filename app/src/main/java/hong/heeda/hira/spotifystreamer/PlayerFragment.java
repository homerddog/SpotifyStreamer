package hong.heeda.hira.spotifystreamer;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerFragment} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends DialogFragment {

    public static final String FRAGMENT_TAG = "PFTAG";

    private OnFragmentInteractionListener mListener;
    private TrackInfo mTrackInfo;

    private TextView mArtist;
    private TextView mAlbum;
    private TextView mTrack;
    private ImageView mAlbumImage;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTrackInfo = arguments.getParcelable(TrackInfo.TRACK_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media_player, container, false);
        mArtist = (TextView) rootView.findViewById(R.id.artist_text_view);
        mAlbum = (TextView) rootView.findViewById(R.id.album_text_view);
        mTrack = (TextView) rootView.findViewById(R.id.track_text_view);
        mAlbumImage = (ImageView) rootView.findViewById(R.id.album_image_view);

        mArtist.setText(mTrackInfo.getArtist());
        mAlbum.setText(mTrackInfo.getAlbum());
        mTrack.setText(mTrackInfo.getName());

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int size = Math.round(86 * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        mAlbumImage.setImageResource(R.mipmap.ic_launcher);

        if (!TextUtils.isEmpty(mTrackInfo.getImageUrl())) {
            Picasso.with(getActivity())
                    .load(mTrackInfo.getImageUrl())
                    .resize(size, size)
                    .into(mAlbumImage);
        }
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().finish();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
