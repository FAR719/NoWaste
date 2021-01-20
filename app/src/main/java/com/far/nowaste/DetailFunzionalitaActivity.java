package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DetailFunzionalitaActivity extends AppCompatActivity {

    // variabili
    Toolbar mToolbar;

    // view
    TextView testoTextView, domandaTextView, rispTextView;
    ImageView mArrowBtn;
    RecyclerView mFAQList;
    FirestoreRecyclerAdapter adapter;

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
        mFAQList = findViewById(R.id.FAQ_recyclerView);
        mArrowBtn = findViewById(R.id.recView_faq_ImageBtn);

        // variabili passate
        Intent in = getIntent();
        nome = in.getStringExtra("com.far.nowaste.FUNZ_NOME");
        testo = in.getStringExtra("com.far.nowaste.FUNZ_TESTO");

        // cambia il titolo della toolbar
        mToolbar.setTitle(nome);
        testoTextView.setText(testo);

        // query
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        Query query = fstore.collection("funzionalita").document(nome).collection("faq")
    }

    // ends this activity (back arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}