package com.alphanum.plenitud.alphanumkc;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Dispenser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import static android.Manifest.permission.CAMERA;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private int teste = 0;

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;

    private ImageButton btn_flash, btn_digitar;
    private int estadoCamera = 0;

    private FrameLayout fragment_digit_qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        mScannerView = new ZXingScannerView(getApplicationContext());
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relative_scan_take_single);
        rl.addView(mScannerView);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                /*Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();*/


            } else {
                requestPermission();
            }
        }

        //Click botao flash
        btn_flash = (ImageButton) findViewById(R.id.btn_flash);

        btn_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estadoCamera == 0) {
                    mScannerView.setFlash(true);
                    estadoCamera = 1;
                    btn_flash.setImageResource(R.drawable.ic_highlight_off_black_24dp);
                } else if (estadoCamera == 1) {
                    mScannerView.setFlash(false);
                    estadoCamera = 0;
                    btn_flash.setImageResource(R.drawable.ic_highlight_black_24dp);
                }
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_digit_qr, new DigitarQrCodeFragment()).commit();
        }

        btn_digitar = (ImageButton) findViewById(R.id.btn_digitar);
        fragment_digit_qr = (FrameLayout) findViewById(R.id.fragment_digit_qr);

        btn_digitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_digit_qr.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    RelativeLayout rl = (RelativeLayout) findViewById(R.id.relative_scan_take_single);
                    rl.addView(mScannerView);
                }
                mScannerView.setLaserEnabled(false);
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    //Recebe o valor do QR Code
    @Override
    public void handleResult(final Result result) {

        DatabaseReference referenceQrCode = ConfiguracaoFirebase.getFirebase();
        final DatabaseReference resultQr = referenceQrCode.child("dispenser").child(result.toString());


        resultQr.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Dispenser dispenser = dataSnapshot.getValue(Dispenser.class);
                try {
                    switch (dispenser.getLinkQr()) {
                        case 0:
                            if (teste == 0) {
                                msgToast("Produto pago");
                                Intent i = new Intent(QRCodeActivity.this, MapsActivity.class);
                                startActivity(i);
                                resultQr.child("linkQr").setValue(1);
                                teste = 1;
                            }
                            break;
                        case 1:
                            if (teste == 0) {
                                msgToast("Produto em uso");
                            }
                            break;
                        default:
                            msgToast("Deu merda");
                    }

                } catch (Exception ex){
                    Log.i("Error", ex.toString());
                    msgToast("Dispenser não registrado");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mScannerView.resumeCameraPreview(this);
    }

    private void msgToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        //msgToast("Permission Granted, Now you can access camera");
                    } else {
                        //msgToast("Permission Denied, You cannot access and camera");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
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

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(QRCodeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void fecharDigitaQR(View view) {
        fragment_digit_qr.setVisibility(View.GONE);
    }

    public void enviarCodigo(View view) {
        Toast.makeText(this, "Enviado", Toast.LENGTH_SHORT).show();
        //TODO ENVIAR CODIGO DE QR CODE, senha como quiserem chamar
    }
}
