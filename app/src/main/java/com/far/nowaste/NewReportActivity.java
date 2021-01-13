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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.far.nowaste.objects.Report;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class NewReportActivity extends AppCompatActivity {

    // variabili
    Toolbar mToolbar;
    RadioButton mVetroRdb, mCartaRdb, mIndifferenziataRdb, mPlasticaRdb, mIndumentiRdb, mAltroRdb;
    EditText mIndirizzo, mCommento;
    Button mSendBtn;

    RelativeLayout layout;
    Typeface nunito;

    // firebase
    FirebaseAuth fAuth;

    TextInputLayout typoInputLayout;
    MaterialAutoCompleteTextView typoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        // toolbar
        mToolbar = findViewById(R.id.report_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
        layout = findViewById(R.id.newTicket_layout);

        // collegamneti view
        typoInputLayout = findViewById(R.id.typoInputLayout);
        typoTextView = findViewById(R.id.typoTextView);
        mVetroRdb = findViewById(R.id.rdbVetro);
        mCartaRdb = findViewById(R.id.rdbCarta);
        mIndifferenziataRdb = findViewById(R.id.rdbIndifferenziata);
        mPlasticaRdb = findViewById(R.id.rdbPlastica);
        mIndumentiRdb = findViewById(R.id.rdbIndumenti);
        mAltroRdb = findViewById(R.id.rdbAltro);
        mIndirizzo = findViewById(R.id.indirizzoEditText);
        mCommento = findViewById(R.id.commentoEditText);
        mSendBtn = findViewById(R.id.sendButton);

        fAuth = FirebaseAuth.getInstance();

        // spinner
        ArrayList<String> values = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.listReports)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_spinner_dropdown, values);

        typoTextView.setAdapter(adapter);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tipologia = typoTextView.getText().toString();
                String cassonetto = (mVetroRdb.isChecked())?"Vetro":(mCartaRdb.isChecked())?"Carta":(mIndifferenziataRdb.isChecked())?"Indifferenziata":
                        (mPlasticaRdb.isChecked())?"Plastica":(mIndumentiRdb.isChecked())?"Indumenti":(mAltroRdb.isChecked())?"Altro":"";
                String indirizzo = mIndirizzo.getText().toString();
                String commento = mCommento.getText().toString();
                // controlla la info aggiunte
                if (TextUtils.isEmpty(tipologia)) {
                    typoInputLayout.setError("Selezionare una tipologia.");
                } else {
                    typoInputLayout.setErrorEnabled(false);
                }

                if(TextUtils.isEmpty(cassonetto)) {
                    mIndirizzo.setError("Selezionare un cassonetto.");
                    return;
                } else if(TextUtils.isEmpty(indirizzo)){
                    mIndirizzo.setError("Inserisci l'indirizzo.");
                    return;
                }

                if(cassonetto.equals("Altro") && TextUtils.isEmpty(commento)){
                    mCommento.setError("Specificare la tipologia del cassonetto.");
                    return;
                }

                // inserisce il ticket in firebase
                loadReport(tipologia,cassonetto,indirizzo,commento);
            }
        });


    }

    private void loadReport(String problemaScelto, String cassonetto, String indirizzo, String commento) {
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
        DocumentReference documentReference = fStore.collection("reports").document();
        Report report= new Report(problemaScelto,cassonetto,indirizzo,commento,email,day,month,year,hour,minute,second);

        documentReference.set(report).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("com.far.nowaste.NEW_REPORT_REQUEST", true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
                showSnackbar("Report non inviato!");
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
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}