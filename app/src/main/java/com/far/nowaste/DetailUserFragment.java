package com.far.nowaste;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class DetailUserFragment extends Fragment {

    // definizione variabili
    TextView mFullName, mEmail, verifyMsg;
    Button resendCode;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_user, container, false);

        // collegamento view
        mFullName = view.findViewById(R.id.nameTextView);
        mEmail = view.findViewById(R.id.emailTextView);
        verifyMsg = view.findViewById(R.id.verifyTextView);
        resendCode = view.findViewById(R.id.verifyButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        /*// imposta dati personali
        fStore.collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String nome = value.getString("fullName");
                String email = value.getString("email");

                mFullName.setText(nome);
                mEmail.setText(email);
            }
        });*/

        // verifica email
        if (!user.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);
            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Email di verifica inviata", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        } else {
            verifyMsg.setVisibility(View.GONE);
            resendCode.setVisibility(View.GONE);
        }

        return view;
    }

    // onclick logout button
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getContext(), "Logout effettuato.", Toast.LENGTH_SHORT).show();
    }
}
