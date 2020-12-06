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
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class DetailSearchActivity extends AppCompatActivity {

    Toolbar mToolbar;

    // firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    Rifiuto rifiuto;
    Utente utente;
    FirebaseUser fUser;

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
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // query per istanziare il rifiuto e impostare le view
        fStore.collection("rifiuti").document(stringName).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                rifiuto = value.toObject(Rifiuto.class);

                nomeTextView.setText(rifiuto.getNome());
                materialeTextView.setText(rifiuto.getMateriale());
                descrizioneTextView.setText(rifiuto.getDescrizione());
                punteggioTextView.setText(Html.fromHtml(rifiuto.getPunteggio() + "g di CO<sup><small>2</small></sup>"));
                Glide.with(getApplicationContext()).load(rifiuto.getImmagine()).into(immagineImageView);
            }
        });

        if (fUser != null) {
            // query per istanziare un Utente
            fStore.collection("users").document(fUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    utente = value.toObject(Utente.class);
                }
            });
        }
    }

    public void loadPunteggio(View view) {
        if (fUser != null){
            // carica punteggio in firestore
            DocumentReference documentReference = fStore.collection("users").document(fUser.getUid());
            Map<String,Object> userMap = new HashMap<>();
            switch (rifiuto.getSmaltimento()){
                case "Plastica":
                    userMap.put("nPlastica", utente.getnPlastica() + 1);
                    userMap.put("pPlastica", utente.getpPlastica() + rifiuto.getPunteggio());
                    break;
                case "Organico":
                    userMap.put("nOrganico", utente.getnOrganico() + 1);
                    userMap.put("pOrganico", utente.getpOrganico() + rifiuto.getPunteggio());
                    break;
                case "Indifferenziata":
                    userMap.put("nIndifferenziata", utente.getnIndifferenziata() + 1);
                    userMap.put("pIndifferenziata", utente.getpIndifferenziata() + rifiuto.getPunteggio());
                    break;
                case "Carta":
                    userMap.put("nCarta", utente.getnCarta() + 1);
                    userMap.put("pCarta", utente.getpCarta() + rifiuto.getPunteggio());
                    break;
                case "Vetro":
                    userMap.put("nVetro", utente.getnVetro() + 1);
                    userMap.put("pVetro", utente.getpVetro() + rifiuto.getPunteggio());
                    break;
                case "Metalli":
                    userMap.put("nMetalli", utente.getnMetalli() + 1);
                    userMap.put("pMetalli", utente.getpMetalli() + rifiuto.getPunteggio());
                    break;
                case "Elettrici":
                    userMap.put("nElettrici", utente.getnElettrici() + 1);
                    userMap.put("pElettrici", utente.getpElettrici() + rifiuto.getPunteggio());
                    break;
                case "Speciali":
                    userMap.put("nSpeciali", utente.getnSpeciali() + 1);
                    userMap.put("pSpeciali", utente.getpSpeciali() + rifiuto.getPunteggio());
                    break;
            }
            documentReference.update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(DetailSearchActivity.this, "Rifiuto aggiunto", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onSuccess: user data is updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailSearchActivity.this, "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onFailure: " + e.toString());
                }
            });
        } else {
            Toast.makeText(DetailSearchActivity.this, "Devi accedere per memorizzare i tuoi progressi!", Toast.LENGTH_SHORT).show();
        }
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