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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.far.nowaste.objects.Utente;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    TextInputLayout mEmailLayout, mPasswordLayout;
    TextInputEditText mEmail, mPassword;
    Button mLoginBtn, mResendBtn;
    TextView mResetBtn, mRegisterBtn, mWarning;
    Button mGoogleBtn;
    ProgressBar progressBar;
    View mDivider;

    RelativeLayout layout;
    Typeface nunito;

    FirebaseAuth fAuth;

    // login Google
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 101;
    List<String> emails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
        layout = findViewById(R.id.login_layout);

        // toolbar
        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // collegamento view
        mEmail = findViewById(R.id.login_nameInputText);
        mPassword = findViewById(R.id.login_passwordInputText);

        mEmailLayout = findViewById(R.id.login_emailInputLayout);
        mPasswordLayout = findViewById(R.id.login_passwordInputLayout);

        mLoginBtn = findViewById(R.id.loginButton);
        mResetBtn = findViewById(R.id.resetPassTextView);
        mGoogleBtn = findViewById(R.id.googleButton);
        progressBar = findViewById(R.id.progressBar);
        mDivider = findViewById(R.id.login_divider);
        mRegisterBtn = findViewById(R.id.lRegisterTextView);
        mWarning = findViewById(R.id.warningTextView);
        mResendBtn = findViewById(R.id.sendEmailButton);

        fAuth = FirebaseAuth.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // controlla le info aggiunte
                if (TextUtils.isEmpty(email)) {
                    mEmailLayout.setError("Inserisci la tua email.");
                    return;
                } else {
                    mEmailLayout.setErrorEnabled(false);
                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordLayout.setError("Inserisci la password.");
                    return;
                } else {
                    mPasswordLayout.setErrorEnabled(false);
                }

                if (password.length() < 8) {
                    mPasswordLayout.setError("La password deve essere lunga almeno 8 caratteri!");
                    return;
                } else {
                    mPasswordLayout.setErrorEnabled(false);
                }

                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user
                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        verificaEmail();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LOG", "Error! " + e.getLocalizedMessage());
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            String errorCode = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();

                            if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                                mEmailLayout.setError("L'email inserita non è ben formata.");
                            } else if (errorCode.equals("ERROR_WRONG_PASSWORD")){
                                mPasswordLayout.setError("La password inserita non è corretta.");
                            }
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

                            if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                mEmailLayout.setError("L'email inserita non ha un account associato.");
                            } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                                mEmailLayout.setError("Il tuo account è stato disabilitato.");
                            } else {
                                showSnackbar(e.getLocalizedMessage());
                            }
                        }  else {
                            showSnackbar(e.getLocalizedMessage());
                        }
                        progressBar.setVisibility(View.GONE);
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
                    mEmailLayout.setError("Inserisci la tua email.");
                    return;
                }

                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showSnackbar("Email inviata. Controlla la tua posta!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LOG", "Error! " + e.getLocalizedMessage());
                        showSnackbar("Errore! Email non inviata.");
                    }
                });
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
                        showSnackbar("Email inviata. Controlla la tua posta!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LOG", "Error! " + e.getLocalizedMessage());
                        showSnackbar("Errore! Email non inviata.");
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
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        emails.add(document.getString("email"));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                }
            });
        } else {
            verificaEmail();
        }
    }

    // ends this activity (back arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (fAuth.getCurrentUser() != null && !fAuth.getCurrentUser().isEmailVerified()) {
            fAuth.signOut();
        }
        this.finish();
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

        // utile quando si crea un account in RegisterActivity e non si ha ancora una email verificata
        boolean registerRequest = false;

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
                showSnackbar("Accesso con Google fallito.");
                // ...
            }
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            registerRequest = data.getBooleanExtra("com.far.nowaste.REGISTER_REQUEST", false);
        }
        if (registerRequest) {
            showSnackbar("Account creato!");
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    // crea utente in Firestore se non esiste
                    createFirestoreUser();
                    verificaEmail();
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    showSnackbar("Accesso con Google fallito.");
                    Log.d("LOG", "Error! " + task.getException().getLocalizedMessage());
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    // se accedi con Google crea l'utente in firestore (se non è già presente)
    private void createFirestoreUser() {
        FirebaseUser fUser = fAuth.getCurrentUser();
        if (!exists(fUser)) {
            Utente utente = new Utente(fUser.getDisplayName(), fUser.getEmail(), fUser.getPhotoUrl().toString(), true, false, "", "");
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("users").document(fUser.getUid()).set(utente).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("TAG", "onSuccess: user Profile is created for " + fUser.getUid());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showSnackbar("Accesso con Google non effettuato correttamente.");
                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                    progressBar.setVisibility(View.GONE);
                    fAuth.signOut();
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
        if (fUser.isEmailVerified()){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("com.far.nowaste.detailUserRequest", true);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            showSnackbar("Accesso effettuato!");
            mEmailLayout.setVisibility(View.GONE);
            mPasswordLayout.setVisibility(View.GONE);
            mLoginBtn.setVisibility(View.GONE);
            mResetBtn.setVisibility(View.GONE);
            mGoogleBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mDivider.setVisibility(View.GONE);
            mRegisterBtn.setVisibility(View.GONE);
            mWarning.setVisibility(View.VISIBLE);
            mResendBtn.setVisibility(View.VISIBLE);
        }
    }

    private void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}