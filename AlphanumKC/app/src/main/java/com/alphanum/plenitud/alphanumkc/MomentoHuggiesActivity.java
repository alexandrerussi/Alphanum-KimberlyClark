package com.alphanum.plenitud.alphanumkc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class MomentoHuggiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momento_huggies);

        //config toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMomento);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbar.setTitleTextColor(00000000);
        toolbar.setSubtitleTextColor(00000000);
        setSupportActionBar(toolbar);

        //Adicionando botao de voltar toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
