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
import androidx.fragment.app.Fragment;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.far.nowaste.fragments.CalendarioFragment;
import com.far.nowaste.fragments.ContattaciFragment;
import com.far.nowaste.fragments.CuriositaFragment;
import com.far.nowaste.fragments.ProfileFragment;
import com.far.nowaste.fragments.HomeFragment;
import com.far.nowaste.fragments.ImpostazioniFragment;
import com.far.nowaste.fragments.LuoghiFragment;
import com.far.nowaste.objects.Saving;
import com.far.nowaste.objects.Settimanale;
import com.far.nowaste.objects.Utente;
import com.far.nowaste.ui.main.SearchToolbarAnimation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.primitives.Ints;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // toolbar
    MenuItem mSearchItem;
    private Toolbar mToolbar;

    // navigationView
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    static public Utente CURRENT_USER;
    static public Settimanale SETTIMANALE;
    static public ArrayList<ArrayList<Saving>> CARBON_DIOXIDE_ARRAY_LIST;
    static public ArrayList<ArrayList<Saving>> ENERGY_ARRAY_LIST;
    static public ArrayList<ArrayList<Saving>> OIL_ARRAY_LIST;
    static public ArrayList<ArrayList<Saving>> OTHER_ARRAY_LIST;
    static public int[] QUANTITA;

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
    Fragment mFragmentToSet;

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

        CURRENT_USER = null;
        mFragmentToSet = null;

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

        fAuth = FirebaseAuth.getInstance();

        QUANTITA = new int[7];

        updateCurrentUser();

        // header onclick
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth = FirebaseAuth.getInstance();
                if (fAuth.getCurrentUser() == null) {
                    goToLogin();
                } else {
                    if (fragment != 2) {
                        mToolbar.setTitle("Profilo");
                        mFragmentToSet = new ProfileFragment();
                        fragment = 2;
                        navigationView.setCheckedItem(R.id.nav_invisible);
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        if (fAuth.getCurrentUser() != null && !fAuth.getCurrentUser().isEmailVerified()) {
            goToLogin();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            fragment = 1;
        }

        // serve per evitare i lag nel navdrawer
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}
            @Override public void onDrawerOpened(@NonNull View drawerView) {}
            @Override public void onDrawerStateChanged(int newState) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (mFragmentToSet != null) {
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.main_frameLayout, mFragmentToSet).commit();
                    mFragmentToSet = null;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateHeader();
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
        switch (item.getItemId()){
            case R.id.nav_home:
                if (fragment != 1) {
                    mToolbar.setTitle("NoWaste");
                    mFragmentToSet = new HomeFragment();
                    fragment = 1;
                }
                break;
            case R.id.nav_curiosita:
                if (fragment != 3) {
                    mToolbar.setTitle("Curiosità");
                    mFragmentToSet = new CuriositaFragment();
                    fragment = 3;
                }
                break;
            case R.id.nav_calendario:
                if (fragment != 4) {
                    mToolbar.setTitle("Calendario");
                    mFragmentToSet = new CalendarioFragment();
                    fragment = 4;
                }
                break;
            case R.id.nav_luoghi:
                if (fragment != 5) {
                    mToolbar.setTitle("Luoghi");
                    mFragmentToSet = new LuoghiFragment();
                    fragment = 5;
                }
                break;
            case R.id.nav_contattaci:
                if (fragment != 6) {
                    mToolbar.setTitle("Contattaci");
                    mFragmentToSet = new ContattaciFragment();
                    fragment = 6;
                }
                break;
            case R.id.nav_impostazioni:
                if (fragment != 7) {
                    mToolbar.setTitle("Impostazioni");
                    mFragmentToSet = new ImpostazioniFragment();
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
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            boolean detailUserRequest = data.getBooleanExtra("com.far.nowaste.detailUserRequest", false);
            if (detailUserRequest) {
                updateCurrentUser();
                showSnackbar("Hai effettuato l'accesso come " + fAuth.getCurrentUser().getDisplayName());
                mToolbar.setTitle("Profilo");
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.main_frameLayout, new ProfileFragment()).commit();
                fragment = 2;
                navigationView.setCheckedItem(R.id.nav_invisible);
            }
        }

    }

    // chiude la navigation quando premi back
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if(fragment == 5 && LuoghiFragment.ISEXPANDED) {
            LuoghiFragment fragment = (LuoghiFragment) getSupportFragmentManager().findFragmentById(R.id.main_frameLayout);
            fragment.animateMap();
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

    public void changeEmail(String email, String password){
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
                                CURRENT_USER.setEmail(email);
                                updateHeader();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("LOG", "Error! " + e.getLocalizedMessage());
                                showSnackbar("Errore! Email non aggiornata correttamente.");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LOG", "Error! " + e.getLocalizedMessage());
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            showSnackbar("L'email inserita non è ben formata.");
                        } else if (e instanceof FirebaseAuthUserCollisionException) {
                            showSnackbar("L'email inserita è già associata ad un account.");
                        } else {
                            showSnackbar(e.getLocalizedMessage());
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LOG", "Error! " + e.getLocalizedMessage());
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
                if (newPass.length() >= 8) {
                    // cambia la mail in Auth
                    user.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showSnackbar("Password aggiornata!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("LOG", "Error! " + e.getLocalizedMessage());
                            if (e instanceof FirebaseAuthWeakPasswordException) {
                                showSnackbar("La password inserita non è abbastanza sicura.");
                            } else {
                                showSnackbar(e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    showSnackbar("La nuova password deve essere lunga almeno 8 caratteri!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LOG", "Error! " + e.getLocalizedMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    showSnackbar("La vecchia password inserita non è corretta.");
                } else {
                    showSnackbar(e.getLocalizedMessage());
                }
            }
        });
    }

    // logout from settings
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        CURRENT_USER = null;
        SETTIMANALE = null;
        CARBON_DIOXIDE_ARRAY_LIST = null;
        ENERGY_ARRAY_LIST = null;
        OIL_ARRAY_LIST = null;
        QUANTITA = null;
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
                        if (!CURRENT_USER.getImage().equals("")) {
                            storage = FirebaseStorage.getInstance();
                            storageReference = storage.getReference();
                            final String key = CURRENT_USER.getEmail();
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
                                            CURRENT_USER = null;
                                            SETTIMANALE = null;
                                            CARBON_DIOXIDE_ARRAY_LIST = null;
                                            ENERGY_ARRAY_LIST = null;
                                            OIL_ARRAY_LIST = null;
                                            QUANTITA = null;
                                            updateHeader();
                                            mToolbar.setTitle("NoWaste");
                                            getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, new HomeFragment()).commit();
                                            navigationView.setCheckedItem(R.id.nav_home);
                                            fragment = 1;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("LOG", "Error! " + e.getLocalizedMessage());
                                            showSnackbar("Account non eliminato correttamente.");
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("LOG", "Error! " + e.getLocalizedMessage());
                                    showSnackbar("Account non eliminato correttamente.");
                                }
                            });
                        } else {
                            // cancella l'utente da fireauth
                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showSnackbar("Account eliminato!");
                                    CURRENT_USER = null;
                                    SETTIMANALE = null;
                                    CARBON_DIOXIDE_ARRAY_LIST = null;
                                    ENERGY_ARRAY_LIST = null;
                                    OIL_ARRAY_LIST = null;
                                    QUANTITA = null;
                                    updateHeader();
                                    mToolbar.setTitle("NoWaste");
                                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, new HomeFragment()).commit();
                                    navigationView.setCheckedItem(R.id.nav_home);
                                    fragment = 1;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("LOG", "Error! " + e.getLocalizedMessage());
                                    showSnackbar("Account non eliminato correttamente.");
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LOG", "Error! " + e.getLocalizedMessage());
                        showSnackbar("Account non eliminato correttamente!");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LOG", "Error! " + e.getLocalizedMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    showSnackbar("La password inserita non è corretta.");
                } else {
                    showSnackbar(e.getLocalizedMessage());
                }
            }
        });
    }

    public void updateHeader(){
        if (CURRENT_USER != null) {
            mFullName.setText(CURRENT_USER.getFullName());
            mEmail.setText(CURRENT_USER.getEmail());
            mFullName.setVisibility(View.VISIBLE);
            if (!CURRENT_USER.getImage().equals("")) {
                Glide.with(getApplicationContext()).load(CURRENT_USER.getImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
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

    private void updateCurrentUser(){
        // imposta CURRENT_USER
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        if (fUser != null) {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("users").document(fUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    CURRENT_USER = documentSnapshot.toObject(Utente.class);
                    updateHeader();
                    retrieveUserSavings();

                    // retrieve QUANTITA
                    List<Integer> quantita = (List<Integer>) documentSnapshot.get("quantita");
                    QUANTITA = Ints.toArray(quantita);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("LOG", "Error! " + e.getLocalizedMessage());
                }
            });
        } else {
            updateHeader();
        }
    }

    public void retrieveUserSavings() {
        // inizializzazione liste
        CARBON_DIOXIDE_ARRAY_LIST = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            CARBON_DIOXIDE_ARRAY_LIST.add(i, new ArrayList<Saving>());
        }

        OIL_ARRAY_LIST = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            OIL_ARRAY_LIST.add(i, new ArrayList<Saving>());
        }

        ENERGY_ARRAY_LIST = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ENERGY_ARRAY_LIST.add(i, new ArrayList<Saving>());
        }

        OTHER_ARRAY_LIST = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            OTHER_ARRAY_LIST.add(i, new ArrayList<Saving>());
        }

        String[] typeList = new String[] {"carbon_dioxide", "oil", "energy", "water", "fertilizer", "sand"};

        for (String type : typeList) {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                    .collection(type).orderBy("year", Query.Direction.ASCENDING)
                    .orderBy("month", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                    switch (type) {
                        case "carbon_dioxide":
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    Saving item = document.toObject(Saving.class);
                                    CARBON_DIOXIDE_ARRAY_LIST.get(item.getNtipo()).add(item);
                                }
                            }

                            break;
                        case "oil":
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    Saving item = document.toObject(Saving.class);
                                    OIL_ARRAY_LIST.get(item.getNtipo()).add(item);
                                }
                            }
                            break;
                        case "energy":
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    Saving item = document.toObject(Saving.class);
                                    ENERGY_ARRAY_LIST.get(item.getNtipo()).add(item);
                                }
                            }
                            break;
                        default:
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    Saving item = document.toObject(Saving.class);
                                    OTHER_ARRAY_LIST.get(item.getNtipo()).add(item);
                                }
                            }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("LOG", "Error! " + e.getLocalizedMessage());
                    CARBON_DIOXIDE_ARRAY_LIST = null;
                    OIL_ARRAY_LIST = null;
                    ENERGY_ARRAY_LIST = null;
                    OTHER_ARRAY_LIST = null;
                }
            });
        }
    }

    public void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(drawerLayout, string, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.inverted_primary_text));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}