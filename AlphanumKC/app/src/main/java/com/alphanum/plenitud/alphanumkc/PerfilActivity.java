package com.alphanum.plenitud.alphanumkc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PerfilActivity extends AppCompatActivity {

    private TextView txt_nome, txt_email, txt_datanasc, txt_telefone, txt_facebook, txt_pontos_usuario;

    private static final String PATH_USER = "usuarios";
    FirebaseUser userFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();
    DatabaseReference userReference = ConfiguracaoFirebase.getFirebase()
            .child(PATH_USER).child(userFirebase.getUid());

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

        //PERFIL
        //Arredondando e chamando img
        RoundedImageView image = RoundedImageView.class.cast(findViewById(R.id.img_perfil));
        image.setImageResource(R.mipmap.ic_launcher);

        txt_nome = (TextView) findViewById(R.id.txt_nome);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_datanasc = (TextView) findViewById(R.id.txt_datanasc);
        txt_telefone = (TextView) findViewById(R.id.txt_telefone);
        txt_pontos_usuario = (TextView) findViewById(R.id.txt_pontos_usuario);

        if (userFirebase != null){

            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuarios usuario = dataSnapshot.getValue(Usuarios.class);

                    txt_nome.setText(usuario.getNomeUser());
                    txt_email.setText(usuario.getEmailUser());
                    txt_datanasc.setText(usuario.getDataNascUser());
                    txt_telefone.setText(usuario.getTelefoneUser());
                    txt_pontos_usuario.setText("R$ " + usuario.getSaldo().toString());

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


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
