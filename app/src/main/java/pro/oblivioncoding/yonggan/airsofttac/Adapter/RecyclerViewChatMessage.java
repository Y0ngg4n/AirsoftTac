package pro.oblivioncoding.yonggan.airsofttac.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Chat.ChatMessage;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class RecyclerViewChatMessage extends RecyclerView.Adapter<RecyclerViewChatMessage.ViewHolder> {

    private ArrayList<ChatMessage> chatDataArrayList;

    public RecyclerViewChatMessage(ArrayList<ChatMessage> chatData) {
        chatDataArrayList = chatData;
    }

    @NonNull
    @Override
    public RecyclerViewChatMessage.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_chat_message, viewGroup, false);
        RecyclerViewChatMessage.ViewHolder holder = new RecyclerViewChatMessage.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewChatMessage.ViewHolder viewHolder, int i) {
        viewHolder.nickName.setText(chatDataArrayList.get(i).getNickName());
        viewHolder.text.setText(chatDataArrayList.get(i).getText());
    }

    @Override
    public int getItemCount() {
        return chatDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nickName, text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nickName = itemView.findViewById(R.id.chatMessageNickName);
            text = itemView.findViewById(R.id.chatMessageText);
        }
    }
}
