package com.example.aro_pc.minasyangps.fragments;


import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.example.aro_pc.minasyangps.Consts;
import com.example.aro_pc.minasyangps.R;
import com.example.aro_pc.minasyangps.instance.UserHelper;
import com.example.aro_pc.minasyangps.interfaces.PlayerStatus;
import com.example.aro_pc.minasyangps.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import static com.example.aro_pc.minasyangps.Consts.USERS_STORAGE_NAME;
import static com.example.aro_pc.minasyangps.Consts.USER_MODEL_BACKGROUND;
import static com.example.aro_pc.minasyangps.Consts.USER_MODEL_VOICE_URL;
import static com.example.aro_pc.minasyangps.Consts.VOICE_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoRecorderFragment extends Fragment implements View.OnClickListener, ValueEventListener, PlayerStatus, MediaPlayer.OnCompletionListener {

    private FrameLayout mStartListen, mStopListen, mGetRecord;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private UserModel userModel;
    private StorageReference storageReference;
    private FloatingActionButton mPlayButton,mStopButton, mPauseButton;
    private SeekBar recordSeekBar;
    private CardView playRecordCardView;
    private LinearLayout transparentLayer;
    private Runnable playerRunnable ;
    private Handler handler;
    private String playerStatus;
    private RelativeLayout serverIsDisconnected;
    private LinearLayout layoutRecord;

    public UserInfoRecorderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info_recorder, container, false);
        serverIsDisconnected = (RelativeLayout) view.findViewById(R.id.server_disconnected_id);
        layoutRecord = (LinearLayout) view.findViewById(R.id.layout_record_id);
        mStartListen = (FrameLayout) view.findViewById(R.id.record_start_listen);
        mStopListen = (FrameLayout) view.findViewById(R.id.record_stop_listen);
        mGetRecord = (FrameLayout) view.findViewById(R.id.record_get);
        mPlayButton = (FloatingActionButton)view.findViewById(R.id.play_record_button) ;
        mPauseButton = (FloatingActionButton) view.findViewById(R.id.pause_record_button);
        mStopButton = (FloatingActionButton) view.findViewById(R.id.stop_record_button);
        recordSeekBar = (SeekBar) view.findViewById(R.id.record_seek_bar);
        playRecordCardView = (CardView) view.findViewById(R.id.play_record_cad_view);
        transparentLayer = (LinearLayout) view.findViewById(R.id.transparent_layout_record_card_view);
        transparentLayer.setVisibility(View.VISIBLE);
        mPlayButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        mStartListen.setOnClickListener(this);
        mStopListen.setOnClickListener(this);
        mGetRecord.setOnClickListener(this);

        userModel = UserHelper.getInstance().getmUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(Consts.DATABASE_NAME).child(userModel.getUid()).child(Consts.USER_MODEL_VOICE);
        reference.addValueEventListener(this);
        reference.getParent().child(USER_MODEL_BACKGROUND).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = (String)dataSnapshot.getValue();
                boolean isServiceOn = Boolean.parseBoolean(data);
                if (!isServiceOn){
                    // service disconnected
                    serverIsDisconnected.setVisibility(View.VISIBLE);
                    layoutRecord.setVisibility(View.GONE);
                } else {
                    serverIsDisconnected.setVisibility(View.GONE);
                    layoutRecord.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reference.getParent().child(USER_MODEL_VOICE_URL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = (String)dataSnapshot.getValue();
                switch (data){
                    case Consts.USER_MODEL_VOICE_URL:


                        break;
                    default:

                        mGetRecord.setAlpha(0.2f);
                        mGetRecord.setClickable(false);
                        StorageReference storageReference1 = storageReference.child(USERS_STORAGE_NAME).child(userModel.getUid()).child(VOICE_STORAGE);
                        try {
                            downloadFromDatabase(data,storageReference1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_start_listen:
                reference.setValue(Consts.START_LISTEN);
                transparentLayer.setVisibility(View.VISIBLE);
                if (mPlayer != null && mPlayer.isPlaying()){
                    stopPlaying();
                }

                break;
            case R.id.record_stop_listen:
                reference.setValue(Consts.STOP_LISTEN);

                break;
            case R.id.record_get:
                mGetRecord.setAlpha(0.2f);
                mGetRecord.setClickable(false);
                reference.setValue(Consts.GET_RECORD);

                break;
            case R.id.play_record_button:

                this.onMediaPlay();




                break;
            case R.id.pause_record_button:
                if (mPlayer!= null && mPlayer.isPlaying()){
                    this.onMediaPause(mPlayer.getCurrentPosition());




                }
                break;
            case R.id.stop_record_button:
                if (mPlayer!= null){
                    this.onMediaStop();

                }
                break;

        }
    }

    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private int currentMilisec = 0;


    private void startPlaying() {

        if (mPlayer!= null){
            if (!mPlayer.isPlaying()) {
                mPlayer.seekTo(currentMilisec);
                mPlayer.start();
                handler.post(playerRunnable);
            }

        } else {
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(this);

            try {
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            mGetRecord.setAlpha(1f);
            mGetRecord.setClickable(true);
            animateSeekBar();
        }
    }

    private void animateSeekBar() {

        playerRunnable = new Runnable() {
            @Override
            public void run() {

                if (mPlayer != null) {
                    recordSeekBar.setProgress(mPlayer.getCurrentPosition());
                }
                handler.postDelayed(this,100);
            }
        };
        handler = new Handler();
        handler.post(playerRunnable);


    }


    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String data = (String)dataSnapshot.getValue();
        switch (data){
            case Consts.START_LISTEN:
                break;
            case Consts.STOP_LISTEN:
                break;
            case Consts.GET_RECORD:
                break;
            default:
//                mFileName = getActivity().getFilesDir().getAbsolutePath();
//                mFileName += "/audiorecordtest.3gp";
//                StorageReference storageReference1 = storageReference.child(USERS_STORAGE_NAME).child(userModel.getUid()).child(VOICE_STORAGE);
//
//                try {
//                    downloadFromDatabase(data,storageReference1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                startPlaying(localFile);

                break;
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void downloadFromDatabase(String url, StorageReference storageReference1) throws IOException {
        mFileName = getActivity().getApplicationContext().getFilesDir().getAbsolutePath();
        mFileName +=  "/" +userModel.getUid() + "voice.mp3";
        File file = new File(mFileName);
        file.createNewFile();
        storageReference1.getFile(file)
                .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        transparentLayer.setVisibility(View.GONE);
                        mGetRecord.setAlpha(1);
                        mGetRecord.setClickable(true);
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
    public void onMediaPlay() {
        startPlaying();
        recordSeekBar.setMax(mPlayer.getDuration());
        recordSeekBar.setProgress(mPlayer.getCurrentPosition());
    }

    @Override
    public void onMediaStop() {
        mPlayer.stop();
        currentMilisec = 0;
        handler.removeCallbacks(playerRunnable);
        recordSeekBar.setProgress(0);
        mPlayer = null;

    }

    @Override
    public void onMediaPause(int milisec) {
        mPlayer.pause();
        handler.removeCallbacks(playerRunnable);
        recordSeekBar.setProgress(milisec);
        currentMilisec = milisec;

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayer = null;
        recordSeekBar.setProgress(0);
        handler.removeCallbacks(playerRunnable);
    }
}
