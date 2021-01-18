package com.far.nowaste;

import androidx.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.far.nowaste.fragments.ProfileFragment;
import com.far.nowaste.objects.Saving;
import com.far.nowaste.objects.Rifiuto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;

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

    // view
    TextView nomeTextView;
    TextView materialeTextView;
    TextView descrizioneTextView;
    ImageView immagineImageView;
    FloatingActionButton addBtn;
    FloatingActionButton checkBtn;
    LinearProgressIndicator mProgressIndicator;

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

        // variabile passata
        Intent in = getIntent();
        stringName = in.getStringExtra("com.far.nowaste.NAME");

        // associazione view
        nomeTextView = findViewById(R.id.detailRifiuto_nomeTextView);
        materialeTextView = findViewById(R.id.detailRifiuto_materialeTextView);
        descrizioneTextView = findViewById(R.id.detailRifiuto_descrizioneTextView);
        immagineImageView = findViewById(R.id.detailRifiuto_rifiutoImageView);
        mProgressIndicator = findViewById(R.id.rifiuto_progressIindicator);



        // associazione firebase
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        // imposta l'animazione del floating button
        initFloatingMenu();

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
                Log.e("LOG", "Error! " + e.getLocalizedMessage());
            }
        });
    }

    public void loadPunteggio() {
        if (MainActivity.CURRENT_USER != null){
            CalendarDay currentDay = CalendarDay.today();

            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("users").document(fUser.getUid()).collection("carbon_dioxide")
                    .whereEqualTo("ntipo", rifiuto.getNtipo()).whereEqualTo("year", currentDay.getYear())
                    .whereEqualTo("month", currentDay.getMonth()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()){
                        Saving carbonDioxide = new Saving(rifiuto.getSmaltimento(), rifiuto.getPunteggio(),
                                currentDay.getYear(), currentDay.getMonth(), rifiuto.getNtipo());

                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                        fStore.collection("users").document(fUser.getUid()).collection("carbon_dioxide")
                                .add(carbonDioxide).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                MainActivity.CARBON_DIOXIDE_ARRAY_LIST.get(carbonDioxide.getNtipo()).add(carbonDioxide);
                                MainActivity.QUANTITA[carbonDioxide.getNtipo()]++;
                                showSnackbar("Rifiuto aggiunto!");
                                mProgressIndicator.hide();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("LOG", "Error! " + e.getLocalizedMessage());
                                showSnackbar("Il rifiuto non è stato aggiunto correttamente!");
                                mProgressIndicator.hide();
                            }
                        });
                    } else if (queryDocumentSnapshots.size() == 1) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Saving carbonDioxide = document.toObject(Saving.class);

                            carbonDioxide.setPunteggio(carbonDioxide.getPunteggio() + rifiuto.getPunteggio());
                            carbonDioxide.setQuantita(carbonDioxide.getQuantita() + 1);

                            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                            fStore.collection("users").document(fUser.getUid())
                                    .collection("carbon_dioxide").document(document.getId()).set(carbonDioxide)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            MainActivity.CARBON_DIOXIDE_ARRAY_LIST.get(rifiuto.getNtipo())
                                                    .remove(MainActivity.CARBON_DIOXIDE_ARRAY_LIST.get(rifiuto.getNtipo()).size() - 1);
                                            MainActivity.CARBON_DIOXIDE_ARRAY_LIST.get(rifiuto.getNtipo()).add(carbonDioxide);
                                            MainActivity.QUANTITA[carbonDioxide.getNtipo()]++;
                                            showSnackbar("Rifiuto aggiunto!");
                                            mProgressIndicator.hide();
                                        }
                                    });
                        }
                    } else {
                        Log.e("LOG", "Errore! Ci sono più istanze dello stesso mese nel database.");
                        showSnackbar("Il rifiuto non è stato aggiunto correttamente!");
                        mProgressIndicator.hide();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("LOG", "Error! " + e.getLocalizedMessage());
                    showSnackbar("Il rifiuto non è stato aggiunto correttamente!");
                    mProgressIndicator.hide();
                }
            });
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
                if (fUser != null) {
                    mProgressIndicator.show();
                    loadPunteggio();
                    animateFloatingMenu();
                } else {
                    showSnackbar("Accedi per memorizzare i tuoi progressi!");
                }
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
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.inverted_primary_text));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}