package com.far.nowaste;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class DetailUserFragment extends Fragment {

    // definizione variabili
    ImageView mImage;
    TextView mFullName, mEmail;

    // grafico a torta
    TextView tvCO, tvPlastica, tvOrganico, tvSecco, tvCarta, tvVetro, tvMetalli, tvElettrici, tvSpeciali;
    PieChart pieChart;

    // firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser fUser;

    Utente currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_user, container, false);

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

        // utente implementato con variabile static in MainActivity
        currentUser = MainActivity.CURRENTUSER;
        if (currentUser != null) {
            // imposta nome, cognome e immagine
            mFullName.setText(currentUser.getFullName());
            mEmail.setText(currentUser.getEmail());
            if (currentUser.getImage() != null) {
                Glide.with(getContext()).load(currentUser.getImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
            }

            // imposta il grafico a torta
            tvPlastica.setText((currentUser.getpPlastica()) + "g");
            tvOrganico.setText(currentUser.getpOrganico() + "g");
            tvSecco.setText(currentUser.getpIndifferenziata() + "g");
            tvCarta.setText(currentUser.getpCarta() + "g");
            tvVetro.setText(currentUser.getpVetro() + "g");
            tvMetalli.setText(currentUser.getpMetalli() + "g");
            tvElettrici.setText(currentUser.getpElettrici() + "g");
            tvSpeciali.setText(currentUser.getpSpeciali() + "g");
            setPieChartData(currentUser);
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
        pieChart.addPieSlice(
                new PieModel(
                        "Plastica",
                        Integer.parseInt(((int) utente.getpPlastica()) + ""),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Organico",
                        Integer.parseInt(((int) utente.getpOrganico()) + ""),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Secco",
                        Integer.parseInt(((int) utente.getpIndifferenziata()) + ""),
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        "Carta",
                        Integer.parseInt(((int) utente.getpCarta()) + ""),
                        Color.parseColor("#29B6F6")));
        pieChart.addPieSlice(
                new PieModel(
                        "Vetro",
                        Integer.parseInt(((int) utente.getpVetro()) + ""),
                        Color.parseColor("#61000000")));
        pieChart.addPieSlice(
                new PieModel(
                        "Metalli",
                        Integer.parseInt(((int) utente.getpMetalli()) + ""),
                        Color.parseColor("#05af9b")));
        pieChart.addPieSlice(
                new PieModel(
                        "Elettrici",
                        Integer.parseInt(((int) utente.getpElettrici()) + ""),
                        Color.parseColor("#024265")));
        pieChart.addPieSlice(
                new PieModel(
                        "Speciali",
                        Integer.parseInt(((int) utente.getpSpeciali()) + ""),
                        Color.parseColor("#FFBB86FC")));

        // To animate the pie chart
        pieChart.startAnimation();
    }
}
