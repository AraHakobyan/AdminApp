package com.example.aro_pc.minasyangps.fragments;


import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.animationhelperlibrary.ColorAnimation;
import com.example.animationhelperlibrary.DrawPolylineAnimation;
import com.example.animationhelperlibrary.roadhelper.RoadHelper;
import com.example.animationhelperlibrary.roadhelper.RoadInfo;
import com.example.animationhelperlibrary.roadhelper.RoadIsReadyListener;
import com.example.aro_pc.minasyangps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowOnMapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private Button clearData, showAll;

    private MapView mapView;
    private GoogleMap googleMap;
    private AnimatorSet animatorSet;

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    List<LatLng> latLngs;

    public ShowOnMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_on_map, container, false);
        initUi(view);
        addListeners();
        initMap();

        return view;
    }

    private void initMap() {
        mapView.onCreate(null);
        mapView.onResume();

        mapView.getMapAsync(this);
    }

    private void addListeners() {
        showAll.setOnClickListener(this);
        clearData.setOnClickListener(this);
    }

    private void initUi(View view) {
        mapView = (MapView) view.findViewById(R.id.map_view);

        clearData = (Button) view.findViewById(R.id.clear_history);
        showAll = (Button) view.findViewById(R.id.show_all_id);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.clear_history:
                clearData();
                break;
            case R.id.show_all_id:
                if (latLngs.size() > 3)
                    for (int i = 1; i < latLngs.size() - 1; i++) {
                        RoadHelper.getInstance().addPoint(latLngs.get(i));

                    }
                break;
        }
    }

    private void clearData() {
        latLngs.clear();
        googleMap.clear();
        DatabaseReference reference = database.getReference("location");
        reference.setValue("");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        lineForLib = googleMap.addPolyline(new PolylineOptions()
                .width(4)
                .color(Color.BLACK));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(CHARENTSAVAN_KINO, 10, 10, 10)));
        getLocation();
        getLastLatLng();
    }

    FirebaseDatabase database;

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public Polyline lineForLib;

    private LatLng lastLatLng = CHARENTSAVAN_KINO;
    public static final LatLng CHARENTSAVAN_KINO = new LatLng(40.4018115,44.6433958);

    public void setLastLatLng(LatLng lastLatLng) {
        this.lastLatLng = lastLatLng;
    }

    private LatLng getLastLatLng() {
        database.getReference("lastLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    setLastLatLng(getItemsFromData(dataSnapshot.getValue(String.class)));
                    googleMap.addMarker(new MarkerOptions().position(lastLatLng));
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(lastLatLng, 10, 10, 10)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return lastLatLng;
    }

    private void getLocation() {
        googleMap.clear();
        lineForLib = googleMap.addPolyline(new PolylineOptions()
                .width(2)
                .color(Color.BLACK));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(lastLatLng, 10, 10, 10)));
        database.getReference("location").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                message = dataSnapshot.getValue(String.class);
//                textView.setText(textView.getText() + "\n" + message);
//                textView.setText("");
                try {
                    latLngs = new ArrayList<>();
//                    getItemsFromData(dataSnapshot.getValue(String.class));
                    getItemsFromData((Map<String, Object>) dataSnapshot.getValue());

                    if (latLngs.size() > 2)
                        RoadHelper.getInstance().setPoints(latLngs.get(0), lastLatLng).addRoadIsReadyListener(new RoadIsReadyListener() {
                            @Override
                            public void getRoad(RoadInfo roadInfo) {
                                lineForLib.setPoints(roadInfo.getRoadPoints());
                                ColorAnimation colorAnimation = new ColorAnimation();
                                colorAnimation.addPolyline(lineForLib);
                                ValueAnimator colorAnimator = colorAnimation.getStartColorAnim();
                                DrawPolylineAnimation drawPolylineAnimation = new DrawPolylineAnimation();
                                drawPolylineAnimation.setPolyline(lineForLib);
                                ValueAnimator drawAnim = drawPolylineAnimation.getDrawAnim();
                                if (animatorSet == null) {
                                    animatorSet = new AnimatorSet();
                                } else {
                                    animatorSet.removeAllListeners();
                                    animatorSet.end();
                                    animatorSet.cancel();

                                    animatorSet = new AnimatorSet();
                                }
                                animatorSet.playSequentially(drawAnim, colorAnimator);
                                animatorSet.start();
                            }
                        });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private LatLng getItemsFromData(String singleUser) throws JSONException {
//            JSONObject singleUser = (JSONObject) entry.getValue();
        JSONObject jsonObject = new JSONObject(singleUser);
        LatLng pos= new LatLng(Double.parseDouble(String.valueOf( jsonObject.get("lat"))),Double.parseDouble(String.valueOf( jsonObject.get("lng"))));
        return pos;
    }



    private void getItemsFromData(Map<String, Object> users) throws JSONException {

        for (Map.Entry<String, Object> entry : users.entrySet()) {
            String singleUser = (String) entry.getValue();
            JSONObject jsonObject = new JSONObject(singleUser);
            LatLng pos= new LatLng(Double.parseDouble(String.valueOf( jsonObject.get("lat"))),Double.parseDouble(String.valueOf( jsonObject.get("lng"))));
            latLngs.add(pos);
//            textView.setText(textView.getText() + "\n" + singleUser);
        }
    }
}
