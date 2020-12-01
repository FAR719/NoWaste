package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class DetailSearchActivity extends AppCompatActivity {

    Toolbar mToolbar;

    private FirebaseFirestore firebaseFirestore;

    TextView nomeTextView;
    TextView materialeTextView;
    TextView descrizioneTextView;
    TextView punteggioTextView;
    ImageView immagineImageView;

    String stringName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_search);

        // toolbar
        mToolbar = findViewById(R.id.detailSearch_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // to launch the activity
        Intent in = getIntent();

        // variabile passata
        stringName = in.getStringExtra("com.far.nowaste.NAME");

        // associazione view
        nomeTextView = findViewById(R.id.detailSearch_nomeTextView);
        materialeTextView = findViewById(R.id.detailSearch_materialeTextView);
        descrizioneTextView = findViewById(R.id.detailSearch_descrizioneTextView);
        punteggioTextView = findViewById(R.id.detailSearch_punteggioTextView);
        immagineImageView = findViewById(R.id.detailSearch_rifiutoImageView);

        // associazione firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // query
        firebaseFirestore.collection("rifiuti").document(stringName).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String nome = value.getString("nome");
                String materiale = value.getString("materiale");
                String smaltimento = value.getString("smaltimento");
                String descrizione = value.getString("descrizione");
                String punteggio = value.get("punteggio") + "";
                String immagine = value.getString("immagine");

                nomeTextView.setText(nome);
                materialeTextView.setText(materiale);
                descrizioneTextView.setText(descrizione);
                punteggioTextView.setText(punteggio);
                Glide.with(getApplicationContext()).load(immagine).into(immagineImageView);
            }
        });
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