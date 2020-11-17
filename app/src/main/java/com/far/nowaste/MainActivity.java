package com.far.nowaste;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity {

    // toolbar
    private Toolbar mToolbar;

    //definizione variabili
    SearchView wasteSearchView;
    CardView seccoCardView;
    CardView plasticaCardView;
    CardView cartaCardView;
    CardView organicoCardView;
    CardView vetroCardView;
    CardView metalliCardView;
    CardView elettriciCardView;
    CardView specialiCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // collega le view
        wasteSearchView = (SearchView)findViewById(R.id.wasteSearchView);
        seccoCardView = (CardView)findViewById(R.id.seccoCardView);
        plasticaCardView = (CardView)findViewById(R.id.plasticaCardView);
        cartaCardView = (CardView)findViewById(R.id.cartaCardView);
        organicoCardView = (CardView)findViewById(R.id.organicoCardView);
        vetroCardView = (CardView)findViewById(R.id.vetroCardView);
        metalliCardView = (CardView)findViewById(R.id.metalliCardView);
        elettriciCardView = (CardView)findViewById(R.id.elettriciCardView);
        specialiCardView = (CardView)findViewById(R.id.specialiCardView);

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
}