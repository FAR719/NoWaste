package com.far.nowaste;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //definizione variabili
    CardView seccoCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //collega le view
        seccoCardView = (CardView)findViewById(R.id.seccoCardView);

        //definizione onClick
        seccoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardDetailActivity = new Intent(getApplicationContext(), CardDetailActivity.class);
                startActivity(cardDetailActivity);
            }
        });
    }
}