package com.example.aro_pc.minasyangps.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aro_pc.minasyangps.Consts;
import com.example.aro_pc.minasyangps.R;
import com.example.aro_pc.minasyangps.instance.UserHelper;
import com.example.aro_pc.minasyangps.model.UserModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static com.example.aro_pc.minasyangps.Consts.USERS_STORAGE_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private FirebaseDatabase database;
    private TextView name;
    private TextView userName;
    private TextView password;
    private FrameLayout voiceRecorder, userLocation;
    private UserModel userModel;
    private FragmentManager fragmentManager;
    private ImageView imageView;
    private StorageReference storageReference;
    private String mFileName;
    private File imageFile;
    private CardView userInfoCardView;
    private LinearLayout userInfoLayout;
    private FrameLayout containr;
    private boolean letAnimateContainer = false;
    private double displayHeight;
    private double displayWidth;


    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    public UserInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        name = (TextView) view.findViewById(R.id.user_info_name);
        userName = (TextView) view.findViewById(R.id.user_info_user_name);
        password = (TextView) view.findViewById(R.id.user_info_password);
        voiceRecorder = (FrameLayout) view.findViewById(R.id.voice_recorder_button);
        userLocation = (FrameLayout) view.findViewById(R.id.get_location_button);
        imageView = (ImageView) view.findViewById(R.id.user_info_image_view);
        userInfoCardView = (CardView) view.findViewById(R.id.user_info_card_view);
        userInfoLayout = (LinearLayout) view.findViewById(R.id.user_info_layout);
        containr = (FrameLayout) view.findViewById(R.id.user_info_container);
//        userInfoCardView.setOnTouchListener(this);
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userModel = UserHelper.getInstance().getmUser();
        if (userModel.getImageUrl().toString().equals(Consts.USER_MODEL_IMAGEURL_VALUE)){
            imageView.setImageResource(R.drawable.ic_account_box_black_24dp);
        } else {
            StorageReference storageReference1 = storageReference.child(USERS_STORAGE_NAME).child(userModel.getUid()).child(Consts.ACCOUNT_IMAGE_STORAGE);
            try {
                downloadFromDatabase(storageReference1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        displayWidth = display.getWidth();
        displayHeight = display.getHeight();
        return view;
    }

    private void downloadFromDatabase( StorageReference storageReference1) throws IOException {
        mFileName = getActivity().getCacheDir().getAbsolutePath();
        mFileName +=  "/" + userModel.getUid() + "image.jpg";
        imageFile = new File(mFileName);
        imageFile.createNewFile();
        storageReference1.getFile(imageFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                        if(imageFile.exists()){

                            Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                            imageView.setImageBitmap(myBitmap);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                Log.d("sd","sdsdsd");
            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name.setText(UserHelper.getInstance().getmUser().getName());
        userName.setText(UserHelper.getInstance().getmUser().getUserName());
        password.setText(UserHelper.getInstance().getmUser().getPassword());
        voiceRecorder.setOnClickListener(this);
        userLocation.setOnClickListener(this);

    }

    private LatLng postition;
    private boolean isAnimatedUp = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.voice_recorder_button:
                if (!isAnimatedUp){
                    animateUpCardView();
//                    animateUpRecorderFragment();

                    isAnimatedUp = true;
                }

                UserInfoRecorderFragment userInfoMapFragment = new UserInfoRecorderFragment();
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.user_info_container,userInfoMapFragment).commit();
                letAnimateContainer = true;
                break;
            case R.id.get_location_button:
                if (!isAnimatedUp){
                    animateUpCardView();
//                    animateUpRecorderFragment();

                    isAnimatedUp = true;
                }

                database.getReference(Consts.DATABASE_NAME).child(userModel.getUid()).child(Consts.USER_MODEL_LOCATION).setValue(Consts.GET_LOCATION_VALUE);
                database.getReference(Consts.DATABASE_NAME).child(userModel.getUid()).child(Consts.USER_MODEL_LOCATION).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String locationJson = (String) dataSnapshot.getValue();
                        try {
                            JSONObject jsonObject = new JSONObject(locationJson);
                            double lat = Double.parseDouble(String.valueOf(jsonObject.get(Consts.LATITUDE_JSON)));
                            double lng = Double.parseDouble(String.valueOf(jsonObject.get(Consts.LONGITUDE_JSON)));
                            postition = new LatLng(lat,lng);
                            UserInfoMapFragment userInfoMapFragment = new UserInfoMapFragment();
                            userInfoMapFragment.setPosition(postition);
                            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.user_info_container,userInfoMapFragment).commit();
                            letAnimateContainer = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.user_info_card_view:
                animateCardView(event);
                break;
        }
        return false;
    }

    private boolean isClicked = true;
    private void animateCardView(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isClicked = true;
                break;
            case MotionEvent.ACTION_MOVE:
                isClicked = false;
                break;
            case MotionEvent.ACTION_UP:
                if (isClicked){
                    animateUpCardView();
                }
                break;
        }
    }

    private void animateUpCardView() {
//        userInfoCardView.animate().yBy(-userInfoLayout.getHeight()).setDuration(150).start();
        userInfoLayout.setVisibility(View.GONE);
//        containr.animate().yBy(-userInfoLayout.getHeight()).setDuration(150).start();

    }

    private void animateUpRecorderFragment(){
            ValueAnimator animator = ValueAnimator.ofInt(containr.getHeight(), (int)(displayHeight -userInfoCardView.getY()) +  + userInfoCardView.getHeight() );
            animator.setInterpolator(new FastOutLinearInInterpolator());
            animator.setDuration(150);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    containr.getLayoutParams().height = (int) animation.getAnimatedValue();
                    containr.requestLayout();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    containr.setY(userInfoCardView.getY() + userInfoCardView.getHeight());
                }
            });
            animator.start();
    }
}
