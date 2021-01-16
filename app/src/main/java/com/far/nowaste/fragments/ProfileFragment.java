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
import com.far.nowaste.objects.Saving;
import com.far.nowaste.objects.Utente;
import com.far.nowaste.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
    TextView tvSaving, tvPlastica, tvOrganico, tvSecco, tvCarta, tvVetro, tvMetalli, tvElettrici, tvSpeciali;
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
        tvSaving = view.findViewById(R.id.tvSaving);

        colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.plastica));
        colors.add(ContextCompat.getColor(getContext(), R.color.organico));
        colors.add(ContextCompat.getColor(getContext(), R.color.secco));
        colors.add(ContextCompat.getColor(getContext(), R.color.carta));
        colors.add(ContextCompat.getColor(getContext(), R.color.vetro));
        colors.add(ContextCompat.getColor(getContext(), R.color.metalli));
        colors.add(ContextCompat.getColor(getContext(), R.color.elettrici));
        colors.add(ContextCompat.getColor(getContext(), R.color.speciali));

        fAuth = FirebaseAuth.getInstance();

        retrieveCurrentUser();
        retrieveUserData();

        return view;
    }

    private void retrieveCurrentUser() {
        if (MainActivity.CURRENT_USER != null) {
            // imposta nome, cognome e immagine
            mFullName.setText(MainActivity.CURRENT_USER.getFullName());
            mEmail.setText(MainActivity.CURRENT_USER.getEmail());
            if (MainActivity.CURRENT_USER.getImage() != null) {
                Glide.with(getContext()).load(MainActivity.CURRENT_USER.getImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
            }
        } else {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fUser = fAuth.getCurrentUser();

            // imposta dati personali
            fStore.collection("users").document(fUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Utente utente = documentSnapshot.toObject(Utente.class);
                    // imposta nome, cognome e immagine
                    mFullName.setText(utente.getFullName());
                    mEmail.setText(utente.getEmail());
                    if (utente.getImage() != null) {
                        Glide.with(getContext()).load(utente.getImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                }
            });
        }
    }

    private void retrieveUserData() {
        if (MainActivity.CARBON_DIOXIDE_ARRAY_LIST == null || MainActivity.QUANTITA == null) {
            // inizializzazione liste
            MainActivity.CARBON_DIOXIDE_ARRAY_LIST = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                MainActivity.CARBON_DIOXIDE_ARRAY_LIST.add(i, new ArrayList<Saving>());
            }

            MainActivity.ENERGY_ARRAY_LIST = new ArrayList<>();
            MainActivity.OIL_ARRAY_LIST = new ArrayList<>();
            MainActivity.QUANTITA = new int[]{0, 0, 0, 0, 0, 0, 0, 0};

            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                    .collection("carbon_dioxide").orderBy("year", Query.Direction.ASCENDING)
                    .orderBy("month", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Saving item = document.toObject(Saving.class);
                        MainActivity.CARBON_DIOXIDE_ARRAY_LIST.get(item.getNtipo()).add(item);

                        MainActivity.QUANTITA[item.getNtipo()] += item.getQuantita();
                    }
                    setCO2Data(MainActivity.CARBON_DIOXIDE_ARRAY_LIST);

                    // imposta il BarChart
                    setBarChartData(MainActivity.QUANTITA);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("LOG", "Error! " + e.getLocalizedMessage());
                }
            });
        } else {
            setCO2Data(MainActivity.CARBON_DIOXIDE_ARRAY_LIST);
            setBarChartData(MainActivity.QUANTITA);
        }
    }

    private void setCO2Data(ArrayList<ArrayList<Saving>> arrayOfArray){
        // aggiorna la descrizione
        tvSaving.setText(Html.fromHtml("Hai risparmiato (in CO<sub><small><small>2</small></small></sub>):"));

        for (int i = 0; i < 8; i++) {
            ArrayList<Saving> arrayList = arrayOfArray.get(i);
            double punteggio = 0;

            if (!arrayList.isEmpty()) {
                for (Saving item : arrayList) {
                    punteggio += item.getPunteggio();
                }
            }

            // aggiungi una slice alla PieChart
            pieChart.addPieSlice(new PieModel(Integer.parseInt(((int) punteggio) + ""), colors.get(i)));

            // aggiorna le TextView
            switch (i) {
                case 0:
                    tvPlastica.setText(punteggio + "g");
                    break;
                case 1:
                    tvOrganico.setText(punteggio + "g");
                    break;
                case 2:
                    tvSecco.setText(punteggio + "g");
                    break;
                case 3:
                    tvCarta.setText(punteggio + "g");
                    break;
                case 4:
                    tvVetro.setText(punteggio + "g");
                    break;
                case 5:
                    tvMetalli.setText(punteggio + "g");
                    break;
                case 6:
                    tvElettrici.setText(punteggio + "g");
                    break;
                case 7:
                    tvSpeciali.setText(punteggio + "g");
                    break;
            }
        }

        // animate the PieChart
        pieChart.startAnimation();
    }

    private void setBarChartData(int[] quantita) {
        for (int i = 0; i < 8; i++) {
            barChart.addBar(new BarModel(quantita[i] + "", (float) quantita[i], colors.get(i)));
        }
        barChart.startAnimation();
    }
}
