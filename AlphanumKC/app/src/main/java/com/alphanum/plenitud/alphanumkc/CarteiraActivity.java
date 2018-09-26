package com.alphanum.plenitud.alphanumkc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class CarteiraActivity extends AppCompatActivity {

    private TextView txt_saldo_carteira;
    private Button btn_comprar_credito;

    DatabaseReference referenceFirebase = ConfiguracaoFirebase.getFirebase();
    DatabaseReference usuarioReferencia = referenceFirebase.child("usuarios");
    Usuarios usuario = new Usuarios();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carteira);

        //config toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarCarteira);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Minha carteira");

        //Adicionando botao de voltar toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_saldo_carteira = (TextView) findViewById(R.id.txt_saldo_carteira);
        btn_comprar_credito = (Button) findViewById(R.id.btn_comprar_credito);

        btn_comprar_credito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarteiraActivity.this, ComprarCreditosActivity.class);
                startActivity(intent);
            }
        });

        //Verificando saldo

        DatabaseReference userEspecifico = usuarioReferencia.child("Qfp6uusS7Wbt9IfWYmddyG8jzfI3");//TODO Recuperar usuario logado

        userEspecifico.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario = dataSnapshot.getValue(Usuarios.class);
                Double saldo = usuario.getSaldo();
                txt_saldo_carteira.setText("R$ " + saldo.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
