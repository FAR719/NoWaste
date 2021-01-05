package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.far.nowaste.objects.Rifiuto;
import com.far.nowaste.objects.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class DetailRifiutoActivity extends AppCompatActivity {

    RelativeLayout layout;
    Typeface nunito;

    Toolbar mToolbar;

    // firebase
    FirebaseAuth fAuth;
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

        layout = findViewById(R.id.detailRifiuto_layout);

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);

        // toolbar
        mToolbar = findViewById(R.id.detailRifiuto_toolbar);
        setSupportActionBar(mToolbar);

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
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // query per istanziare il rifiuto e impostare le view
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("rifiuti").document(stringName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                rifiuto = documentSnapshot.toObject(Rifiuto.class);

                nomeTextView.setText(rifiuto.getNome());
                materialeTextView.setText(rifiuto.getMateriale());
                descrizioneTextView.setText(rifiuto.getDescrizione());
                Glide.with(getApplicationContext()).load(rifiuto.getImmagine()).into(immagineImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
            }
        });
    }

    public void loadPunteggio() {
        if (currentUser != null){
            // carica punteggio in firestore
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            int numero;
            double punteggio;
            Map<String,Object> userMap = new HashMap<>();
            switch (rifiuto.getSmaltimento()){
                case "Plastica":
                    numero = currentUser.getnPlastica() + 1;
                    punteggio = currentUser.getpPlastica() + rifiuto.getPunteggio();
                    userMap.put("nPlastica", numero);
                    userMap.put("pPlastica", punteggio);
                    MainActivity.CURRENTUSER.setnPlastica(numero);
                    MainActivity.CURRENTUSER.setpPlastica(punteggio);
                    break;
                case "Organico":
                    numero = currentUser.getnOrganico() + 1;
                    punteggio = currentUser.getpOrganico() + rifiuto.getPunteggio();
                    userMap.put("nOrganico", numero);
                    userMap.put("pOrganico", punteggio);
                    MainActivity.CURRENTUSER.setnOrganico(numero);
                    MainActivity.CURRENTUSER.setpOrganico(punteggio);
                    break;
                case "Secco":
                    numero = currentUser.getnSecco() + 1;
                    punteggio = currentUser.getpSecco() + rifiuto.getPunteggio();
                    userMap.put("nSecco", numero);
                    userMap.put("pSecco", punteggio);
                    MainActivity.CURRENTUSER.setnSecco(numero);
                    MainActivity.CURRENTUSER.setpSecco(punteggio);
                    break;
                case "Carta":
                    numero = currentUser.getnCarta() + 1;
                    punteggio = currentUser.getpCarta() + rifiuto.getPunteggio();
                    userMap.put("nCarta", numero);
                    userMap.put("pCarta", punteggio);
                    MainActivity.CURRENTUSER.setnCarta(numero);
                    MainActivity.CURRENTUSER.setpCarta(punteggio);
                    break;
                case "Vetro":
                    numero = currentUser.getnVetro() + 1;
                    punteggio = currentUser.getpVetro() + rifiuto.getPunteggio();
                    userMap.put("nVetro", numero);
                    userMap.put("pVetro", punteggio);
                    MainActivity.CURRENTUSER.setnVetro(numero);
                    MainActivity.CURRENTUSER.setpVetro(punteggio);
                    break;
                case "Metalli":
                    numero = currentUser.getnMetalli() + 1;
                    punteggio = currentUser.getpMetalli() + rifiuto.getPunteggio();
                    userMap.put("nMetalli", numero);
                    userMap.put("pMetalli", punteggio);
                    MainActivity.CURRENTUSER.setnMetalli(numero);
                    MainActivity.CURRENTUSER.setpMetalli(punteggio);
                    break;
                case "Elettrici":
                    numero = currentUser.getnElettrici() + 1;
                    punteggio = currentUser.getpElettrici() + rifiuto.getPunteggio();
                    userMap.put("nElettrici", numero);
                    userMap.put("pElettrici", punteggio);
                    MainActivity.CURRENTUSER.setnElettrici(numero);
                    MainActivity.CURRENTUSER.setpElettrici(punteggio);
                    break;
                case "Speciali":
                    numero = currentUser.getnSpeciali() + 1;
                    punteggio = currentUser.getpSpeciali() + rifiuto.getPunteggio();
                    userMap.put("nSpeciali", numero);
                    userMap.put("pSpeciali", punteggio);
                    MainActivity.CURRENTUSER.setnSpeciali(numero);
                    MainActivity.CURRENTUSER.setpSpeciali(punteggio);
                    break;
            }
            fStore.collection("users").document(fUser.getUid()).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showSnackbar("Rifiuto aggiunto!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                    showSnackbar("Il rifiuto non è stato aggiunto correttamente!");
                }
            });
        } else {
            showSnackbar("Accedi per memorizzare i tuoi progressi!");
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

    private void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT).setAnchorView(addBtn)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}