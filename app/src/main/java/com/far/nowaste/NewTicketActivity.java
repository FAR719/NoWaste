package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.far.nowaste.Objects.Message;
import com.far.nowaste.Objects.Tickets;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Date;

public class NewTicketActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_new_ticket);

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
                insertNewTicket(oggetto, testo);
                finish();

            }
        });
    }


    // insert method
    private void insertNewTicket(String oggetto, String testo) {
        // variabili
        boolean stato = true;

        Date date = new Date();
        int hour = date.getHours();
        int minute= date.getMinutes();
        int second = date.getSeconds();

        CalendarDay currentDate = CalendarDay.today();
        int day = currentDate.getDay();
        int month = currentDate.getMonth();
        int year = currentDate.getYear();

        String email = fAuth.getCurrentUser().getEmail();

        //Formatto l’ora
        String ora_corr= hour + ":" + minute+ ":" + second;

        // ID DOCUMENTO
        String ticketID = email + ora_corr;

        // caricamneto su firebase
        DocumentReference documentReference = fStore.collection("tickets").document(ticketID);
        Tickets ticket = new Tickets(oggetto,email,day,month,year,hour,minute,second,stato);

        documentReference.set(ticket).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "onSuccess: ticket sent");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e.toString());
            }
        });

        createMessage(testo, ticket,ticketID);

    }


    // chat method
    private void createMessage(String testo,Tickets ticket,String ticketID) {
        // variabili
        boolean operatore = false;

        DocumentReference documentReference = fStore.collection("tickets").document(ticketID).
                collection("messages").document();

        Message message = new Message(testo,ticket.getDay(),ticket.getMonth(),ticket.getYear(),ticket.getHour(),ticket.getMinute(),ticket.getSecond(),operatore);

        documentReference.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e.toString());
            }
        });
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