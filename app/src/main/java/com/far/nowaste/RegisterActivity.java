package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.far.nowaste.objects.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    TextInputLayout mFullNameLayout, mEmailLayout, mPasswordLayout, mPasswordAgainLayout;
    TextInputEditText mFullName, mEmail, mPassword, mPasswordAgain;
    Button mRegisterButton;
    TextView mLoginBtn;
    LinearProgressIndicator mProgressIndicator;

    FirebaseAuth fAuth;
    FirebaseUser fUser;

    RelativeLayout layout;
    Typeface nunito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
        layout = findViewById(R.id.register_layout);

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
        mProgressIndicator = findViewById(R.id.register_progressIndicator);

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
                } else {
                    mFullNameLayout.setErrorEnabled(false);
                }

                if (TextUtils.isEmpty(email)){
                    mEmailLayout.setError("Inserisci la tua email.");
                    return;
                } else {
                    mEmailLayout.setErrorEnabled(false);
                }

                if (TextUtils.isEmpty(password)){
                    mPasswordLayout.setError("Inserisci la password.");
                    return;
                } else {
                    mPasswordLayout.setErrorEnabled(false);
                }

                if (password.length() < 8){
                    mPasswordLayout.setError("La password deve essere lunga almeno 8 caratteri!");
                    return;
                } else {
                    mPasswordLayout.setErrorEnabled(false);
                }

                if (passwordAgain.length() == 0){
                    mPasswordAgainLayout.setError("Inserisci nuovamente la password.");
                    return;
                } else {
                    mPasswordAgainLayout.setErrorEnabled(false);
                }

                if (!passwordAgain.equals(password)){
                    mPasswordAgainLayout.setError("Le password inserite non corrispondono!");
                    return;
                } else {
                    mPasswordAgainLayout.setErrorEnabled(false);
                }

                mProgressIndicator.show();

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
        fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
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
                        Log.e("LOG", "Error! " + e.getLocalizedMessage());
                    }
                });
                // store data in firestore
                createFirestoreUser(fullName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LOG", "Error! " + e.getLocalizedMessage());
                if (e instanceof FirebaseAuthWeakPasswordException) {
                    mPasswordLayout.setError("La password inserita non è abbastanza sicura.");
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mEmailLayout.setError("L'email inserita non è ben formata.");
                } else if (e instanceof FirebaseAuthUserCollisionException) {
                    mEmailLayout.setError("L'email inserita ha già un account associato.");
                } else {
                    showSnackbar("Registrazione fallita.");
                }
                mProgressIndicator.hide();
            }
        });
    }

    // crea l'utente in firebase
    private void createFirestoreUser(String fullName){
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        // insert name into fUser
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();
        fUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // store data in firestore
                Utente utente = new Utente(fullName, fUser.getEmail(), "", false, false, "", "");
                fStore.collection("users").document(fUser.getUid()).set(utente).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "onSuccess: user Profile is created for " + fUser.getUid());
                        Map<String, Object> quantitaMap = new HashMap<>();
                        quantitaMap.put("quantita", Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                        fStore.collection("users").document(fUser.getUid()).update(quantitaMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        MainActivity.CURRENT_USER = utente;
                                        Intent returnIntent = new Intent();
                                        returnIntent.putExtra("com.far.nowaste.REGISTER_REQUEST", true);
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressIndicator.hide();
                                showSnackbar("Account non creato correttamente!");
                                Log.e("LOG", "Error! " + e.getLocalizedMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LOG", "Error! " + e.getLocalizedMessage());
                        mProgressIndicator.hide();
                        showSnackbar("Account non creato correttamente!");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar("Account non creato correttamente!");
                mProgressIndicator.hide();
            }
        });
    }

    private void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.inverted_primary_text));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}