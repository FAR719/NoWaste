package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.far.nowaste.objects.Curiosity;
import com.far.nowaste.objects.Saving;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CategoriaActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;

    MaterialCardView mCuriositaCard;
    ValueLineChart mLineChart;
    TextView mCuriositaTV;
    Button mBtn;

    String categoria;
    int nCategoria;

    Curiosity curiosity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        // toolbar
        mToolbar = findViewById(R.id.categoria_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // variabile passata
        Intent in = getIntent();
        categoria = in.getStringExtra("com.far.nowaste.CATEGORIA");
        nCategoria = in.getIntExtra("com.far.nowaste.NCATEGORIA", 0);

        // cambia il titolo della toolbar
        mToolbar.setTitle(categoria);

        mCuriositaCard = findViewById(R.id.categoria_curiosita_card);
        mLineChart = findViewById(R.id.categoria_lineChart);
        mCuriositaTV = findViewById(R.id.categoria_curiosita_TV);
        mBtn = findViewById(R.id.categoria_button);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listaCardActivity = new Intent(getApplicationContext(), ListaCardActivity.class);
                listaCardActivity.putExtra("com.far.nowaste.CARD_TYPE", categoria);
                startActivity(listaCardActivity);
            }
        });

        setLineChartData(MainActivity.CARBON_DIOXIDE_ARRAY_LIST);
        loadCuriosita();
    }

    private void  setLineChartData(ArrayList<ArrayList<Saving>> arrayOfArray) {
        ValueLineSeries series = new ValueLineSeries();

        // assegna il colore corrispondente
        switch (nCategoria) {
            case 0:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.plastica), 150));
                break;
            case 1:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.organico), 150));
                break;
            case 2:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.secco), 150));
                break;
            case 3:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.carta), 150));
                break;
            case 4:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.vetro), 150));
                break;
            case 5:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.metalli), 150));
                break;
            case 6:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.elettrici), 150));
                break;
            case 7:
                series.setColor(ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(), R.color.speciali), 150));
                break;
        }

        for (Saving item : arrayOfArray.get(nCategoria)) {
            String month;
            if (item.getMonth() < 10) {
                month = "0" + item.getMonth();
            } else {
                month = item.getMonth() + "";
            }
            series.addPoint(new ValueLinePoint(month + "/" + item.getYear(), (float)item.getPunteggio()));
        }

        mLineChart.addSeries(series);
        mLineChart.startAnimation();
    }

    private void loadCuriosita() {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("curiosity").whereEqualTo("etichetta", categoria).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    mCuriositaCard.setVisibility(View.VISIBLE);
                    List<Curiosity> curiosityList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Curiosity curiosity = document.toObject(Curiosity.class);
                        curiosityList.add(curiosity);
                    }
                    int curiosityCount = curiosityList.size();
                    int randomNumber= new Random().nextInt(curiosityCount);

                    curiosity = curiosityList.get(randomNumber);
                    mCuriositaTV.setText(curiosity.getDescrizione());
                } else {
                    mCuriositaCard.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LOG", "Error! " + e.getLocalizedMessage());
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