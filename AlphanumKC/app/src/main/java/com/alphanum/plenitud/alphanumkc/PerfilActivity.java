package com.alphanum.plenitud.alphanumkc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class PerfilActivity extends AppCompatActivity {

    private TextView txt_nome, txt_email, txt_datanasc, txt_telefone, txt_facebook, txt_pontos_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //config toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarPerfil);
        setSupportActionBar(toolbar);

        //Adicionando botao de voltar toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //QTD PONTOS USUARIO
        Integer pontos = 201;//TODO chamar qtd de pontos do usuario do firebase

        //PERFIL
        //Arredondando e chamando img
        RoundedImageView image = RoundedImageView.class.cast(findViewById(R.id.img_perfil));
        image.setImageResource(R.mipmap.ic_launcher);//TODO Chamar imagem do usuario no firebase

        txt_nome = (TextView) findViewById(R.id.txt_nome);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_datanasc = (TextView) findViewById(R.id.txt_datanasc);
        txt_telefone = (TextView) findViewById(R.id.txt_telefone);
        txt_facebook = (TextView) findViewById(R.id.txt_facebook);
        txt_pontos_usuario = (TextView) findViewById(R.id.txt_pontos_usuario);

        //TODO Chamar campos do usuário do firebase
        String nome = "Alexandre Russi";
        String email = "junior.russi2013@gmail.com";
        String dataNasc = "20/05/1999";
        String telefone = "(11)98299-8094";
        String facebook = "/alexandrerussssi";

        txt_nome.setText(nome);
        txt_email.setText(email);
        txt_datanasc.setText(dataNasc);
        txt_telefone.setText(telefone);
        txt_facebook.setText(facebook);
        txt_pontos_usuario.setText(pontos.toString());

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
