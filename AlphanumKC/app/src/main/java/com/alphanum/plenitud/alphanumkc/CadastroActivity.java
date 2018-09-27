package com.alphanum.plenitud.alphanumkc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class CadastroActivity extends AppCompatActivity {

    FirebaseAuth autenticacao;

    EditText edtNomeCadastro, edtEmailCadastro, edtSenhaCadastro, edtDataNascCadastro, edtTelefoneCadastro;
    Button btnCadastrar, btnVoltarCadastro;
    Usuarios usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //IMPORTANDO EDITTEXTS
        edtNomeCadastro = (EditText) findViewById(R.id.edtNomeCadastro);
        edtDataNascCadastro = (EditText) findViewById(R.id.edtDataNascCadastro);
        edtTelefoneCadastro = (EditText) findViewById(R.id.edtTelefoneCadastro);
        edtEmailCadastro = (EditText) findViewById(R.id.edtEmailCadastro);
        edtSenhaCadastro = (EditText) findViewById(R.id.edtSenhaCadastro);

        //IMPORTANDO BOTOES
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        btnVoltarCadastro = (Button) findViewById(R.id.btnVoltarCadastro);

        //MASCARAS PARA EDT'S
        SimpleMaskFormatter simpleMaskDataNasc = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher maskDataNasc = new MaskTextWatcher(edtDataNascCadastro, simpleMaskDataNasc);
        edtDataNascCadastro.addTextChangedListener(maskDataNasc);

        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("(NN) NNNNN-NNNN");
        MaskTextWatcher maskTelefone = new MaskTextWatcher(edtTelefoneCadastro, simpleMaskTelefone);
        edtTelefoneCadastro.addTextChangedListener(maskTelefone);

        //SETANDO CLICKS BOTOES

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario = new Usuarios();
                usuario.setNomeUser(edtNomeCadastro.getText().toString());
                usuario.setDataNascUser(edtDataNascCadastro.getText().toString());
                usuario.setTelefoneUser(edtTelefoneCadastro.getText().toString());
                usuario.setEmailUser(edtEmailCadastro.getText().toString());
                usuario.setSenhaUser(edtSenhaCadastro.getText().toString());
                usuario.setLatitudeUser(0.0);
                usuario.setLongitudeUser(0.0);
                usuario.setSaldo(0.0);
                cadastrarUsuario();


            }
        });

        btnVoltarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               retornoInicio(CadastroActivity.this, MainActivity.class);
            }
        });
    }

    private void cadastrarUsuario() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmailUser(), usuario.getSenhaUser())
                .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Cadastro feito com sucesso", Toast.LENGTH_SHORT).show();

                            FirebaseUser usuarioFirebase = task.getResult().getUser();
                            usuario.setIdUser(usuarioFirebase.getUid());
                            usuario.salvar();

                            retornoInicio(CadastroActivity.this, MainActivity.class);


                        }else{

                            String erroCadastro = "";

                            try{
                                throw task.getException();

                            } catch (FirebaseAuthWeakPasswordException e) {
                                erroCadastro = "Digite uma senha mais forte";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroCadastro = "O email editado é invalido";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erroCadastro = "Esse email já esta em uso";

                            } catch (Exception e) {
                                erroCadastro = "Ao cadastrar";
                                e.printStackTrace();
                            }

                            Toast.makeText(getApplicationContext(), "Erro: " + erroCadastro, Toast.LENGTH_SHORT).show();

                        }

                    }


                });
    }

    private void retornoInicio(Context packageContext, Class<?> cls) {

        Intent i = new Intent(packageContext, cls);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }
}
