package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.far.nowaste.objects.Funzionalita;
import com.far.nowaste.objects.Rifiuto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DetailFunzionalitaActivity extends AppCompatActivity {

    // variabili
    Toolbar mToolbar;

    // view
    TextView testoTextView;

    String nome;
    String testo;


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

        // associazione view
        testoTextView = findViewById(R.id.detailFunzionalita_testoTextView);

        // variabili passate
        Intent in = getIntent();
        nome = in.getStringExtra("com.far.nowaste.FUNZ_NOME");
        testo = in.getStringExtra("com.far.nowaste.FUNZ_TESTO");

        // cambia il titolo della toolbar
        mToolbar.setTitle(nome);
        testoTextView.setText(testo);
    }
}