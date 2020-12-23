package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.far.nowaste.objects.Bug;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Date;

public class AssistenzaActivity extends AppCompatActivity {
    // variabili
    Toolbar mToolbar;
    EditText mOggetto, mTesto;
    Button mSendBugBtn;

    // firebase
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistenza);

        // toolbar
        mToolbar = findViewById(R.id.assistenza_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // collegamento view
        mOggetto = findViewById(R.id.oggettoBug_EditText);
        mTesto = findViewById(R.id.textBug_EditText);
        mSendBugBtn = findViewById(R.id.sendBugButton);

        fAuth = FirebaseAuth.getInstance();

        mSendBugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oggetto = mOggetto.getText().toString();
                String testo = mTesto.getText().toString();

                // controlla la info aggiunte
                if (TextUtils.isEmpty(oggetto)) {
                    mOggetto.setError("Inserisci oggetto.");
                    return;
                }

                if (TextUtils.isEmpty(testo)) {
                    mTesto.setError("Inserisci testo.");
                    return;
                }

                // inserisce il ticket in firebase
                insertNewBug(oggetto, testo);
                Toast.makeText(getApplicationContext(), "Bug segnalato!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void insertNewBug(String oggetto, String testo) {
        // variabili
        Date date = new Date();
        int hour = date.getHours();
        int minute= date.getMinutes();
        int second = date.getSeconds();

        CalendarDay currentDate = CalendarDay.today();
        int day = currentDate.getDay();
        int month = currentDate.getMonth();
        int year = currentDate.getYear();

        String email = fAuth.getCurrentUser().getEmail();

        // caricamneto su firebase
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("bugs").document();
        Bug bug = new Bug(oggetto,testo,email,day,month,year,hour,minute,second);

        documentReference.set(bug).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "onSuccess: bug sent");
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