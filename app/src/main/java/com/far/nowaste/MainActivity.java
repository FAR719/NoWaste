package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // toolbar
    MenuItem mSearchItem;
    private Toolbar mToolbar;

    // navigationView
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    View header;
    TextView mFullName, mEmail;
    ImageView mImage;

    // firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    // home 1, profilo 2, curiosità 3, calendario 4, luoghi 5, contattaci 6, impostazioni 7
    int fragment;

    // variabili mappa
    FrameLayout mainFrameLayout;
    FrameLayout mapFrameLayout;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    FloatingActionButton gpsBtn;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

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

        // frame
        mainFrameLayout = findViewById(R.id.main_frame_layout);
        mapFrameLayout = findViewById(R.id.map_frame_layout);

        // firebase auth
        fAuth = FirebaseAuth.getInstance();

        // gpsBtn
        gpsBtn = findViewById(R.id.gpsButton);
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });


        // header onclick
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } else {
                    mToolbar.setTitle("Profilo");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new DetailUserFragment()).commit();
                    if (fragment == 5) {
                        mainFrameLayout.setVisibility(View.VISIBLE);
                        mapFrameLayout.setVisibility(View.GONE);
                        client = null;
                    }
                    fragment = 2;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            fragment = 1;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // cambia i dati nell'header
        if (fAuth.getCurrentUser() != null){
            fStore = FirebaseFirestore.getInstance();
            fAuth = FirebaseAuth.getInstance();
            FirebaseUser user = fAuth.getCurrentUser();
            fStore.collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    String nome = value.getString("fullName");
                    String email = value.getString("email");
                    String image = value.getString("image");

                    mFullName.setText(nome);
                    mEmail.setText(email);
                    mFullName.setVisibility(View.VISIBLE);
                    if (image != null) {
                        Glide.with(getApplicationContext()).load(image).apply(RequestOptions.circleCropTransform()).into(mImage);
                    }
                    fAuth.getCurrentUser().getPhotoUrl();
                }
            });
            fStore.terminate();
        } else {
            mEmail.setText("Accedi al tuo account");
            mFullName.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFragmentRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        mSearchItem = menu.findItem(R.id.m_search);

        // crea le animazioni
        new SearchToolbarAnimation(getWindow(), mToolbar, mSearchItem, getApplicationContext(), getResources()).setAnimation();

        //set queryListener searchView
        SearchView wasteSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
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
        switch (item.getItemId()){
            case R.id.nav_home:
                if (fragment != 1) {
                    mToolbar.setTitle("NoWaste");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new HomeFragment()).commit();
                    if (fragment == 5) {
                        mainFrameLayout.setVisibility(View.VISIBLE);
                        mapFrameLayout.setVisibility(View.GONE);
                        client = null;
                    }
                    fragment = 1;
                }
                break;
            case R.id.nav_curiosita:
                if (fragment != 3) {
                    mToolbar.setTitle("Curiosità");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new CuriositaFragment()).commit();
                    if (fragment == 5) {
                        mainFrameLayout.setVisibility(View.VISIBLE);
                        mapFrameLayout.setVisibility(View.GONE);
                        supportMapFragment = null;
                        client = null;
                    }
                    fragment = 3;
                }
                break;
            case R.id.nav_calendario:
                if (fragment != 4) {
                    mToolbar.setTitle("Calendario");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new CalendarioFragment()).commit();
                    if (fragment == 5) {
                        mainFrameLayout.setVisibility(View.VISIBLE);
                        mapFrameLayout.setVisibility(View.GONE);
                        supportMapFragment = null;
                        client = null;
                    }
                    fragment = 4;
                }
                break;
            case R.id.nav_luoghi:
                if (fragment != 5) {
                    mToolbar.setTitle("Luoghi");
                    mainFrameLayout.setVisibility(View.GONE);
                    mapFrameLayout.setVisibility(View.VISIBLE);

                    // assegnazione variabile
                    supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

                    // inizializzazione FusedLocation
                    client = LocationServices.getFusedLocationProviderClient(this);

                    // Check permessi
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // permesso concesso
                        getCurrentLocation();
                    } else {
                        // permesso negato
                        // RICHIESTA PERMESSO
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                    }
                    fragment = 5;
                }
                break;
            case R.id.nav_contattaci:
                if (fragment != 6) {
                    mToolbar.setTitle("Contattaci");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new ContattaciFragment()).commit();
                    if (fragment == 5) {
                        mainFrameLayout.setVisibility(View.VISIBLE);
                        mapFrameLayout.setVisibility(View.GONE);
                        supportMapFragment = null;
                        client = null;
                    }
                    fragment = 6;
                }
                break;
            case R.id.nav_impostazioni:
                if (fragment != 7) {
                    mToolbar.setTitle("Impostazioni");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new ImpostazioniFragment()).commit();
                    if (fragment == 5) {
                        mainFrameLayout.setVisibility(View.VISIBLE);
                        mapFrameLayout.setVisibility(View.GONE);
                        supportMapFragment = null;
                        client = null;
                    }
                    fragment = 7;
                }
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // chiude la navigation quando premi back
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if(fragment != 1){
            mToolbar.setTitle("NoWaste");
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            if (fragment == 5) {
                mainFrameLayout.setVisibility(View.VISIBLE);
                mapFrameLayout.setVisibility(View.GONE);
                supportMapFragment = null;
                client = null;
            }
            fragment = 1;
        } else {
            super.onBackPressed();
        }
    }


    // metodi Maps
    private void getCurrentLocation() {
        // permessi per usare getLastLocation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Inizializzazione task Location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Se ha successo

                if(location != null){
                    // Sincronizza Mappa
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // inizializzazione Latitudine e Longitudine
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                            // marker per segnalare la posizione attuale
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Sei qui!");

                            // Zoom Mappa
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

                            // aggiungere il marker sulla mappa
                            googleMap.addMarker(markerOptions);
                            
                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    gpsBtn.animate().translationY(-140f).setInterpolator(interpolator).setDuration(400).start();
                                    return false;
                                }
                            });

                            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    gpsBtn.animate().translationY(0f).setInterpolator(interpolator).setDuration(400).start();
                                }
                            });
                        }
                    });
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Quando Permesso concesso
                getCurrentLocation();
            }
        }
    }

    private void checkFragmentRequest(){
        // num: se 1 home e logout, se 2 profilo, se 3 home e deleteAccount
        if (LoginActivity.NUM == 1) {
            LoginActivity.NUM = 0;
            mToolbar.setTitle("NoWaste");
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new HomeFragment()).commit();
            if (fragment == 5) {
                mainFrameLayout.setVisibility(View.VISIBLE);
                mapFrameLayout.setVisibility(View.GONE);
                client = null;
            }
            navigationView.setCheckedItem(R.id.nav_home);
            fragment = 1;
            logout();
        } else if(LoginActivity.NUM == 2) {
            LoginActivity.NUM = 0;
            mToolbar.setTitle("Profilo");
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new DetailUserFragment()).commit();
            if (fragment == 5) {
                mainFrameLayout.setVisibility(View.VISIBLE);
                mapFrameLayout.setVisibility(View.GONE);
                client = null;
            }
            fragment = 2;
        } else if (LoginActivity.NUM == 3) {
            LoginActivity.NUM = 0;
            mToolbar.setTitle("NoWaste");getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new HomeFragment()).commit();
            if (fragment == 5) {
                mainFrameLayout.setVisibility(View.VISIBLE);
                mapFrameLayout.setVisibility(View.GONE);
                client = null;
            }
            navigationView.setCheckedItem(R.id.nav_home);
            fragment = 1;
            deleteAccount();
        }
    }

    // logout from settings
    public void logout() {
        fStore.terminate();
        fAuth.signOut();
        Toast.makeText(MainActivity.this, "Logout effettuato.", Toast.LENGTH_SHORT).show();
        mEmail.setText("Accedi al tuo account");
        mFullName.setVisibility(View.GONE);
        Drawable defaultImage = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_user);
        mImage.setImageDrawable(defaultImage);
        mToolbar.setTitle("NoWaste");
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new HomeFragment()).commit();
        fragment = 1;
    }

    // delete account from settings
    public void deleteAccount() {
        FirebaseUser user = fAuth.getCurrentUser();

        // re-authenticate the user
        AuthCredential credential;
        credential = EmailAuthProvider.getCredential(user.getEmail(), ImpostazioniFragment.PASSWORD);
        ImpostazioniFragment.PASSWORD = null;

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "User re-authenticated.");
            }
        });

        // cancella l'utente da firestore
        fStore.collection("users").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error deleting document", e);
            }
        });
        fStore.terminate();

        // cancella l'utente da fireauth
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Account eliminato", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}