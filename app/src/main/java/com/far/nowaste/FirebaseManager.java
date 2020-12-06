package com.far.nowaste;

import androidx.annotation.Nullable;

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
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("rifiuti").document(string).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                rifiuto = value.toObject(Rifiuto.class);
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
                    utente = value.toObject(Utente.class);
                }
            });
            firebaseFirestore.terminate();
            return utente;
        } else {
            return null;
        }
    }
}