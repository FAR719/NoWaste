package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.far.nowaste.objects.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    TextInputLayout mFullNameLayout, mEmailLayout, mPasswordLayout, mPasswordAgainLayout;
    TextInputEditText mFullName, mEmail, mPassword, mPasswordAgain;
    Button mRegisterButton;
    TextView mLoginBtn;
    ProgressBar mProgressBar;

    FirebaseAuth fAuth;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // toolbar
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // collegamento view
        mFullName = findViewById(R.id.login_nameInputText);
        mEmail = findViewById(R.id.login_emailInputText);
        mPassword = findViewById(R.id.login_passwordInputText);
        mPasswordAgain = findViewById(R.id.login_passwordAgainInputText);

        mFullNameLayout = findViewById(R.id.register_nameInputLayout);
        mEmailLayout = findViewById(R.id.register_emailInputLayout);
        mPasswordLayout = findViewById(R.id.register_passwordInputLayout);
        mPasswordAgainLayout = findViewById(R.id.register_passwordAgainInputLayout);

        mRegisterButton = findViewById(R.id.registerButton);
        mLoginBtn = findViewById(R.id.rLogintextView);
        mProgressBar = findViewById(R.id.registerProgressBar);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null){
            finish();
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = mFullName.getText().toString();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passwordAgain = mPasswordAgain.getText().toString().trim();

                // controlla la info aggiunte
                if (TextUtils.isEmpty(fullName)){
                    mFullNameLayout.setError("Inserisci nome e cognome.");
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    mEmailLayout.setError("Inserisci la tua email.");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPasswordLayout.setError("Inserisci la password.");
                    return;
                }

                if (password.length() < 8){
                    mPasswordLayout.setError("La password deve essere lunga almeno 8 caratteri!");
                    return;
                }

                if (passwordAgain.length() == 0){
                    mPasswordAgainLayout.setError("Inserisci nuovamente la password.");
                    return;
                }

                if (!passwordAgain.equals(password)){
                    mPasswordAgainLayout.setError("Le password inserite non corrispondono!");
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                // register the user in firebase
                register(fullName, email, password);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    // register method
    private void register(String fullName, String email, String password) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // verify the email
                    fUser = fAuth.getCurrentUser();
                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "Email di verifica inviata!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                    // store data in firestore
                    createFirestoreUser(fullName);
                }else {
                    Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    // crea l'utente in firebase
    private void createFirestoreUser(String fullName){
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        // insert name into fUser
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();
        fUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "User profile updated.");
                }
            }
        });

        // store data in firestore
        Utente utente = new Utente(fullName, fUser.getEmail(), null, false, false, "", "");
        fStore.collection("users").document(fUser.getUid()).set(utente).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "onSuccess: user Profile is created for " + fUser.getUid());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("com.far.nowaste.REGISTER_REQUEST", true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e.toString());
            }
        });
    }
}