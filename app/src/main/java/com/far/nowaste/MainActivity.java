package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // toolbar
    Toolbar mToolbar;

    // navigationView
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //definizione variabili
    SearchView wasteSearchView;
    MaterialCardView seccoCardView;
    MaterialCardView plasticaCardView;
    MaterialCardView cartaCardView;
    MaterialCardView organicoCardView;
    MaterialCardView vetroCardView;
    MaterialCardView metalliCardView;
    MaterialCardView elettriciCardView;
    MaterialCardView specialiCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // navigationView
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.nav_view);

        // collega le view
        wasteSearchView = (SearchView)findViewById(R.id.wasteSearchView);
        seccoCardView = (MaterialCardView) findViewById(R.id.seccoCardView);
        plasticaCardView = (MaterialCardView)findViewById(R.id.plasticaCardView);
        cartaCardView = (MaterialCardView)findViewById(R.id.cartaCardView);
        organicoCardView = (MaterialCardView)findViewById(R.id.organicoCardView);
        vetroCardView = (MaterialCardView)findViewById(R.id.vetroCardView);
        metalliCardView = (MaterialCardView)findViewById(R.id.metalliCardView);
        elettriciCardView = (MaterialCardView)findViewById(R.id.elettriciCardView);
        specialiCardView = (MaterialCardView)findViewById(R.id.specialiCardView);

        // navigationView
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.openNavDrawer, R.string.closeNavDrawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // definizione onClick searchView
        wasteSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", query);
                startActivity(cardDetailActivity);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // definizione onClick secco
        seccoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "SECCO");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick plastica
        plasticaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "PLASTICA");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick carta
        cartaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "CARTA");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick organico
        organicoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "ORGANICO");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick vetro
        vetroCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "VETRO");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick metalli
        metalliCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "METALLI");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick elettrici
        elettriciCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "ELETTRICI");
                startActivity(cardDetailActivity);
            }
        });
        // definizione onClick speciali
        specialiCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                cardDetailActivity.putExtra("com.far.nowaste.CARD_TYPE", "SPECIALI");
                startActivity(cardDetailActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}