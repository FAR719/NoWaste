package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class ReportProblemActivity extends AppCompatActivity {

    Toolbar mToolbar;
    Spinner mProblema;
    RadioButton mVetroRdb, mCartaRdb, mIndifferenziataRdb, mPlasticaRdb, mIndumentiRdb, mAltroRdb;
    EditText mIndirizzo, mCommento;
    Button mProbBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        Intent intent = getIntent();

        // toolbar
        mToolbar = findViewById(R.id.reportProblem_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // collegamneti view
        mProblema = findViewById(R.id.spinnerProblema);
        mVetroRdb = findViewById(R.id.rdbVetro);
        mCartaRdb = findViewById(R.id.rdbCarta);
        mIndifferenziataRdb = findViewById(R.id.rdbIndifferenziata);
        mPlasticaRdb = findViewById(R.id.rdbPlastica);
        mIndumentiRdb = findViewById(R.id.rdbIndumenti);
        mAltroRdb = findViewById(R.id.rdbAltro);
        mIndirizzo = findViewById(R.id.indirizzoEditText);
        mCommento = findViewById(R.id.commentoEditText);
        mProbBtn = findViewById(R.id.sendProblemButton);


        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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