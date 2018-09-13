package com.alphanum.plenitud.alphanumkc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alphanum.plenitud.alphanumkc.R;
import com.alphanum.plenitud.alphanumkc.RoundedImageView;
import com.alphanum.plenitud.alphanumkc.model.Produtos;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ProdutoAdapter extends ArrayAdapter<Produtos>{

    private ArrayList<Produtos> produtos;
    private Context context;

    public ProdutoAdapter(@NonNull Context c, @NonNull ArrayList<Produtos> objects) {
        super(c, 0, objects);
        this.produtos = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;



        //Verificar se a lista esta vazia
        if (produtos != null){

            //inicializar objeto para montagem da view

           LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //monta a view a partir do xml
            view = inflater.inflate(R.layout.lista_produtos, parent, false);

            //recupera elemento para exibicao
            RoundedImageView imgProduto = RoundedImageView.class.cast(view.findViewById(R.id.img_produto));
            TextView nomeProduto = (TextView) view.findViewById(R.id.txt_nome_produto);
            TextView pontosProduto = (TextView) view.findViewById(R.id.txt_qtd_pontos);

            Produtos produto = produtos.get(position);
            imgProduto.setImageResource(R.mipmap.ic_launcher);//TODO Chamar img do firebase com aquele storage
            nomeProduto.setText("Nome: " + produto.getNomeProduto());
            pontosProduto.setText("Pontos: " + produto.getPontosProduto());

        }

        return view;

    }
}
