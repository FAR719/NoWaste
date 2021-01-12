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

public class DetailFunzionalitaActivity extends AppCompatActivity {

    // variabili
    Toolbar mToolbar;
    Funzionalita funzionalita;

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

    /*@Override
    protected void onStart() {
        super.onStart();
        // query per istanziare la funzionalità e impostare la view
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("funzionalita").whereEqualTo("nome", stringName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                funzionalita = documentSnapshot.toObject(Funzionalita.class);

                testoTextView.setText(funzionalita.getTesto());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
            }
        });
    }*/
}