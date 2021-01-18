package com.far.nowaste;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.far.nowaste.objects.Saving;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;

public class CategoriaActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;

    ValueLineChart mLineChart;
    Button mBtn;
    String categoria;
    int nCategoria;

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

        mLineChart = findViewById(R.id.categoria_lineChart);
        mBtn = findViewById(R.id.categoria_button);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listaCardActivity = new Intent(getApplicationContext(), ListaCardActivity.class);
                listaCardActivity.putExtra("com.far.nowaste.CARD_TYPE", categoria);
                startActivity(listaCardActivity);
            }
        });

        setLineChartData();
    }

    private void  setLineChartData() {
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        for (Saving item : MainActivity.CARBON_DIOXIDE_ARRAY_LIST.get(nCategoria)) {
            series.addPoint(new ValueLinePoint(item.getMonth() + "", (float)item.getPunteggio()));
        }

        mLineChart.addSeries(series);
        mLineChart.startAnimation();
    }
}