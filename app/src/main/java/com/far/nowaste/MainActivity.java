package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.far.nowaste.fragments.CalendarioFragment;
import com.far.nowaste.fragments.ContattaciFragment;
import com.far.nowaste.fragments.CuriositaFragment;
import com.far.nowaste.fragments.ProfileFragment;
import com.far.nowaste.fragments.HomeFragment;
import com.far.nowaste.fragments.ImpostazioniFragment;
import com.far.nowaste.fragments.LuoghiFragment;
import com.far.nowaste.objects.Utente;
import com.far.nowaste.ui.main.SearchToolbarAnimation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // toolbar
    MenuItem mSearchItem;
    private Toolbar mToolbar;

    // navigationView
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    static public Utente CURRENTUSER;

    View header;
    TextView mFullName, mEmail;
    ImageView mImage;

    Typeface nunito;

    // firebase
    FirebaseAuth fAuth;
    FirebaseStorage storage;
    StorageReference storageReference;

    // home 1, profilo 2, curiosità 3, calendario 4, luoghi 5, contattaci 6, impostazioni 7
    int fragment;

    // variabili per la night mode
    int nightMode;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        nightMode = sharedPreferences.getInt("NightModeInt", -1);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        setContentView(R.layout.activity_main);

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);

        CURRENTUSER = null;

        // toolbar
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // navigationView
        drawerLayout = findViewById(R.id.main_drawerlayout);
        navigationView = findViewById(R.id.main_navView);

        // navigationView
        navigationView.bringToFront();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.openNavDrawer, R.string.closeNavDrawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // associazione view dell'header
        header = navigationView.getHeaderView(0);

        mFullName = header.findViewById(R.id.navHeader_nameTextView);
        mEmail = header.findViewById(R.id.navHeader_emailTextView);
        mImage = header.findViewById(R.id.navHeader_userImageView);

        // header onclick
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth = FirebaseAuth.getInstance();
                if (fAuth.getCurrentUser() == null) {
                    goToLogin();
                } else {
                    mToolbar.setTitle("Profilo");
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.main_frameLayout, new ProfileFragment()).commit();
                    fragment = 2;
                    navigationView.setCheckedItem(R.id.nav_invisible);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            fragment = 1;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // imposta CURRENTUSER
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        if (fUser != null) {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("users").document(fUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    CURRENTUSER = documentSnapshot.toObject(Utente.class);

                    mFullName.setText(CURRENTUSER.getFullName());
                    mEmail.setText(CURRENTUSER.getEmail());
                    mFullName.setVisibility(View.VISIBLE);
                    if (CURRENTUSER.getImage() != null) {
                        Glide.with(getApplicationContext()).load(CURRENTUSER.getImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                }
            });
        } else {
            mEmail.setText("Accedi al tuo account");
            mFullName.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        mSearchItem = menu.findItem(R.id.m_search);

        // crea le animazioni
        new SearchToolbarAnimation(getWindow(), mToolbar, mSearchItem, getApplicationContext(), getResources(), sharedPreferences).setAnimation();

        //set queryListener searchView
        SearchView wasteSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        wasteSearchView.setQueryHint("Cerca un rifiuto...");
        wasteSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent listaSearchActivity = new Intent(getApplicationContext(), ListaSearchActivity.class);
                listaSearchActivity.putExtra("com.far.nowaste.SEARCH_QUERY", query);
                startActivity(listaSearchActivity);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    // onclick sulla navigation
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()){
            case R.id.nav_home:
                if (fragment != 1) {
                    mToolbar.setTitle("NoWaste");
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .replace(R.id.main_frameLayout, new HomeFragment()).commit();
                    fragment = 1;
                }
                break;
            case R.id.nav_curiosita:
                if (fragment != 3) {
                    mToolbar.setTitle("Curiosità");
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.main_frameLayout, new CuriositaFragment()).commit();
                    fragment = 3;
                }
                break;
            case R.id.nav_calendario:
                if (fragment != 4) {
                    mToolbar.setTitle("Calendario");
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.main_frameLayout, new CalendarioFragment()).commit();
                    fragment = 4;
                }
                break;
            case R.id.nav_luoghi:
                if (fragment != 5) {
                    mToolbar.setTitle("Luoghi");
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.main_frameLayout, new LuoghiFragment()).commit();
                    fragment = 5;
                }
                break;
            case R.id.nav_contattaci:
                if (fragment != 6) {
                    mToolbar.setTitle("Contattaci");
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.main_frameLayout, new ContattaciFragment()).commit();
                    fragment = 6;
                }
                break;
            case R.id.nav_impostazioni:
                if (fragment != 7) {
                    mToolbar.setTitle("Impostazioni");
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.main_frameLayout, new ImpostazioniFragment()).commit();
                    fragment = 7;
                }
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean detailUserRequest = false;
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            detailUserRequest = data.getBooleanExtra("com.far.nowaste.detailUserRequest", false);
        }
        if (detailUserRequest) {
            showSnackbar("Hai effettuato l'accesso come " + fAuth.getCurrentUser().getDisplayName());
            mToolbar.setTitle("Profilo");
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main_frameLayout, new ProfileFragment()).commit();
            fragment = 2;
            navigationView.setCheckedItem(R.id.nav_invisible);
        }
    }

    // chiude la navigation quando premi back
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if(fragment != 1){
            mToolbar.setTitle("NoWaste");
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .replace(R.id.main_frameLayout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            fragment = 1;
        } else {
            super.onBackPressed();
        }
    }

    // metodi impostazioni
    public void goToLogin(){
        startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), 1);
    }

    public void changeEmail(String password, String email){
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();

        // re-authenticate the user
        AuthCredential credential;
        credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // cambia la mail in Auth
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // cambia la mail in firestore
                        Map<String,Object> emailMap = new HashMap<>();
                        emailMap.put("email", email);
                        fStore.collection("users").document(user.getUid()).update(emailMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showSnackbar("Email aggiornata!");
                                CURRENTUSER.setEmail(email);
                                updateHeader();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("LOG", "Error! " + e.getLocalizedMessage());
                                showSnackbar("Errore! Email non aggiornata correttamente.");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LOG", "Error! " + e.getLocalizedMessage());
                        showSnackbar("Errore! Email non aggiornata correttamente.");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    showSnackbar("La password inserita non è corretta.");
                } else {
                    showSnackbar(e.getLocalizedMessage());
                }
            }
        });
    }

    // password from settings
    public void changePassword(String oldPass, String newPass){
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();

        // re-authenticate the user
        AuthCredential credential;
        credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);

        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // cambia la mail in Auth
                user.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showSnackbar("Password aggiornata!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LOG", "Error! " + e.getLocalizedMessage());
                        showSnackbar("La password non è stata aggiornata correttamente.");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    showSnackbar("La password inserita non è corretta.");
                } else {
                    showSnackbar(e.getLocalizedMessage());
                }
            }
        });
    }

    // logout from settings
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        CURRENTUSER = null;
        showSnackbar("Logout effettuato!");
        updateHeader();
        mToolbar.setTitle("NoWaste");
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
        fragment = 1;
    }

    // delete account from settings
    public void deleteAccount(String password) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();

        // re-authenticate the user
        AuthCredential credential;
        credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // cancella l'utente da firestore
                fStore.collection("users").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Account eliminato da FireStore.");
                        // se esiste, elimina la proPic da storage
                        if (CURRENTUSER.getImage() != null) {
                            storage = FirebaseStorage.getInstance();
                            storageReference = storage.getReference();
                            final String key = CURRENTUSER.getEmail();
                            StorageReference picRef = storageReference.child("proPics/" + key);
                            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "Foto eliminata da storage.");
                                    // cancella l'utente da fireauth
                                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showSnackbar("Account eliminato!");
                                            CURRENTUSER = null;
                                            updateHeader();
                                            mToolbar.setTitle("NoWaste");
                                            getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, new HomeFragment()).commit();
                                            navigationView.setCheckedItem(R.id.nav_home);
                                            fragment = 1;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("LOG", "Error! " + e.getLocalizedMessage());
                                            showSnackbar("Account non eliminato correttamente.");
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                                    showSnackbar("Account non eliminato correttamente.");
                                }
                            });
                        } else {
                            // cancella l'utente da fireauth
                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showSnackbar("Account eliminato!");
                                    CURRENTUSER = null;
                                    updateHeader();
                                    mToolbar.setTitle("NoWaste");
                                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, new HomeFragment()).commit();
                                    navigationView.setCheckedItem(R.id.nav_home);
                                    fragment = 1;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                                    showSnackbar("Account non eliminato correttamente.");
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LOG", "Error! " + e.getLocalizedMessage());
                        showSnackbar("Account non eliminato correttamente!");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    showSnackbar("La password inserita non è corretta.");
                } else {
                    showSnackbar(e.getLocalizedMessage());
                }
            }
        });
    }

    public void updateHeader(){
        if (CURRENTUSER != null) {
            mFullName.setText(CURRENTUSER.getFullName());
            mEmail.setText(CURRENTUSER.getEmail());
            mFullName.setVisibility(View.VISIBLE);
            if (CURRENTUSER.getImage() != null) {
                Glide.with(getApplicationContext()).load(CURRENTUSER.getImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
            } else {
                Drawable defaultImage = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_user);
                mImage.setImageDrawable(defaultImage);
            }
        } else {
            mEmail.setText("Accedi al tuo account");
            mFullName.setVisibility(View.GONE);
            Drawable defaultImage = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_user);
            mImage.setImageDrawable(defaultImage);
        }
    }

    public void goToSettings() {
        mToolbar.setTitle("Impostazioni");
        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main_frameLayout, new ImpostazioniFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_impostazioni);
        fragment = 7;
    }

    public void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(drawerLayout, string, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}