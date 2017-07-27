package com.example.aro_pc.minasyangps.instance;

import com.example.aro_pc.minasyangps.model.UserModel;

/**
 * Created by Aro-PC on 7/14/2017.
 */

public class UserHelper {

    public UserModel getmUser() {
        if (mUser == null){
            mUser = new UserModel();
        }
        return mUser;
    }

    public void setmUser(UserModel mUser) {
        this.mUser = mUser;
    }

    private UserModel mUser;

    private static UserHelper instance;

    public static synchronized UserHelper getInstance(){
        if (instance == null){
            instance = new UserHelper();
        }
        return instance;
    }

}
