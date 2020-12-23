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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.far.nowaste.objects.Report;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Date;

public class ReportProblemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // variabili
    Toolbar mToolbar;
    Spinner mSpinnerProb;
    RadioButton mVetroRdb, mCartaRdb, mIndifferenziataRdb, mPlasticaRdb, mIndumentiRdb, mAltroRdb;
    EditText mIndirizzo, mCommento;
    Button mProbBtn;

    // firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        Intent intent = getIntent();

        // toolbar
        mToolbar = findViewById(R.id.reportProblem_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // collegamneti view
        mSpinnerProb = findViewById(R.id.spinnerProblema);
        mVetroRdb = findViewById(R.id.rdbVetro);
        mCartaRdb = findViewById(R.id.rdbCarta);
        mIndifferenziataRdb = findViewById(R.id.rdbIndifferenziata);
        mPlasticaRdb = findViewById(R.id.rdbPlastica);
        mIndumentiRdb = findViewById(R.id.rdbIndumenti);
        mAltroRdb = findViewById(R.id.rdbAltro);
        mIndirizzo = findViewById(R.id.indirizzoEditText);
        mCommento = findViewById(R.id.commentoEditText);
        mProbBtn = findViewById(R.id.sendProblemButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.themeListReportProblems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerProb.setAdapter(adapter);
        mSpinnerProb.setOnItemSelectedListener(this);

        mProbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String problemaScelto = mSpinnerProb.getSelectedItem().toString();
                String cassonetto = (mVetroRdb.isChecked())?"Vetro":(mCartaRdb.isChecked())?"Carta":(mIndifferenziataRdb.isChecked())?"Indifferenziata":
                        (mPlasticaRdb.isChecked())?"Plastica":(mIndumentiRdb.isChecked())?"Indumenti":(mAltroRdb.isChecked())?"Altro":"";
                String indirizzo = mIndirizzo.getText().toString();
                String commento = mCommento.getText().toString();
                // controlla la info aggiunte
                if(cassonetto.equals("")){
                    mIndirizzo.setError("Selezionare un cassonetto");
                    return;
                } else if(TextUtils.isEmpty(indirizzo)){
                    mIndirizzo.setError("Inserisci indirizzo");
                    return;
                }
                if(cassonetto == "Altro" && TextUtils.isEmpty(commento)){
                    mCommento.setError("Specificare la tipologia del cassonetto");
                    return;
                }

                // inserisce il ticket in firebase
                insertReportProblem(problemaScelto,cassonetto,indirizzo,commento);
                finish();

            }
        });


    }

    private void insertReportProblem(String problemaScelto, String cassonetto, String indirizzo, String commento) {
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
        DocumentReference documentReference = fStore.collection("reports").document();
        Report report= new Report(problemaScelto,cassonetto,indirizzo,commento,email,day,month,year,hour,minute,second);

        documentReference.set(report).addOnSuccessListener(new OnSuccessListener<Void>() {
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


    // Metodi per OnItemSelectedListener
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String probChoose = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}