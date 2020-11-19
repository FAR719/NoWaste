package com.far.nowaste;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class CardDetailActivity extends AppCompatActivity {

    // toolbar
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // definizione variabili
        TextView detailsTextView = (TextView) findViewById(R.id.detailsTextView);

        // back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // to launch the activity
        Intent in = getIntent();

        // display the name
        String wasteType = in.getStringExtra("com.far.nowaste.CARD_TYPE");
        detailsTextView.setText(wasteType);
    }
}