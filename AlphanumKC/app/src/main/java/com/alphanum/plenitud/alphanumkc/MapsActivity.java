package com.alphanum.plenitud.alphanumkc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Dispenser;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback ,NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    float latitude;
    float longitude;
    Integer qtdAtualMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //config toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTrans);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbar.setTitleTextColor(00000000);
        toolbar.setSubtitleTextColor(00000000);

        toolbar.bringToFront();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (savedInstanceState == null) {
            /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_toolbar, new ToolbarTransparente()).commit();*/
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_button, new ButtonFragment()).commit();
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_conta:
                Intent intent = new Intent(MapsActivity.this, PerfilActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_carteira:
                break;

            case R.id.nav_loggout:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent sair = new Intent(MapsActivity.this, MainActivity.class);
                sair.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sair);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private GoogleMap mMap;

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_box1);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final DatabaseReference dispenserLatLon = ConfiguracaoFirebase.getFirebase().child("dispenser");

        dispenserLatLon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Dispenser dispenser = data.getValue(Dispenser.class);

                    latitude = dispenser.getLatitude();
                    longitude = dispenser.getLongitude();
                    qtdAtualMaps = dispenser.getQtdAtual();

                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                            .title(data.getKey().toString())
                            .snippet(qtdAtualMaps.toString())
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_box1)));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Add a marker in Sydney and move the camera;
        LatLng localUser = new LatLng(-23.4844651, -46.6301937);
        mMap.addMarker(new MarkerOptions().position(localUser));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(localUser));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localUser, 15));
        //mMap.setMyLocationEnabled(true);
        //mMap.setTrafficEnabled(true);


    }

    public void abrirQR(View view){
        Intent qrcode = new Intent(MapsActivity.this, QRCodeActivity.class);
        startActivity(qrcode);
    }
}
