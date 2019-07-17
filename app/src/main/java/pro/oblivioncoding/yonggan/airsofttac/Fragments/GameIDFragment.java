package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameIDFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameIDFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameIDFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public GameIDFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameIDFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameIDFragment newInstance(final String param1, final String param2) {
        final GameIDFragment fragment = new GameIDFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_game_id, container, false);
        getActivity().runOnUiThread(() -> {
            final String gameID = FirebaseDB.getGameData().getGameID();
            ((TextView) rootView.findViewById(R.id.gameIDTitle)).setText(gameID);
            final ImageView gameIDQRCode = rootView.findViewById(R.id.gameIDQRCode);
            final QRCodeWriter qrCodeWriter = new QRCodeWriter();
            final Point point = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(point);
            try {
                final BitMatrix bitMatrix = qrCodeWriter.encode(gameID, BarcodeFormat.QR_CODE, point.x - 50, point.x - 50);
                final Bitmap bitmap = Bitmap.createBitmap(bitMatrix.getWidth(), bitMatrix.getHeight(), Bitmap.Config.RGB_565);
                for (int x = 0; x < bitMatrix.getWidth(); x++) {
                    for (int y = 0; y < bitMatrix.getHeight(); y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                gameIDQRCode.setImageBitmap(bitmap);
            } catch (final WriterException e) {
                Toast.makeText(getContext(), R.string.code_gameid_fragment_could_not_generate_qr_code, Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(final Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
