package com.alphanum.plenitud.alphanumkc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ComprarCreditosActivity extends AppCompatActivity {

    private Button btn_recarregar;
    private EditText edt_temporario;
    private Double saldoNovo;

    DatabaseReference referenceFirebase = ConfiguracaoFirebase.getFirebase();
    DatabaseReference usuarioReferencia = referenceFirebase.child("usuarios");
    Usuarios usuario = new Usuarios();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_creditos);

        //config toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarComprarCreditos);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Compre créditos");

        //Adicionando botao de voltar toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edt_temporario = (EditText) findViewById(R.id.edt_temporario);
        saldoNovo = Double.parseDouble(edt_temporario.getText().toString());

        btn_recarregar = (Button) findViewById(R.id.btn_recarregar);
        btn_recarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verificando saldo
/*
                final DatabaseReference userEspecifico = usuarioReferencia.child("Qfp6uusS7Wbt9IfWYmddyG8jzfI3");//TODO Recuperar usuario logado

                userEspecifico.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuario = dataSnapshot.getValue(Usuarios.class);
                        Double saldo = usuario.getSaldo();
                        saldo = saldoNovo + saldo;

//                        userEspecifico.child("saldo").setValue(saldo.toString());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/


            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
