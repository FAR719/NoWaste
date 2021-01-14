package com.far.nowaste.fragments;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.far.nowaste.MainActivity;
import com.far.nowaste.objects.Utente;
import com.far.nowaste.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
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
    BarChart barChart;

    List<Integer> colors;

    // firebase
    FirebaseAuth fAuth;
    FirebaseUser fUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // collegamento view
        mImage = view.findViewById(R.id.userImageView);
        mFullName = view.findViewById(R.id.nameTextView);
        mEmail = view.findViewById(R.id.emailTextView);

        // dassegnazione views
        pieChart = view.findViewById(R.id.piechart);
        barChart = view.findViewById(R.id.istograph);
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
        if (MainActivity.CURRENT_USER != null) {
            setData(MainActivity.CURRENT_USER);
        } else {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fAuth = FirebaseAuth.getInstance();
            fUser = fAuth.getCurrentUser();

            // imposta dati personali
            fStore.collection("users").document(fUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Utente utente = documentSnapshot.toObject(Utente.class);
                    setData(utente);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                }
            });
        }
        return view;
    }

    private void setData(Utente utente){
        // imposta nome, cognome e immagine
        mFullName.setText(utente.getFullName());
        mEmail.setText(utente.getEmail());
        if (utente.getImage() != null) {
            Glide.with(getContext()).load(utente.getImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
        }

        // imposta grafici e textView
        tvPlastica.setText((utente.getpPlastica()) + "g");
        tvOrganico.setText(utente.getpOrganico() + "g");
        tvSecco.setText(utente.getpSecco() + "g");
        tvCarta.setText(utente.getpCarta() + "g");
        tvVetro.setText(utente.getpVetro() + "g");
        tvMetalli.setText(utente.getpMetalli() + "g");
        tvElettrici.setText(utente.getpElettrici() + "g");
        tvSpeciali.setText(utente.getpSpeciali() + "g");
        setPieChartData(utente);
        setBarChartData(utente);
    }

    private void setPieChartData(Utente utente) {
        // Set the data and color to the pie chart
        pieChart.addPieSlice(new PieModel("Plastica", Integer.parseInt(((int) utente.getpPlastica()) + ""), colors.get(0)));
        pieChart.addPieSlice(new PieModel("Organico", Integer.parseInt(((int) utente.getpOrganico()) + ""), colors.get(1)));
        pieChart.addPieSlice(new PieModel("Secco", Integer.parseInt(((int) utente.getpSecco()) + ""), colors.get(2)));
        pieChart.addPieSlice(new PieModel("Carta", Integer.parseInt(((int) utente.getpCarta()) + ""), colors.get(3)));
        pieChart.addPieSlice(new PieModel("Vetro", Integer.parseInt(((int) utente.getpVetro()) + ""), colors.get(4)));
        pieChart.addPieSlice(new PieModel("Metalli", Integer.parseInt(((int) utente.getpMetalli()) + ""), colors.get(5)));
        pieChart.addPieSlice(new PieModel("Elettrici", Integer.parseInt(((int) utente.getpElettrici()) + ""), colors.get(6)));
        pieChart.addPieSlice(new PieModel("Speciali", Integer.parseInt(((int) utente.getpSpeciali()) + ""), colors.get(7)));

        // To animate the pie chart
        pieChart.startAnimation();
    }

    private void setBarChartData(Utente utente) {
        barChart.addBar(new BarModel(utente.getnPlastica() + "", (float)utente.getnPlastica(), colors.get(0)));
        barChart.addBar(new BarModel(utente.getnOrganico() + "", (float)utente.getnOrganico(), colors.get(1)));
        barChart.addBar(new BarModel(utente.getnSecco() + "", (float)utente.getnSecco(), colors.get(2)));
        barChart.addBar(new BarModel(utente.getnCarta() + "", (float)utente.getnCarta(), colors.get(3)));
        barChart.addBar(new BarModel(utente.getnVetro() + "", (float)utente.getnVetro(), colors.get(4)));
        barChart.addBar(new BarModel(utente.getnMetalli() + "", (float)utente.getnMetalli(), colors.get(5)));
        barChart.addBar(new BarModel(utente.getnElettrici() + "", (float)utente.getnElettrici(), colors.get(6)));
        barChart.addBar(new BarModel(utente.getnSpeciali() + "", (float)utente.getnSpeciali(), colors.get(7)));

        barChart.startAnimation();
    }
}
