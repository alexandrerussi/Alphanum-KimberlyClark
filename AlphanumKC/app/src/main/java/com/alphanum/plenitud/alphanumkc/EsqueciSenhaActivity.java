package com.alphanum.plenitud.alphanumkc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class EsqueciSenhaActivity extends AppCompatActivity {

    EditText edtEmailEsqueciSenha;
    Button btnEnviarEsqueciSenha, btnVoltarEsqueciSenha;
    FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        //IMPORTANDO EDITTEXT
        edtEmailEsqueciSenha = (EditText) findViewById(R.id.edtEmailEsqueciSenha);

        //IMPORTANDO BOTOES
        btnEnviarEsqueciSenha = (Button) findViewById(R.id.btnEnviarEsqueciSenha);
        btnVoltarEsqueciSenha = (Button) findViewById(R.id.btnVoltarEsqueciSenha);

        //SETANDO CLICKS BOTOES

        btnEnviarEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSenha();

                edtEmailEsqueciSenha.setText("");

                retornoInicio(EsqueciSenhaActivity.this, MainActivity.class);


            }
        });

        btnVoltarEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EsqueciSenhaActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    private void resetSenha() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.sendPasswordResetEmail(edtEmailEsqueciSenha.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            msg(EsqueciSenhaActivity.this,
                                    "Email para reset enviado com sucesso",
                                    Toast.LENGTH_SHORT);

                        }else {

                            msg(EsqueciSenhaActivity.this,
                                    "Falhou",
                                    Toast.LENGTH_SHORT);


                        }
                    }

                });
    }

    private void msg(EsqueciSenhaActivity esqueciSenhaActivity, String s, int lengthShort) {
        Toast.makeText(esqueciSenhaActivity, s, lengthShort).show();
    }

    private void retornoInicio(Context packageContext, Class<?> cls) {

        Intent i = new Intent(packageContext, cls);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }
}
