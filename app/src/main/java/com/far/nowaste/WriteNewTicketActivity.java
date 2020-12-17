package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class WriteNewTicketActivity extends AppCompatActivity {

    // variabili
    Toolbar mToolbar;
    EditText mOggetto, mTesto;
    Button mSendBtn;

    // firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_new_ticket);

        Intent intent = getIntent();


        // toolbar
        mToolbar = findViewById(R.id.nuovoTicket_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // collegamento view
        mOggetto = findViewById(R.id.oggettoEditText);
        mTesto = findViewById(R.id.textTicketEditText);
        mSendBtn = findViewById(R.id.sendButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oggetto = mOggetto.getText().toString();
                String testo = mTesto.getText().toString();

                Date date = new Date();
                int hour = date.getHours();
                int minutes= date.getMinutes();
                int seconds = date.getSeconds();



                // controlla la info aggiunte
                if (TextUtils.isEmpty(oggetto)){
                    mOggetto.setError("Inserisci oggetto.");
                    return;
                }

                if (TextUtils.isEmpty(testo)){
                    mTesto.setError("Inserisci testo.");
                    return;
                }

                // inserisce il ticket in firebase
                insertNewTicket(oggetto,testo);



            }
        });
    }

    // insert method
    private void insertNewTicket(String oggetto, String testo) {
        String email = fAuth.getCurrentUser().getEmail();


    }

    // ends this activity (back arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}