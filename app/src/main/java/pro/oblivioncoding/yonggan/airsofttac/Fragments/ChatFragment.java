package pro.oblivioncoding.yonggan.airsofttac.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import pro.oblivioncoding.yonggan.airsofttac.Adapter.RecyclerViewChatMessage;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Chat.ChatMessage;
import pro.oblivioncoding.yonggan.airsofttac.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static String chatChannelID;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private RecyclerView recyclerView;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String pChatChannelID) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        chatChannelID = pChatChannelID;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = rootView.findViewById(R.id.chatMessageList);
        //TODO: Add creation of Chat channel
        setRecyclerView(FirebaseDB.getGameData().getChatMessages());
        final View sendMessage = rootView.findViewById(R.id.chatSendMessage);
        FirebaseDB.getGames().whereEqualTo("gameID", FirebaseDB.getGameData().getGameID())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    final DocumentReference documentReference = FirebaseDB.getGames().document(task.getResult().getDocuments().get(0).getId());
                    sendMessage.setOnClickListener(v -> {
                        final TextView writeMessage = rootView.findViewById(R.id.chatWriteMessage);
                        if (!writeMessage.getText().toString().isEmpty()) {
                            FirebaseDB.getGameData().getChatMessages().add(new ChatMessage(FirebaseDB.getGameData().getOwnUserData(
                                    FirebaseAuthentication.getFirebaseUser().getEmail()).getNickname(),
                                    FirebaseAuthentication.getFirebaseUser().getEmail(),
                                    writeMessage.getText().toString()));
                            FirebaseDB.updateObject(documentReference, "chatMessages",
                                    FirebaseDB.getGameData().getChatMessages());
                            setRecyclerView(FirebaseDB.getGameData().getChatMessages());
                        }
                        sendMessage.setVisibility(View.INVISIBLE);
                        writeMessage.setText("");
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(() -> {
                                    sendMessage.setVisibility(View.VISIBLE);
                                });
                            }
                        }, 5000L);
                    });
                }
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
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

    private void setRecyclerView(ArrayList<ChatMessage> chatMessages) {
        RecyclerViewChatMessage recyclerViewChatMessage = new RecyclerViewChatMessage(FirebaseDB.getGameData().getChatMessages());
        recyclerView.setAdapter(recyclerViewChatMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.scrollToPosition(chatMessages.size() - 1);
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
