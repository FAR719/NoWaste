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

import com.far.nowaste.objects.Bug;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Date;

public class NewBugActivity extends AppCompatActivity {
    // variabili
    Toolbar mToolbar;
    EditText mOggetto, mTesto;
    Button mSendBugBtn;

    RelativeLayout layout;
    Typeface nunito;

    // firebase
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bug);

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
        layout = findViewById(R.id.newBug_layout);

        // toolbar
        mToolbar = findViewById(R.id.newBug_toolbar);
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

        // caricamento su firebase
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("bugs").document();
        Bug bug = new Bug(oggetto,testo,email,day,month,year,hour,minute,second);

        documentReference.set(bug).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("com.far.nowaste.NEW_BUG_REQUEST", true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
                showSnackbar("Segnalazione non inviata correttamente!");
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

    private void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}