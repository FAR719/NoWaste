package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.far.nowaste.objects.Message;
import com.far.nowaste.objects.Tickets;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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

    RelativeLayout layout;
    Typeface nunito;

    // firebase
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket);

        // toolbar
        mToolbar = findViewById(R.id.nuovoTicket_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
        layout = findViewById(R.id.newReport_layout);

        // collegamento view
        mOggetto = findViewById(R.id.oggettoTicket_EditText);
        mTesto = findViewById(R.id.textTicket_EditText);
        mSendBtn = findViewById(R.id.sendTicketButton);

        fAuth = FirebaseAuth.getInstance();

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oggetto = mOggetto.getText().toString();
                String testo = mTesto.getText().toString();

                // controlla la info aggiunte
                if (TextUtils.isEmpty(oggetto)) {
                    mOggetto.setError("Inserisci oggetto.");
                    return;
                }

                if (oggetto.length() > 20) {
                    mOggetto.setError("L'oggetto non può superare 20 caratteri!");
                    return;
                }

                if (TextUtils.isEmpty(testo)) {
                    mTesto.setError("Inserisci testo.");
                    return;
                }

                // inserisce il ticket in firebase
                insertNewTicket(oggetto, testo);
            }
        });
    }


    // insert method
    private void insertNewTicket(String oggetto, String testo) {
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
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("tickets").document(ticketID);
        Tickets ticket = new Tickets(oggetto, email, day, month, year, hour, minute, second,true);

        documentReference.set(ticket).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                createMessage(testo, ticket,ticketID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
            }
        });
    }


    // chat method
    private void createMessage(String testo,Tickets ticket,String ticketID) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("tickets").document(ticketID).
                collection("messages").document();

        Message message = new Message(testo, ticket.getDay(), ticket.getMonth(), ticket.getYear(), ticket.getHour(),
                ticket.getMinute(), ticket.getSecond(),false);

        documentReference.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("com.far.nowaste.NEW_TICKET_REQUEST", true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar("Ticket non inviato!");
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
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

    public void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.inverted_primary_text));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}