package com.alphanum.plenitud.alphanumkc;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.alphanum.plenitud.alphanumkc.adapter.ProdutoAdapter;
import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.alphanum.plenitud.alphanumkc.model.Produtos;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

public class InicioActivity extends AppCompatActivity {

    Button btn_sair, btnCadastrarDispenser;

    RelativeLayout inicio_tab, produtos_tab, loja_tab, perfil_tab, inicio_cadastro_dispenser, inicio_dispenser;

    TextView txt_nome, txt_email, txt_datanasc, txt_telefone, txt_facebook, txt_pontos_usuario, txt_qtdDispenserPorCem, txt_qtdDispenser, txt_qtdDispenserMaximo, txt_nome_bemvindo;

    Toolbar toolbar;

    ListView lv_produtos;

    ProgressBar pb_dispenser;

    DatabaseReference referenciaFirebase;

    ArrayAdapter adapter; //Array para criar lista de produtos
    ArrayList<Produtos> produtos_array; //produtos que serão cadastrados ao usuario

    private static final int BARCODE_RECO_REQ_CODE = 200;

    private static final String PATH_PRODUSER = "prodUsuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //QTD PONTOS USUARIO
        Integer pontos = 201;//TODO chamar qtd de pontos do usuario

        //config toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Alphanum App - Pontos: " + pontos.toString());
        setSupportActionBar(toolbar);


        inicio_tab = (RelativeLayout) findViewById(R.id.inicio_tab);
        produtos_tab = (RelativeLayout) findViewById(R.id.produtos_tab);
        loja_tab = (RelativeLayout) findViewById(R.id.loja_tab);
        perfil_tab = (RelativeLayout) findViewById(R.id.perfil_tab);

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_inicio:
                        inicio_tab.setVisibility(View.VISIBLE);
                        produtos_tab.setVisibility(View.GONE);
                        loja_tab.setVisibility(View.GONE);
                        perfil_tab.setVisibility(View.GONE);

                        break;
                    case R.id.action_produtos:
                        inicio_tab.setVisibility(View.GONE);
                        produtos_tab.setVisibility(View.VISIBLE);
                        loja_tab.setVisibility(View.GONE);
                        perfil_tab.setVisibility(View.GONE);
                        break;
                    case R.id.action_loja:
                    inicio_tab.setVisibility(View.GONE);
                    produtos_tab.setVisibility(View.GONE);
                    loja_tab.setVisibility(View.VISIBLE);
                    perfil_tab.setVisibility(View.GONE);
                    break;

                    case R.id.action_perfil:
                        inicio_tab.setVisibility(View.GONE);
                        produtos_tab.setVisibility(View.GONE);
                        loja_tab.setVisibility(View.GONE);
                        perfil_tab.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });


        //INICIO
        //Tela sem dispenser
        btnCadastrarDispenser = (Button) findViewById(R.id.btnCadastrarDispenser);//Botao para cadastrar novos dispensers
        inicio_cadastro_dispenser = (RelativeLayout) findViewById(R.id.inicio_cadastro_dispenser);//tela para exibir qdo nao tiver dispensers cadastrados
        inicio_dispenser = (RelativeLayout) findViewById(R.id.inicio_dispenser); //tela exibir qdo tiver dispensers cadastrados
        txt_nome_bemvindo = (TextView) findViewById(R.id.txt_nome_bemvindo);//Nome bem vindo

        //TODO Chamar nome da base e colocar na linnha abaixo
        txt_nome_bemvindo.setText("Joãozinho");
        //TODO Referente a explicação de cada relativelayout acima, exibir uma ou outra caso tenha dispenser cadastrado ou nao. Ou seja, fazer um if para fazer essa comparação no firebase

        //Para testar botão
        //TODO Fazer tela de cadastro de dispenser. PARAGUAIO, pode fazer do seu jeito e eu arrumo o design dps se quiser, só tenta seguir o padrão de IDs, por favor
       /* btnCadastrarDispenser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicio_cadastro_dispenser.setVisibility(View.GONE);
                inicio_dispenser.setVisibility(View.VISIBLE);
            }
        });*/

        //Tela com dispenser
        txt_qtdDispenserPorCem = (TextView) findViewById(R.id.txt_qtdDispenserPorCem);
        txt_qtdDispenser = (TextView) findViewById(R.id.txt_qtdDispenser);
        txt_qtdDispenserMaximo = (TextView) findViewById(R.id.txt_qtdDispenserMaximo);

        //TODO Chamar do firebase todas as informações de qtds do dispenser
        Integer qtdDispenserPorCem, qtdDispenser, qtdDispenserMaximo;
        qtdDispenser = 14;//Qtd realtime de fraldas no dispenser
        qtdDispenserMaximo = 20;//Qtd maxima de fraldas no dispenser
        qtdDispenserPorCem = (qtdDispenser * 100) / qtdDispenserMaximo; //Porcentagem de fraldas realtime no dispenser

        txt_qtdDispenser.setText(qtdDispenser.toString());
        txt_qtdDispenserMaximo.setText(qtdDispenserMaximo.toString());
        txt_qtdDispenserPorCem.setText(qtdDispenserPorCem.toString() + "%");

        pb_dispenser = (ProgressBar) findViewById(R.id.pb_dispenser);
        //pb_dispenser.setProgress(qtdDispenserPorCem);
        pb_dispenser.setSecondaryProgress(qtdDispenserPorCem);

        /*if (qtdDispenserPorCem < 30){
            pb_dispenser.setSecondaryProgressTintList();
        }*/

        //----------------------------------------------------------------------

        //PRODUTOS
        produtos_array = new ArrayList<>();

        lv_produtos = (ListView) findViewById(R.id.lv_produtos);
        adapter = new ProdutoAdapter(InicioActivity.this, produtos_array);
        lv_produtos.setAdapter(adapter);

        //TODO Recuperar produtos do firebase


        //String identificadorUsuarioLogado
        // TODO Aqui vai ter q identificar o Id do usuario logado pra usar aqui em baixo no segundo child,
        // no qual esta acessando o nó do usuario que esta cadastrados os produtos nos mesmos, ou seja,
        // vai ter que fazer o cadastro dos produtos ligado ao usuario.
        referenciaFirebase = ConfiguracaoFirebase.getFirebase()
                                .child("prodUsuario")
                                .child("t1e2s3t4e5");//NÓ PARA TESTE AQUI VEM O ID DO USUARIO

        //Listner para recuperar contatos
        referenciaFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//metodo chamado sempre que o nó mudar (ser add um produto ao usuario)

                //Limpar lista
                produtos_array.clear();

                //Listar produtos
                for (DataSnapshot dados: dataSnapshot.getChildren()){

                    Produtos produto = dados.getValue(Produtos.class);
                    produtos_array.add(produto);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //----------------------------------------------------------------------

        //PERFIL
        //Arredondando e chamando img
        RoundedImageView image = RoundedImageView.class.cast(findViewById(R.id.img_perfil));
        image.setImageResource(R.mipmap.ic_launcher);//TODO Chamar imagem do usuario no firebase

        txt_nome = (TextView) findViewById(R.id.txt_nome);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_datanasc = (TextView) findViewById(R.id.txt_datanasc);
        txt_telefone = (TextView) findViewById(R.id.txt_telefone);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BARCODE_RECO_REQ_CODE){
            if(resultCode == RESULT_OK){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                barcodeRecognition(photo);
            }
        }

    }

    private void barcodeRecognition(Bitmap photo) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        for (FirebaseVisionBarcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
                            //edtName.setText(rawValue);
                            Toast.makeText(InicioActivity.this, rawValue, Toast.LENGTH_LONG).show();

                            if(rawValue != null){
                                inicio_cadastro_dispenser.setVisibility(View.GONE);
                                inicio_dispenser.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(InicioActivity.this, "QR vazio", Toast.LENGTH_SHORT).show();
                            }



                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InicioActivity.this, "Erro", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    //config actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_inicio:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent sair = new Intent(InicioActivity.this, MainActivity.class);
                sair.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sair);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //codigo para colocar 4 itens no bottomnavigationview
    public static class BottomNavigationViewHelper {
        @SuppressLint("RestrictedApi")
        public static void removeShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("BottomNav", "Unable to get shift mode field", e);
            } catch (IllegalAccessException e) {
                Log.e("BottomNav", "Unable to change value of shift mode", e);
            }
        }
    }

    public void barcodeReco(View view){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, BARCODE_RECO_REQ_CODE);

    }
}
