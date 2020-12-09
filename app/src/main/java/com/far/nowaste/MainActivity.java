package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    // firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    // true se home, false altrimenti
    boolean homeFragment;

    // definizione Fragments
    Fragment currentFragment;
    ImpostazioniFragment impostazioniFragment;

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

        fAuth = FirebaseAuth.getInstance();

        // header onclick
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } else {
                    mToolbar.setTitle("Profilo");
                    currentFragment = new DetailUserFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                    homeFragment = false;
                    if (currentFragment != null && impostazioniFragment != null){
                        getFragmentManager().beginTransaction().remove(impostazioniFragment).commit();
                        impostazioniFragment = null;
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        if (savedInstanceState == null) {
            currentFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            homeFragment = true;
        }
    }

    // onStart cambia i dati nell'header
    @Override
    protected void onStart() {
        super.onStart();

        // modifica username
        if (fAuth.getCurrentUser() != null){
            // query
            fStore = FirebaseFirestore.getInstance();
            FirebaseUser user = fAuth.getCurrentUser();
            fStore.collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    String nome = value.getString("fullName");
                    String email = value.getString("email");

                    mFullName.setText(nome);
                    mEmail.setText(email);
                    mFullName.setVisibility(View.VISIBLE);
                }
            });
            fStore.terminate();
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
        new SearchToolbarAnimation(mToolbar, mSearchItem,this, getResources()).setAnimation();

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
                if (!homeFragment) {
                    mToolbar.setTitle("NoWaste");
                    currentFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                    homeFragment = true;
                }
                break;
            case R.id.nav_curiosita:
                mToolbar.setTitle("Curiosità");
                currentFragment = new CuriositaFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                homeFragment = false;
                break;
            case R.id.nav_calendario:
                mToolbar.setTitle("Calendario");
                currentFragment = new CalendarioFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                homeFragment = false;
                break;
            case R.id.nav_luoghi:
                mToolbar.setTitle("Luoghi");
                currentFragment = new LuoghiFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                homeFragment = false;
                break;
            case R.id.nav_contattaci:
                mToolbar.setTitle("Contattaci");
                currentFragment = new ContattaciFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                homeFragment = false;
                break;
            case R.id.nav_impostazioni:
                mToolbar.setTitle("Impostazioni");
                impostazioniFragment = new ImpostazioniFragment();
                getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, impostazioniFragment).commit();
                currentFragment = null;
                homeFragment = false;
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        if (currentFragment != null && impostazioniFragment != null){
            getFragmentManager().beginTransaction().remove(impostazioniFragment).commit();
            impostazioniFragment = null;
        }
        return true;
    }

    // onclick logout button
    public void logout(View view) {
        fAuth.signOut();
        Toast.makeText(MainActivity.this, "Logout effettuato.", Toast.LENGTH_SHORT).show();
        mEmail.setText("Accedi al tuo account");
        mFullName.setVisibility(View.GONE);
        mToolbar.setTitle("NoWaste");
        currentFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
        homeFragment = true;
    }

    // chiude la navigation quando premi back
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if(!homeFragment && impostazioniFragment != null){
            mToolbar.setTitle("NoWaste");
            getFragmentManager().beginTransaction().remove(impostazioniFragment).commit();
            currentFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            impostazioniFragment = null;
            homeFragment = true;
        } else if(!homeFragment && impostazioniFragment == null){
            mToolbar.setTitle("NoWaste");
            currentFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            homeFragment = true;
        }else {
            super.onBackPressed();
        }
    }
}