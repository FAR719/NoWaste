package com.far.nowaste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CardDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        // definizione variabili
        TextView detailsTextView = (TextView) findViewById(R.id.detailsTextView);

        // to launch the activity
        Intent in = getIntent();

        // display the name
        String wasteType = in.getStringExtra("com.far.nowaste.CARD_TYPE");
        detailsTextView.setText(wasteType);
    }
}