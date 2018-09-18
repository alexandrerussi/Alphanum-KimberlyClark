package com.alphanum.plenitud.alphanumkc;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //Variavel para Autenticação no Firebase JREM
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    //Criando CallbackManager para Facebook JREM
    private CallbackManager mCallbackManager;

    //Para conectar com Google JREM
    private GoogleApiClient googleApiClient;
    private SignInButton btnGoogle;
    public static final int SIGN_IN_CODE = 777;


    private RelativeLayout rellay1, rellay2;
    private EditText edtEmailLogin, edtSenhaLogin;
    private Button btnEntrar, btnCadastro, btnEsqueciSenha, btnFacebookLayout, btnGoogleLayout;
    private LoginButton btnFacebook;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 2000); //2000 é o tempo do splashscreen

        //IMPORTANDO EDITTEXTS
        edtEmailLogin = (EditText) findViewById(R.id.edtEmailLogin);
        edtSenhaLogin = (EditText) findViewById(R.id.edtSenhaLogin);

        //IMPORTANDO BOTÕES
        btnEntrar = (Button) findViewById(R.id.btnEntrar);
        btnCadastro = (Button) findViewById(R.id.btnCadastro);
        btnEsqueciSenha = (Button) findViewById(R.id.btnEsqueciSenha);
        btnFacebookLayout = (Button) findViewById(R.id.btnFacebookLayout);
        btnGoogleLayout = (Button) findViewById(R.id.btnGoogleLayout);


        //Verifica se tem login no Firebase JREM
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmailLogin.getText().toString().trim().equals("") || edtSenhaLogin.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(), "Insira email e senha", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.signInWithEmailAndPassword(edtEmailLogin.getText().toString().trim(), edtSenhaLogin.getText().toString().trim())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent i = new Intent(MainActivity.this, InicioActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);


                                    }else{
                                        Toast.makeText(getApplicationContext(), R.string.erro_no_cadastro, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

        //Conectando com o Facebook JREM
        mCallbackManager = CallbackManager.Factory.create();
        btnFacebook = (LoginButton) findViewById(R.id.btnFacebook);
        btnFacebook.setReadPermissions(Arrays.asList("email","public_profile"));
        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAcessToken(loginResult.getAccessToken());
                }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Operação cancelada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("jremeza", "onError: "+ error);
                Toast.makeText(getApplicationContext(), "Erro no login", Toast.LENGTH_SHORT).show();
            }
        });

        //Conectando ação do LoginButton no botao estilizado
        btnFacebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFacebook.performClick();
            }
        });


        //Login com Google JREM
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Conexão com Google JREM --FOI PARA O MÉTODO signIn()--
        btnGoogle = (SignInButton) findViewById(R.id.btnGoogle);


        //Conectando ação do SignInButton no botao estilizado
        btnGoogleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //Instanciando a autenticação com Firebase JREM
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Intent entrar = new Intent(MainActivity.this, InicioActivity.class);
                    entrar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(entrar);
                }

            }
        };



        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cadastro = new Intent(MainActivity.this, CadastroActivity.class);
                startActivity(cadastro);
            }
        });



        btnEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent esqueciSenha = new Intent(MainActivity.this, EsqueciSenhaActivity.class);
                startActivity(esqueciSenha);
            }
        });
    }

    //Conexão com Google JREM
    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, SIGN_IN_CODE);
    }

    // Gera o token do usuario no Facebook JREM
    private void handleFacebookAcessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), R.string.firebase_error_login, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    //Tratamento de resultado Facebook JREM
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignResult(result);
        }
    }

    //Tratamento do resultado da conexão com Google JREM
    private void handleSignResult(GoogleSignInResult result) {
            if(result.isSuccess()){
                firebaseAuthWithGoogle(result.getSignInAccount());
            }else{
                Toast.makeText(this, "Erro no login", Toast.LENGTH_SHORT).show();
            }
    }

    //Conexão Firebase com Google JREM
    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), R.string.firebase_error_login, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //Listener para verificar alteração no Firebase JREM
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);

    }

    //Finaliza a conexão com o firebaseAuth JREM
    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);

    }

    //Metodo para tratamento de falhas na conexão com Google JREM
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
