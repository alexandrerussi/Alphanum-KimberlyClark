package com.alphanum.plenitud.alphanumkc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Dispenser;
import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private DrawerLayout drawerLayout;

    float latitude;
    float longitude;
    Integer qtdAtualMaps;
    private GoogleApiClient mGoogleApiClient;

    Double latitudeUser;
    Double longitudeUser;

    private LocationRequest mLocationRequest;


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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_button, new ButtonFragment()).commit();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            startLocationUpdate();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient != null){
            stopLocationUpdate();
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


        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getFirebase();
        DatabaseReference teste1 = referenceFirebase.child("usuarios").child("ROrUcl5EhCZ1IkmW5oLj4FhM5LO2");

        teste1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuarios user = dataSnapshot.getValue(Usuarios.class);

                Double latitudeT = user.getLatitudeUser();
                Double longitudeT = user.getLongitudeUser();

                if (checkLocationPermission()) {
                    callConnection();
                    LatLng localUser = new LatLng(latitudeT, longitudeT);
                    mMap.addMarker(new MarkerOptions().position(localUser)).setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_my_location));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(localUser));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localUser, 14));


                } else {
                    requestPermission();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                msgToast("Operação cancelada");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void abrirQR(View view) {
        Intent qrcode = new Intent(MapsActivity.this, QRCodeActivity.class);
        startActivity(qrcode);
    }

    private void msgToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void startLocationUpdate() {
        if (checkLocationPermission()) {
            initLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest,
                    MapsActivity.this);

        } else {
            requestPermission();
        }
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MapsActivity.this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (checkLocationPermission()) {

            Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (l != null) {
                //TODO melhorar esta parte do codigo para automatizar de acordo ao usuario
                DatabaseReference referenceFirebase = ConfiguracaoFirebase.getFirebase();
                referenceFirebase.child("usuarios").child("ROrUcl5EhCZ1IkmW5oLj4FhM5LO2").child("latitudeUser").setValue(l.getLatitude());
                referenceFirebase.child("usuarios").child("ROrUcl5EhCZ1IkmW5oLj4FhM5LO2").child("longitudeUser").setValue(l.getLongitude());
            }

        } else {
            requestPermission();
        }

        startLocationUpdate();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed(" + connectionResult + ")");
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}

