package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class ReportProblemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Toolbar mToolbar;
    Spinner mSpinnerProb;
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

        // spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.themeListReportProblems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerProb.setAdapter(adapter);
        mSpinnerProb.setOnItemSelectedListener(this);


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
        Toast.makeText(parent.getContext(),probChoose,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}