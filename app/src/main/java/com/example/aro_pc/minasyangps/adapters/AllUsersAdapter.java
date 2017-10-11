package com.example.aro_pc.minasyangps.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.aro_pc.minasyangps.R;
import com.example.aro_pc.minasyangps.fragments.UserInfoFragment;
import com.example.aro_pc.minasyangps.instance.UserHelper;
import com.example.aro_pc.minasyangps.model.UserModel;

import java.util.ArrayList;

/**
 * Created by Aro-PC on 7/24/2017.
 */

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.UsersListViewHolder> {

    private Context context;
    private ArrayList<UserModel> userModels;
    private FragmentManager fragmentManager;

    public AllUsersAdapter(Context context, ArrayList<UserModel> userModels, FragmentManager fragmentManager) {
        this.context = context;
        this.userModels = userModels;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public AllUsersAdapter.UsersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_model, null);
        AllUsersAdapter.UsersListViewHolder viewHolder = new AllUsersAdapter.UsersListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AllUsersAdapter.UsersListViewHolder holder, final int position) {
        holder.textView.setText(userModels.get(position).getName());
        holder.isServiceOn.setChecked(Boolean.parseBoolean(userModels.get(position).getService()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHelper.getInstance().setmUser(userModels.get(position));
                UserInfoFragment userInfoFragment = new UserInfoFragment();
                userInfoFragment.setFragmentManager(fragmentManager);
                fragmentManager.beginTransaction().replace(R.id.fragment_map_container,userInfoFragment).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class UsersListViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView textView;
        private CheckBox isServiceOn;

        public UsersListViewHolder(View itemView) {
            super(itemView);

            isServiceOn = (CheckBox) itemView.findViewById(R.id.is_service_on_chech_box);
            cardView = (CardView) itemView.findViewById(R.id.user_list_cardview);
            textView = (TextView) itemView.findViewById(R.id.user_name_in_list_tv);
        }
    }
}
