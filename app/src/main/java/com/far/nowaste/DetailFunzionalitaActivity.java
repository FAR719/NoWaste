package com.far.nowaste;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailFunzionalitaActivity extends AppCompatActivity {

    // variabili
    Toolbar mToolbar;

    // nome funzionalità
    String stringName;

    // view
    TextView testoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_funzionalita);

        // toolbar
        mToolbar = findViewById(R.id.detailFunzionalita_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // to launch the activity
        Intent in = getIntent();

        // variabile passata
        stringName = in.getStringExtra("com.far.nowaste.NOMEfunzionalita");

        // cambia il titolo della toolbar
        mToolbar.setTitle(stringName);

        // associazione view
        testoTextView = findViewById(R.id.detailFunzionalita_testoTextView);

    }
}