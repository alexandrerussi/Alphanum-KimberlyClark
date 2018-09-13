package com.alphanum.plenitud.alphanumkc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CadastroDispenserActivity extends AppCompatActivity {

    private EditText edtNomeDispenser, edtLocalDispenser;

    private Button btnCadastrarDispenser_Tela, btnVoltarCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_dispenser);

        //TODO chamar a activity de cadastro de dispenser após identificar qrcode

        //Importando EditTexts
        edtNomeDispenser = (EditText) findViewById(R.id.edtNomeDispenser);
        edtLocalDispenser = (EditText) findViewById(R.id.edtLocalDispenser);

        //Importando botoes
        btnCadastrarDispenser_Tela = (Button) findViewById(R.id.btnCadastrarDispenser_Tela);
        btnVoltarCadastro = (Button) findViewById(R.id.btnVoltarCadastro);


        //CLIQUES NOS BOTOES
        btnCadastrarDispenser_Tela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO cadastrar nome e local dispenser, depois de cadastrar não esquecer de ativar visibility visible no id inicio_dispenser
            }
        });

        btnVoltarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroDispenserActivity.this, InicioActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
}
