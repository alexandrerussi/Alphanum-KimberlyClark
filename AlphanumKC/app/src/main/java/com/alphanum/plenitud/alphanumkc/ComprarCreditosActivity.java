package com.alphanum.plenitud.alphanumkc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Usuarios;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ComprarCreditosActivity extends AppCompatActivity {

    private Button btn_recarregar;
    private EditText edt_temporario;
    public Double saldoNovo = 0.0;

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private static final String PATH_USER = "usuarios";
    FirebaseUser userFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();
    DatabaseReference userReference = ConfiguracaoFirebase.getFirebase()
            .child(PATH_USER).child(userFirebase.getUid());
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

        radioGroup = (RadioGroup) findViewById(R.id.rdg_creditos);

        /*edt_temporario = (EditText) findViewById(R.id.edt_temporario);*/

        btn_recarregar = (Button) findViewById(R.id.btn_recarregar);
        btn_recarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Recuperando valor credito a ser debitado
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId){
                            case R.id.radio_5:
                                saldoNovo = 5.0;
                                break;

                            case R.id.radio_10:
                                saldoNovo = 10.0;
                                break;

                            case R.id.radio_25:
                                saldoNovo = 25.0;
                                break;

                            case R.id.radio_50:
                                saldoNovo = 50.0;
                                break;
                        }
                    }
                });

                /*if (!saldoText.isEmpty()){
                    try {
                        saldoNovo = Double.parseDouble(saldoText);
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(ComprarCreditosActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }*/

                //Verificando saldo
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuario = dataSnapshot.getValue(Usuarios.class);
                        Double saldo = usuario.getSaldo();

                        saldoNovo = 12.0;

                        saldo = saldoNovo + saldo;
                        usuario.setSaldo(saldo);

                        userReference.child("saldo").setValue(usuario.getSaldo());

                        Toast.makeText(ComprarCreditosActivity.this, "Créditos comprados!", Toast.LENGTH_SHORT).show();
                        ComprarCreditosActivity.this.finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

    public void checkRadioButton(View view){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        Toast.makeText(this, "Selected Radio" + radioButton.getText(), Toast.LENGTH_SHORT).show();
    }
}
