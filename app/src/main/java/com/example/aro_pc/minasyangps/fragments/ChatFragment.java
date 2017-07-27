package com.example.aro_pc.minasyangps.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.aro_pc.minasyangps.R;
import com.example.aro_pc.minasyangps.adapters.ChatAdapter;
import com.example.aro_pc.minasyangps.instance.UserHelper;
import com.example.aro_pc.minasyangps.model.ChatModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements View.OnClickListener {

    private EditText editText;
    private Button send;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private ArrayList<ChatModel> chatModels;
    private ChatModel chatModel;
    private String userName;

    public ChatFragment() {
        chatModels = new ArrayList<>();
         chatModel = new ChatModel();
         userName = UserHelper.getInstance().getmUser().getName();
         setHasOptionsMenu(true);
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initUi(view);
        addListeners();
        getMessage();
        setAdapter();

        return view;
    }

    private void setAdapter() {
        adapter = new ChatAdapter(getActivity(),chatModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    private void addListeners() {
        send.setOnClickListener(this);

    }

    private void initUi(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.chat_recycler_view);
        editText = (EditText)view.findViewById(R.id.chat_edit_text);
        send = (Button) view.findViewById(R.id.send_message);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_message:
                sendMessage(editText.getText().toString());

                editText.setText("");
                break;
        }
    }

    private void sendMessage(String mMessage) {


        DatabaseReference reference = database.getReference("aro");
        reference.setValue(mMessage);
        chatModel.setMessage(mMessage);
        chatModel.setUserName("aro");
        chatModels.add(chatModel);
        recyclerView.scrollToPosition(chatModels.size()-1);
//        reference.push();

    }

    private FirebaseDatabase database;
    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    private void getMessage() {
        database.getReference("xoren").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                message = dataSnapshot.getValue(String.class);
//                textView.setText(textView.getText() + "\n" + message);

                chatModel.setMessage(dataSnapshot.getValue(String.class));
                chatModel.setUserName(UserHelper.getInstance().getmUser().getName());
                chatModels.add(chatModel);
                adapter.notifyItemInserted(chatModels.size() - 1);
                recyclerView.scrollToPosition(chatModels.size()-1);

//                textView.setText(textView.getText() + "\n" + dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
