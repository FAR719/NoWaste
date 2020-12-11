package com.far.nowaste;


// Import the required libraries
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


public class GraficoTorta extends AppCompatActivity {

    //variabili
    // Create the object of TextView
    // and PieChart class

    TextView tvR, tvPython, tvCPP, tvJava, tvVetro, tvMetalli, tvElettrici, tvSpeciali;
    PieChart pieChart;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    Utente utente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grafico_layout);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        // Link those objects with their
        // respective id's that
        // we have given in .XML file
        tvR = findViewById(R.id.tvR);
        tvPython = findViewById(R.id.tvPython);
        tvCPP = findViewById(R.id.tvCPP);
        tvJava = findViewById(R.id.tvJava);
        tvVetro = findViewById(R.id.tvVetro);
        tvMetalli = findViewById(R.id.tvMetalli);
        tvElettrici = findViewById(R.id.tvElettrici);
        tvSpeciali = findViewById(R.id.tvSpeciali);
        pieChart = findViewById(R.id.piechart);

        setData();
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        //prendo i punteggi dell' utente
        fStore.collection("utente").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                utente = value.toObject(Utente.class);
            }
        });

        // Creating a method setData()
        // to set the text in text view and pie chart
        setData();
    }*/

    private void setData() {
        /*tvR.setText(((int) utente.getpPlastica()) + "");
        tvPython.setText((int) utente.getpIndifferenziata());
        tvCPP.setText((int) utente.getpOrganico());
        tvJava.setText((int) utente.getpIndifferenziata());
        tvVetro.setText((int) utente.getpVetro());
        tvMetalli.setText((int) utente.getpMetalli());
        tvElettrici.setText((int) utente.getpElettrici());
        tvSpeciali.setText((int) utente.getpSpeciali());*/

        tvR.setText("10");
        tvPython.setText("10");
        tvCPP.setText("10");
        tvJava.setText("10");
        tvVetro.setText("10");
        tvMetalli.setText("10");
        tvElettrici.setText("10");
        tvSpeciali.setText("10");

        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "Plastica",
                        Integer.parseInt(tvR.getText().toString()),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Carta",
                        Integer.parseInt(tvPython.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Organico",
                        Integer.parseInt(tvCPP.getText().toString()),
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        "Secco",
                        Integer.parseInt(tvJava.getText().toString()),
                        Color.parseColor("#29B6F6")));
        pieChart.addPieSlice(
                new PieModel(
                        "Vetro",
                        Integer.parseInt(tvVetro.getText().toString()),
                        Color.parseColor("#61000000")));
        pieChart.addPieSlice(
                new PieModel(
                        "Metallo",
                        Integer.parseInt(tvMetalli.getText().toString()),
                        Color.parseColor("#fb7268")));
        pieChart.addPieSlice(
                new PieModel(
                        "Elettrici",
                        Integer.parseInt(tvElettrici.getText().toString()),
                        Color.parseColor("#024265")));
        pieChart.addPieSlice(
                new PieModel(
                        "Speciali",
                        Integer.parseInt(tvSpeciali.getText().toString()),
                        Color.parseColor("#FFBB86FC")));

        // To animate the pie chart
        pieChart.startAnimation();
    }
}
