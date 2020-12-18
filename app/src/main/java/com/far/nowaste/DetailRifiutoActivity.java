package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.far.nowaste.Objects.Rifiuto;
import com.far.nowaste.Objects.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class DetailRifiutoActivity extends AppCompatActivity {

    Toolbar mToolbar;

    // firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser fUser;

    Rifiuto rifiuto;
    //Utente utente;
    Utente currentUser;

    // view
    TextView nomeTextView;
    TextView materialeTextView;
    TextView descrizioneTextView;
    ImageView immagineImageView;
    FloatingActionButton addBtn;
    FloatingActionButton checkBtn;

    // nome rifiuto
    String stringName;

    // animazioni
    boolean isMenuOpen;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rifiuto);

        // toolbar
        mToolbar = findViewById(R.id.detailSearch_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // to launch the activity
        Intent in = getIntent();

        // variabile passata
        stringName = in.getStringExtra("com.far.nowaste.NAME");

        currentUser = MainActivity.CURRENTUSER;

        // associazione view
        nomeTextView = findViewById(R.id.detailRifiuto_nomeTextView);
        materialeTextView = findViewById(R.id.detailRifiuto_materialeTextView);
        descrizioneTextView = findViewById(R.id.detailRifiuto_descrizioneTextView);
        immagineImageView = findViewById(R.id.detailRifiuto_rifiutoImageView);

        // imposta l'animazione del floating button
        initFloatingMenu();

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
                Glide.with(getApplicationContext()).load(rifiuto.getImmagine()).into(immagineImageView);
            }
        });

        /*if (fUser != null) {
            // query per istanziare un Utente
            fStore.collection("users").document(fUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    utente = value.toObject(Utente.class);
                }
            });
        }*/
    }

    public void loadPunteggio() {
        if (currentUser != null){
            // carica punteggio in firestore
            DocumentReference documentReference = fStore.collection("users").document(fUser.getUid());
            Map<String,Object> userMap = new HashMap<>();
            switch (rifiuto.getSmaltimento()){
                case "Plastica":
                    userMap.put("nPlastica", currentUser.getnPlastica() + 1);
                    userMap.put("pPlastica", currentUser.getpPlastica() + rifiuto.getPunteggio());
                    break;
                case "Organico":
                    userMap.put("nOrganico", currentUser.getnOrganico() + 1);
                    userMap.put("pOrganico", currentUser.getpOrganico() + rifiuto.getPunteggio());
                    break;
                case "Indifferenziata":
                    userMap.put("nIndifferenziata", currentUser.getnIndifferenziata() + 1);
                    userMap.put("pIndifferenziata", currentUser.getpIndifferenziata() + rifiuto.getPunteggio());
                    break;
                case "Carta":
                    userMap.put("nCarta", currentUser.getnCarta() + 1);
                    userMap.put("pCarta", currentUser.getpCarta() + rifiuto.getPunteggio());
                    break;
                case "Vetro":
                    userMap.put("nVetro", currentUser.getnVetro() + 1);
                    userMap.put("pVetro", currentUser.getpVetro() + rifiuto.getPunteggio());
                    break;
                case "Metalli":
                    userMap.put("nMetalli", currentUser.getnMetalli() + 1);
                    userMap.put("pMetalli", currentUser.getpMetalli() + rifiuto.getPunteggio());
                    break;
                case "Elettrici":
                    userMap.put("nElettrici", currentUser.getnElettrici() + 1);
                    userMap.put("pElettrici", currentUser.getpElettrici() + rifiuto.getPunteggio());
                    break;
                case "Speciali":
                    userMap.put("nSpeciali", currentUser.getnSpeciali() + 1);
                    userMap.put("pSpeciali", currentUser.getpSpeciali() + rifiuto.getPunteggio());
                    break;
            }
            documentReference.update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(DetailRifiutoActivity.this, "Rifiuto aggiunto", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onSuccess: user data is updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailRifiutoActivity.this, "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onFailure: " + e.toString());
                }
            });
        } else {
            Toast.makeText(DetailRifiutoActivity.this, "Devi accedere per memorizzare i tuoi progressi!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initFloatingMenu() {
        // associa le view e imposta le icone bianche
        addBtn = findViewById(R.id.detailRifiuto_addFloatingActionButton);
        checkBtn = findViewById(R.id.detailRifiuto_checkFloatingActionButton);

        checkBtn.setAlpha(0f);
        checkBtn.setTranslationY(100f);
        checkBtn.setScaleX(0.7f);
        checkBtn.setScaleY(0.7f);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFloatingMenu();
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPunteggio();
                animateFloatingMenu();
            }
        });
    }

    private void animateFloatingMenu(){
        if(isMenuOpen){
            addBtn.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
            checkBtn.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
            isMenuOpen = false;
        } else {
            addBtn.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
            checkBtn.animate().translationY(15f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
            isMenuOpen = true;
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