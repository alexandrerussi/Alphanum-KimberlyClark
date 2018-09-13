package com.alphanum.plenitud.alphanumkc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class CadastroSenhaActivity extends AppCompatActivity {

    EditText edtSenhaCadastroSenha;
    Button btnCadastrarSenha, btnVoltarSenha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_senha);

        //IMPORTANDO EDITTEXT
        edtSenhaCadastroSenha = (EditText) findViewById(R.id.edtSenhaCadastroSenha);

        //IMPORTANDO BOTOES
        btnCadastrarSenha = (Button) findViewById(R.id.btnCadastrarSenha);
        btnVoltarSenha = (Button) findViewById(R.id.btnVoltarSenha);

        //SETANDO CLICKS BOTOES

        btnCadastrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





            }
        });

        btnVoltarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(CadastroSenhaActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }
}
