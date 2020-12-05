package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // toolbar
    private MenuItem mSearchItem;
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
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                } else {
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

        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Called when SearchView is collapsing
                if (mSearchItem.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Called when SearchView is expanding
                animateSearchToolbar(1, true, true);
                return true;
            }
        });

        //set queryListener searchView
        androidx.appcompat.widget.SearchView wasteSearchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(mSearchItem);
        wasteSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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

    // animazioni searchView
    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.search_background));
        drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.quantum_grey_600));

        if (show) {
            int width = mToolbar.getWidth() - (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) - ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
            Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar, isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, 0.0f, (float) width);
            createCircularReveal.setDuration(250);
            createCircularReveal.start();
        } else {
            int width = mToolbar.getWidth() - (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) - ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
            Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar, isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, (float) width, 0.0f);
            createCircularReveal.setDuration(250);
            createCircularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mToolbar.setBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimary));
                    drawerLayout.setStatusBarBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimaryDark));
                }
            });
            createCircularReveal.start();
            drawerLayout.setStatusBarBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimaryDark));
        }
    }

    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    // metodo per restituire il colore selezionato del tema
    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    // onclick sulla navigation
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                if (!homeFragment) {
                    currentFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                    homeFragment = true;
                }
                break;
            case R.id.nav_curiosita:
                currentFragment = new CuriositaFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                homeFragment = false;
                break;
            case R.id.nav_calendario:
                currentFragment = new CalendarioFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                homeFragment = false;
                break;
            case R.id.nav_luoghi:
                currentFragment = new LuoghiFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                homeFragment = false;
                break;
            case R.id.nav_contattaci:
                currentFragment = new ContattaciFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                homeFragment = false;
                break;
            case R.id.nav_impostazioni:
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
        mEmail.setText("Accedi al tuo account");
        mFullName.setVisibility(View.GONE);
        currentFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
        homeFragment = true;
        fAuth.signOut();
        Toast.makeText(MainActivity.this, "Logout effettuato.", Toast.LENGTH_SHORT).show();
    }

    // chiude la navigation quando premi back
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if(!homeFragment && impostazioniFragment != null){
            getFragmentManager().beginTransaction().remove(impostazioniFragment).commit();
            currentFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            impostazioniFragment = null;
            homeFragment = true;
        } else if(!homeFragment && impostazioniFragment == null){
            currentFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            homeFragment = true;
        }else {
            super.onBackPressed();
        }
    }
}