package com.far.nowaste;

import android.text.Html;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FirebaseManager {

    // definizione campi
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Utente utente;
    Rifiuto rifiuto;

    // costruttore
    public FirebaseManager(){}

    public Rifiuto getRifiuto(String string){
        // query per istanziare il rifiuto e impostare le view
        firebaseFirestore.collection("rifiuti").document(string).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                rifiuto = new Rifiuto(value.getString("nome"),
                        value.getString("descrizione"),
                        value.getString("materiale"),
                        value.getString("smaltimento"),
                        value.getDouble("punteggio"),
                        value.getString("immagine"));
            }
        });
        firebaseFirestore.terminate();
        return rifiuto;
    }

    public Utente getLoggedUser(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = firebaseAuth.getCurrentUser();

        if (fUser != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("users").document(fUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    utente = new Utente(value.getString("fullName"), value.getString("email"),
                            value.getDouble("nPlastica"), value.getDouble("pPlastica"),
                            value.getDouble("nOrganico"), value.getDouble("pOrganico"),
                            value.getDouble("nIndifferenziata"), value.getDouble("pIndifferenziata"),
                            value.getDouble("nCarta"), value.getDouble("pCarta"),
                            value.getDouble("nVetro"),value.getDouble("pVetro"),
                            value.getDouble("nMetalli"),value.getDouble("pMetalli"),
                            value.getDouble("nElettrici"), value.getDouble("pElettrici"),
                            value.getDouble("nSpeciali"), value.getDouble("pSpeciali"));
                }
            });
            firebaseFirestore.terminate();
            return utente;
        } else {
            return null;
        }
    }
}
