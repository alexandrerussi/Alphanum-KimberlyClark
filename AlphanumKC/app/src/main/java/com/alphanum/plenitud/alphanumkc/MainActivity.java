package com.alphanum.plenitud.alphanumkc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //Variavel para Autenticação no Firebase JREM
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private CallbackManager mCallbackManager;

    //Para conectar com Google JREM
    private GoogleApiClient googleApiClient;
    private SignInButton btnGoogle;
    public static final int SIGN_IN_CODE = 777;


    private RelativeLayout rellay1, rellay2;
    private EditText edtEmailLogin, edtSenhaLogin;
    private Button btnEntrar, btnCadastro, btnEsqueciSenha, btnGoogleLayout;


    private GoogleSignInAccount contaGoogle;

    private Usuarios usuario;

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

        handler.postDelayed(runnable, 1500); //1500 é o tempo do splashscreen

        //IMPORTANDO EDITTEXTS
        edtEmailLogin = (EditText) findViewById(R.id.edtEmailLogin);
        edtSenhaLogin = (EditText) findViewById(R.id.edtSenhaLogin);

        //IMPORTANDO BOTÕES
        btnEntrar = (Button) findViewById(R.id.btnEntrar);
        btnCadastro = (Button) findViewById(R.id.btnCadastro);
        btnEsqueciSenha = (Button) findViewById(R.id.btnEsqueciSenha);

        btnGoogleLayout = (Button) findViewById(R.id.btnGoogleLayout);


        //Verifica se tem login no Firebase JREM
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmailLogin.getText().toString().trim().isEmpty() ||
                        edtSenhaLogin.getText().toString().trim().isEmpty()){
                    msgToast(R.string.insira_email_e_senha);
                }
                else {
                    firebaseAuth.signInWithEmailAndPassword(edtEmailLogin.getText().toString().trim(),
                            edtSenhaLogin.getText().toString().trim())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        passarIntent(MainActivity.this, MapsActivity.class);
                                    }else{
                                        msgToast(R.string.email_ou_senha_erradas);
                                    }
                                }
                            });
                }

            }
        });

        //Conectando com o callManager JREM
        mCallbackManager = CallbackManager.Factory.create();

        //Login com Google JREM
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
                    passarIntent(MainActivity.this, MapsActivity.class);
                }
            }
        };

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passarIntent(MainActivity.this, CadastroActivity.class);
            }
        });

        btnEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               passarIntent(MainActivity.this, EsqueciSenhaActivity.class);
            }
        });
    }

    //Conexão com Google JREM
    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, SIGN_IN_CODE);
    }

    //Tratamento de resultado Google JREM
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
                contaGoogle = result.getSignInAccount();
            }else{
                msgToast(R.string.erro_na_conexao);
            }
    }

    //Conexão Firebase com Google JREM
    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser usuarioFirebase = task.getResult().getUser();

                    usuario = new Usuarios();
                    usuario.setIdUser(usuarioFirebase.getUid());
                    usuario.setNomeUser(contaGoogle.getDisplayName());
                    usuario.setDataNascUser("");
                    usuario.setTelefoneUser("");
                    usuario.setEmailUser(contaGoogle.getEmail());
                    usuario.setLatitudeUser(0.0);
                    usuario.setLongitudeUser(0.0);
                    usuario.setSaldo(0.0);
                    usuario.salvar();


                    }else {
                    msgToast(R.string.erro_na_conexao);
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

    private void msgToast(int i) {
        Toast.makeText(getApplicationContext(), i, Toast.LENGTH_SHORT).show();
    }

    private void passarIntent(Context context, Class c){
        Intent i = new Intent(context, c);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

}
