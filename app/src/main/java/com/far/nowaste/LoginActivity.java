package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    EditText mEmail, mPassword;
    Button mLoginBtn, mResendBtn;
    TextView mResetBtn, mRegisterBtn, mWarning;
    Button mGoogleBtn;
    ProgressBar progressBar;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    // login Google
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 101;
    List<String> emails;

    // // num: se 1 home e logout, se 2 profilo
    static int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        num = 0;

        // toolbar
        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // collegamento view
        mEmail = findViewById(R.id.emailEditText);
        mPassword = findViewById(R.id.passwordEditText);
        mLoginBtn = findViewById(R.id.loginButton);
        mResetBtn = findViewById(R.id.resetPassTextView);
        progressBar = findViewById(R.id.progressBar);
        mGoogleBtn = findViewById(R.id.googleButton);
        mRegisterBtn = findViewById(R.id.lRegisterTextView);
        mWarning = findViewById(R.id.warningTextView);
        mResendBtn = findViewById(R.id.sendEmailButton);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // controlla le info aggiunte
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Inserisci la tua email.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Inserisci la password.");
                    return;
                }

                if (password.length() < 8) {
                    mPassword.setError("La password deve essere lunga almeno 8 caratteri");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login completato.", Toast.LENGTH_SHORT).show();
                            verificaEmail();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        // reset password tramite email
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Inserisci la tua email.");
                    return;
                }

                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this, "Email di reset password inviata", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                    }
                });
                finish();
            }
        });

        // Configure Google Sign In
        createRequest();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                signIn();
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        mResendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Email di verifica inviata", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // resetta emails
        emails = null;
        emails = new LinkedList<>();

        // aggiorna emails
        FirebaseUser fUser = fAuth.getCurrentUser();
        if (fUser == null) {
            fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            emails.add(document.getString("email"));
                        }
                    }
                }
            });
        } else {
            verificaEmail();
        }
    }

    // ends this activity (back arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (fAuth.getCurrentUser() != null && !fAuth.getCurrentUser().isEmailVerified()) {
                fAuth.signOut();
            }
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Configure Google Sign In
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser fUser = fAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Hai effettuato l'accesso come " + fUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                            // crea utente in Firestore se non esiste
                            createFirestoreUser();
                            num = 2;
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // se accedi con Google crea l'utente in firestore (se non è già presente)
    private void createFirestoreUser() {
        FirebaseUser fUser = fAuth.getCurrentUser();
        if (!exists(fUser)) {
            Utente utente = new Utente(fUser.getDisplayName(), fUser.getEmail(), fUser.getPhotoUrl().toString());
            DocumentReference documentReference = fStore.collection("users").document(fUser.getUid());
            documentReference.set(utente).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("TAG", "onSuccess: user Profile is created for " + fUser.getUid());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: " + e.toString());
                }
            });
        }
    }

    // metodo che restituisce un boolean che indica se l'account è già presente in Firestore
    private boolean exists(FirebaseUser fUser){
        boolean esiste = false;
        for (String email : emails) {
            if (fUser.getEmail().equals(email)){
                esiste = true;
                break;
            }
        }
        return esiste;
    }

    // non accedere se la mail non è stata verificata
    private void verificaEmail(){
        FirebaseUser fUser = fAuth.getCurrentUser();
        // impNum: se 1 logout
        if (ImpostazioniFragment.impNum == 1) {
            num = 1;
            finish();
        } else if (fUser.isEmailVerified()){
            num = 2;
            finish();
        } else {
            mEmail.setVisibility(View.GONE);
            mPassword.setVisibility(View.GONE);
            mLoginBtn.setVisibility(View.GONE);
            mResetBtn.setVisibility(View.GONE);
            mGoogleBtn.setVisibility(View.GONE);
            mRegisterBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mWarning.setVisibility(View.VISIBLE);
            mResendBtn.setVisibility(View.VISIBLE);
        }
    }
}