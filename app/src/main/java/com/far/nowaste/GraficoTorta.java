package com.far.nowaste;


// Import the required libraries
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class GraficoTorta extends AppCompatActivity {

    // Create the object of TextView
    // and PieChart class
    TextView tvR, tvPython, tvCPP, tvJava, tvVetro, tvMetalli, tvElettrici, tvSpeciali;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Creating a method setData()
        // to set the text in text view and pie chart
        setData();
    }

    private void setData()
    {

        // Set the percentage of language used
        tvR.setText(Integer.toString(40));
        tvPython.setText(Integer.toString(30));
        tvCPP.setText(Integer.toString(5));
        tvJava.setText(Integer.toString(25));
        tvVetro.setText(Integer.toString(5));
        tvMetalli.setText(Integer.toString(9));
        tvElettrici.setText(Integer.toString(8));
        tvSpeciali.setText(Integer.toString(17));

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
