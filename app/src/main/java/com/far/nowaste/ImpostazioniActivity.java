package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class ImpostazioniActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    // toolbar
    private MenuItem mSearchItem;
    private Toolbar mToolbar;

    // navigationView
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        // toolbar
        mToolbar = findViewById(R.id.impostazioni_toolbar);
        setSupportActionBar(mToolbar);

        // navigationView
        drawerLayout = findViewById(R.id.impostazioni_drawerlayout);
        navigationView = findViewById(R.id.impostazioni_navView);

        // navigationView
        navigationView.bringToFront();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.openNavDrawer, R.string.closeNavDrawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Load setting fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MainSettingsFragment()).commit();
    }


    // onclick sulla navigation
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                finish();
                break;
            case R.id.nav_curiosita:
                startActivity(new Intent(getApplicationContext(), CuriositaActivity.class));
                finish();
                break;
            case R.id.nav_calendario:
                startActivity(new Intent(getApplicationContext(), CalendarioActivity.class));
                finish();
                break;
            case R.id.nav_luoghi:
                startActivity(new Intent(getApplicationContext(), LuoghiActivity.class));
                finish();
                break;
            case R.id.nav_contattaci:
                startActivity(new Intent(getApplicationContext(), ContattaciActivity.class));
                finish();
                break;
            case R.id.nav_impostazioni:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;


    }

    //xml settings
    public static class MainSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}