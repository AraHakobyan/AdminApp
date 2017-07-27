package com.example.aro_pc.minasyangps.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aro_pc.minasyangps.Consts;
import com.example.aro_pc.minasyangps.R;
import com.example.aro_pc.minasyangps.adapters.AllUsersAdapter;
import com.example.aro_pc.minasyangps.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseUserFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllUsersAdapter adapter;
    private ArrayList<UserModel> allUsers;
    private FirebaseDatabase database;

    private Context context;

    public ChooseUserFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        UserModel userModel = new UserModel();
//        userModel.setName("xoren");
//        UserHelper.getInstance().setmUser(userModel);




    }

    private void getAllUsers() {
        database.getReference(Consts.DATABASE_NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserModel post = postSnapshot.getValue(UserModel.class);
                    allUsers.add(post);
                }
                recyclerView.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        allUsers = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        View view = inflater.inflate(R.layout.fragment_choose_user, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.all_users_recycler_view);
        adapter = new AllUsersAdapter(getActivity(),allUsers,getFragmentManager());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(context));


                getAllUsers();


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
