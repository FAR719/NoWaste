package com.far.nowaste.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.far.nowaste.MainActivity;
import com.far.nowaste.Objects.Utente;
import com.far.nowaste.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    // definizione variabili
    ImageView mImage;
    TextView mFullName, mEmail;

    // grafici
    TextView tvCO, tvPlastica, tvOrganico, tvSecco, tvCarta, tvVetro, tvMetalli, tvElettrici, tvSpeciali;
    PieChart pieChart;
    BarChart istograph;
    Typeface nunito;

    List<Integer> colors;

    // firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser fUser;

    Utente currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // collegamento view
        mImage = view.findViewById(R.id.userImageView);
        mFullName = view.findViewById(R.id.nameTextView);
        mEmail = view.findViewById(R.id.emailTextView);

        // view grafico a torta
        pieChart = view.findViewById(R.id.piechart);
        tvPlastica = view.findViewById(R.id.tvPlastica);
        tvOrganico = view.findViewById(R.id.tvOrganico);
        tvSecco = view.findViewById(R.id.tvSecco);
        tvCarta = view.findViewById(R.id.tvCarta);
        tvVetro = view.findViewById(R.id.tvVetro);
        tvMetalli = view.findViewById(R.id.tvMetalli);
        tvElettrici = view.findViewById(R.id.tvElettrici);
        tvSpeciali = view.findViewById(R.id.tvSpeciali);
        tvCO = view.findViewById(R.id.c_o_tv);

        tvCO.setText(Html.fromHtml("Hai risparmiato (in CO<sub><small><small>2</small></small></sub>):"));

        nunito = ResourcesCompat.getFont(getContext(), R.font.nunito);

        colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.plastica));
        colors.add(ContextCompat.getColor(getContext(), R.color.organico));
        colors.add(ContextCompat.getColor(getContext(), R.color.secco));
        colors.add(ContextCompat.getColor(getContext(), R.color.carta));
        colors.add(ContextCompat.getColor(getContext(), R.color.vetro));
        colors.add(ContextCompat.getColor(getContext(), R.color.metalli));
        colors.add(ContextCompat.getColor(getContext(), R.color.elettrici));
        colors.add(ContextCompat.getColor(getContext(), R.color.speciali));

        // utente implementato con variabile static in MainActivity
        currentUser = MainActivity.CURRENTUSER;
        if (currentUser != null) {
            // imposta nome, cognome e immagine
            mFullName.setText(currentUser.getFullName());
            mEmail.setText(currentUser.getEmail());
            if (currentUser.getImage() != null) {
                Glide.with(getContext()).load(currentUser.getImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
            }

            // imposta il grafico a torta e la leggenda
            tvPlastica.setText((currentUser.getpPlastica()) + "g");
            tvOrganico.setText(currentUser.getpOrganico() + "g");
            tvSecco.setText(currentUser.getpIndifferenziata() + "g");
            tvCarta.setText(currentUser.getpCarta() + "g");
            tvVetro.setText(currentUser.getpVetro() + "g");
            tvMetalli.setText(currentUser.getpMetalli() + "g");
            tvElettrici.setText(currentUser.getpElettrici() + "g");
            tvSpeciali.setText(currentUser.getpSpeciali() + "g");
            setPieChartData(currentUser);

            // imposta l'istogramma
            istograph = view.findViewById(R.id.istograph);

            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(1f, currentUser.getnPlastica()));
            entries.add(new BarEntry(2f, currentUser.getnOrganico()));
            entries.add(new BarEntry(3f, currentUser.getnIndifferenziata()));
            entries.add(new BarEntry(4f, currentUser.getnCarta()));
            entries.add(new BarEntry(5f, currentUser.getnVetro()));
            entries.add(new BarEntry(6f, currentUser.getnMetalli()));
            entries.add(new BarEntry(7f, currentUser.getnElettrici()));
            entries.add(new BarEntry(8f, currentUser.getnSpeciali()));

            String[] labels = {"Plastica", "Organico", "Secco", "Carta", "Vetro", "Metalli", "Elettrici", "Speciali"};

            BarDataSet barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(colors);
            barDataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.search_primary_text));
            barDataSet.setStackLabels(labels);

            BarData data = new BarData(barDataSet);
            data.setBarWidth(0.9f);
            istograph.setData(data);
            istograph.setFitBars(true);
            istograph.setPinchZoom(false);
            istograph.setDoubleTapToZoomEnabled(false);
            istograph.invalidate();
            istograph.getXAxis().setDrawGridLines(false);
            istograph.getDescription().setText("");
            istograph.getLegend().setEnabled(false);

            YAxis leftAxis = istograph.getAxisLeft();
            leftAxis.setTypeface(nunito);
            leftAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.search_primary_text));
            YAxis rightAxis = istograph.getAxisRight();
            rightAxis.setDrawLabels(false);
            XAxis xAxis = istograph.getXAxis();
            xAxis.setDrawLabels(false);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser == null) {
            fStore = FirebaseFirestore.getInstance();
            fAuth = FirebaseAuth.getInstance();
            fUser = fAuth.getCurrentUser();

            // imposta dati personali
            fStore.collection("users").document(fUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    Utente utente = value.toObject(Utente.class);

                    // imposta nome, cognome e immagine
                    mFullName.setText(utente.getFullName());
                    mEmail.setText(utente.getEmail());
                    if (utente.getImage() != null) {
                        Glide.with(getContext()).load(utente.getImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
                    }

                    // imposta il grafico a torta
                    tvPlastica.setText((utente.getpPlastica()) + "g");
                    tvOrganico.setText(utente.getpOrganico() + "g");
                    tvSecco.setText(utente.getpIndifferenziata() + "g");
                    tvCarta.setText(utente.getpCarta() + "g");
                    tvVetro.setText(utente.getpVetro() + "g");
                    tvMetalli.setText(utente.getpMetalli() + "g");
                    tvElettrici.setText(utente.getpElettrici() + "g");
                    tvSpeciali.setText(utente.getpSpeciali() + "g");
                    setPieChartData(utente);
                }
            });
            fStore.terminate();
        }
    }

    private void setPieChartData(Utente utente) {
        // Set the data and color to the pie chart
        pieChart.addPieSlice(new PieModel("Plastica", Integer.parseInt(((int) utente.getpPlastica()) + ""), colors.get(0)));
        pieChart.addPieSlice(new PieModel("Organico", Integer.parseInt(((int) utente.getpOrganico()) + ""), colors.get(1)));
        pieChart.addPieSlice(new PieModel("Secco", Integer.parseInt(((int) utente.getpIndifferenziata()) + ""), colors.get(2)));
        pieChart.addPieSlice(new PieModel("Carta", Integer.parseInt(((int) utente.getpCarta()) + ""), colors.get(3)));
        pieChart.addPieSlice(new PieModel("Vetro", Integer.parseInt(((int) utente.getpVetro()) + ""), colors.get(4)));
        pieChart.addPieSlice(new PieModel("Metalli", Integer.parseInt(((int) utente.getpMetalli()) + ""), colors.get(5)));
        pieChart.addPieSlice(new PieModel("Elettrici", Integer.parseInt(((int) utente.getpElettrici()) + ""), colors.get(6)));
        pieChart.addPieSlice(new PieModel("Speciali", Integer.parseInt(((int) utente.getpSpeciali()) + ""), colors.get(7)));

        // To animate the pie chart
        pieChart.startAnimation();
    }
}
