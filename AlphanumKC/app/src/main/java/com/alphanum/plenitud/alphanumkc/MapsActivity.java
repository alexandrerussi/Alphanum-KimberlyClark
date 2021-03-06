package com.alphanum.plenitud.alphanumkc;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Dispenser;
import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMapLongClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String PATH_USER = "usuarios";
    private static final String PATH_LATITUDE_USER = "latitudeUser";
    private static final String PATH_LONGITUDE_USER = "longitudeUser";
    private static final String PATH_DISPENSER = "dispenser";
    private static final int REQUEST_GPS = 1;
    private DrawerLayout drawerLayout;

    private Usuarios usuario;

    float latitude;
    float longitude;
    private GoogleApiClient mGoogleApiClient;

    FirebaseUser userFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();
    DatabaseReference referenceFirebase = ConfiguracaoFirebase.getFirebase();
    DatabaseReference userReference = ConfiguracaoFirebase.getFirebase()
            .child(PATH_USER).child(userFirebase.getUid());

    private LocationRequest mLocationRequest;

    private NavigationView navigationView;

    private GoogleMap mMap, mMapUser;

    private LatLng localUser;

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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario = dataSnapshot.getValue(Usuarios.class);


                View header = navigationView.getHeaderView(0);
                TextView txtNavName = (TextView) header.findViewById(R.id.txt_nav_header_nome);
                txtNavName.setText(usuario.getNomeUser());

                TextView txtNavEmail = (TextView) header.findViewById(R.id.txt_nav_header_email);
                txtNavEmail.setText(usuario.getEmailUser());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_button, new ButtonFragment()).commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient != null) {
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
                Intent tela_conta = new Intent(MapsActivity.this, PerfilActivity.class);
                startActivity(tela_conta);
                break;

            case R.id.nav_carteira:
                Intent tela_carteira = new Intent(MapsActivity.this, CarteiraActivity.class);
                startActivity(tela_carteira);
                break;

            case R.id.nav_loggout:
                FirebaseAuth.getInstance().signOut();
                Intent sair = new Intent(MapsActivity.this, MainActivity.class);
                sair.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sair);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context,
                                                        @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_box);
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

        //Recuperando e mostrando os Dispensers
        mMap = googleMap;
        mMapUser = googleMap;

        mMap.setPadding(0,0,0,200);

        referenceFirebase.child(PATH_DISPENSER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Dispenser dispenser = data.getValue(Dispenser.class);
                    latitude = dispenser.getLatitude();
                    longitude = dispenser.getLongitude();

                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                            .title("R$ 1,00 por cada fralda")
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_box1)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Recuperando e mostrando a posição do usuario
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuario = dataSnapshot.getValue(Usuarios.class);
                        if (checkLocationPermission()) {

                           localUser = new LatLng(usuario.getLatitudeUser(), usuario.getLongitudeUser());

                           mMapUser.setMyLocationEnabled(true);
                            mMapUser.moveCamera(CameraUpdateFactory.newLatLng(localUser));
                            mMapUser.moveCamera(CameraUpdateFactory.newLatLngZoom(localUser, 14));

                        } else {
                            requestPermission();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }, 2000);

        setSettingsMap();
        callConnection();

    }

    public boolean checkLocationPermission() {

        return (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GPS: {
                if (grantResults.length > 0) {

                    boolean gpsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (gpsAccepted) {

                    } else
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("Você precisa permitir o acesso a sua localização",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                            REQUEST_GPS);

                                                }
                                            }
                                        });
                                return;
                            }

                        }

                }

            }
            break;
        }
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
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void startLocationUpdate() {
        if (checkLocationPermission()) {
            initLocationRequest();
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient,
                            mLocationRequest,
                            MapsActivity.this);
        } else {
            requestPermission();
        }
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi
                .removeLocationUpdates(mGoogleApiClient,
                        MapsActivity.this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkLocationPermission()) {
                    Location location =
                            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (location != null) {
                        userReference.child(PATH_LATITUDE_USER).setValue(location.getLatitude());
                        userReference.child(PATH_LONGITUDE_USER).setValue(location.getLongitude());
                        localUser = new LatLng(location.getLatitude(),location.getLongitude());
                        moveCameraMyLocation(localUser);
                        mMapUser.setMyLocationEnabled(true);
                        mMapUser.moveCamera(CameraUpdateFactory.newLatLngZoom(localUser, 14));
                    }

                } else {
                    requestPermission();
                }


            }
        }, 2000);

        initLocationRequest();
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
        localUser = new LatLng(location.getLatitude(), location.getLongitude());

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MapsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void abrirQR(View view) {
        Intent qrcode = new Intent(MapsActivity.this, QRCodeActivity.class);
        startActivity(qrcode);
    }

    public void abrirMomentoHuggies(View view) {
        Intent momento = new Intent(MapsActivity.this, MomentoHuggiesActivity.class);
        startActivity(momento);
    }

    private void msgToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public void moverCamera(View view){
        moveCameraMyLocation(localUser);
    }

    private void moveCameraMyLocation(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(localUser));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    private void setSettingsMap() {
        try {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //Eventos
            mMap.setOnMapLongClickListener(this);


        } catch (Exception e) {
            msgToast("Erro ao realizar configuração do mapa. Detalhes: " + e.getMessage());
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        createMarker("Click no mapa", latLng);
    }

    public void createMarker(String title, LatLng position) {
        MarkerOptions marker = new MarkerOptions();
        marker.title(title);
        marker.position(position);

        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

}

