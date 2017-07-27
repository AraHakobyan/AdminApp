package com.example.aro_pc.minasyangps.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aro_pc.minasyangps.R;
import com.example.aro_pc.minasyangps.model.ChatModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aro-PC on 7/14/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ChatModel> chatModels;


    public ChatAdapter(Context context, ArrayList<ChatModel> chatModels) {
        this.context = context;
        this.chatModels = chatModels;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {

        holder.userName.setText(chatModels.get(position).getUserName());
        holder.chatMessage.setText(chatModels.get(position).getMessage());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.itemLayout.getLayoutParams();
        if (holder.userName.getText().toString().equals("aro")){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.itemLayout.setLayoutParams(layoutParams);
            holder.imageView.setImageResource(R.drawable.adana);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_map_black_24dp);

        }

    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView chatMessage;
        private LinearLayout itemLayout;
        private CircleImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);

            itemLayout = (LinearLayout) itemView.findViewById(R.id.item_layout_id);
            userName = (TextView) itemView.findViewById(R.id.user_name_chat);
            chatMessage = (TextView) itemView.findViewById(R.id.chat_id_item_message);
            imageView = (CircleImageView) itemView.findViewById(R.id.cat_item_image);
        }
    }
}
