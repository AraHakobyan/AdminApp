package com.example.aro_pc.minasyangps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.aro_pc.minasyangps.fragments.ChatFragment;
import com.example.aro_pc.minasyangps.fragments.ChooseUserFragment;
import com.example.aro_pc.minasyangps.fragments.ShowOnMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;

    GoogleMap googleMap;
    private FrameLayout container;
    private FragmentManager fragmentManager;
    private ChooseUserFragment chooseUserFragment;
    private ShowOnMapFragment showOnMapFragment;
    private ChatFragment chatFragment;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        forFragments();

        database = FirebaseDatabase.getInstance();

//        startService(new Intent(this,BackgroundService.class));

    }

    private void forFragments() {
//        chatFragment = new ChatFragment();
        container = (FrameLayout) findViewById(R.id.fragment_map_container);


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
//        getSupportFragmentManager().beginTransaction().replace(container.getId(), chatFragment).commit();
    }

    List<LatLng> latLngs;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       return true;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.chat:
                chatFragment = new ChatFragment();
                chatFragment.setDatabase(database);
                getSupportFragmentManager().beginTransaction().replace(container.getId(),chatFragment).commit();
                break;
            case R.id.map:
                latLngs = new ArrayList<>();
                showOnMapFragment = new ShowOnMapFragment();
                showOnMapFragment.setLatLngs(latLngs);
                showOnMapFragment.setDatabase(database);
                getSupportFragmentManager().beginTransaction().replace(container.getId(),showOnMapFragment).commit();

                break;
            case R.id.users:
                chooseUserFragment = new ChooseUserFragment(getApplicationContext());
                getSupportFragmentManager().beginTransaction().replace(container.getId(),chooseUserFragment).commit();
                break;
        }
        drawer.closeDrawer(Gravity.LEFT);

        item.setChecked(true);
        setTitle(item.getTitle());
        drawer.closeDrawers();
        return true;
    }
}
