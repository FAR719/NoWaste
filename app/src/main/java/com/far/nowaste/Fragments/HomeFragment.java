package com.far.nowaste.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.far.nowaste.ListaCardActivity;
import com.far.nowaste.Objects.Settimanale;
import com.far.nowaste.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class HomeFragment extends Fragment {

    // cardView
    MaterialCardView seccoCardView, plasticaCardView, cartaCardView, organicoCardView, vetroCardView,
            metalliCardView, elettriciCardView, specialiCardView;
    TextView quartiere, lunedi1, lunedi2, lunediE, martedi1, martedi2, martediE,
            mercoledi1, mercoledi2, mercolediE, giovedi1, giovedi2, giovediE,
            venerdi1, venerdi2, venerdiE, sabato, domenica;

    // firebase
    FirebaseFirestore fStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // collega le view
        seccoCardView = view.findViewById(R.id.indifferenziataCardView);
        plasticaCardView = view.findViewById(R.id.plasticaCardView);
        cartaCardView = view.findViewById(R.id.cartaCardView);
        organicoCardView = view.findViewById(R.id.organicoCardView);
        vetroCardView = view.findViewById(R.id.vetroCardView);
        metalliCardView = view.findViewById(R.id.metalliCardView);
        elettriciCardView = view.findViewById(R.id.elettriciCardView);
        specialiCardView = view.findViewById(R.id.specialiCardView);

        quartiere = view.findViewById(R.id.quartiere_conferimento);
        lunedi1 = view.findViewById(R.id.lun_1conferimento);
        lunedi2 = view.findViewById(R.id.lun_2conferimento);
        lunediE = view.findViewById(R.id.lun_e);
        martedi1 = view.findViewById(R.id.mar_1conferimento);
        martedi2 = view.findViewById(R.id.mar_2conferimento);
        martediE = view.findViewById(R.id.mar_e);
        mercoledi1 = view.findViewById(R.id.mer_1conferimento);
        mercoledi2 = view.findViewById(R.id.mer_2conferimento);
        mercolediE = view.findViewById(R.id.mer_e);
        giovedi1 = view.findViewById(R.id.gio_1conferimento);
        giovedi2 = view.findViewById(R.id.gio_2conferimento);
        giovediE = view.findViewById(R.id.gio_e);
        venerdi1 = view.findViewById(R.id.ven_1conferimento);
        venerdi2 = view.findViewById(R.id.ven_2conferimento);
        venerdiE = view.findViewById(R.id.ven_e);
        sabato = view.findViewById(R.id.sab_conferimento);
        domenica = view.findViewById(R.id.dom_conferimento);

        // definizione onClick cardView
        clickCard(plasticaCardView, "Plastica");
        clickCard(organicoCardView, "Organico");
        clickCard(seccoCardView,"Indifferenziata");
        clickCard(cartaCardView, "Carta");
        clickCard(vetroCardView, "Vetro");
        clickCard(metalliCardView,"Metalli");
        clickCard(elettriciCardView, "Elettrici");
        clickCard(specialiCardView, "Speciali");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fStore = FirebaseFirestore.getInstance();
        quartiere.setText("Quartiere " + "Borgovilla");
        fStore.collection("settimanale").document("Borgovilla").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Settimanale settimanale = value.toObject(Settimanale.class);
                lunedi1.setText(settimanale.getLunedi1());
                colorTextView(lunedi1);
                if (!settimanale.getLunedi2().equals("")) {
                    lunediE.setVisibility(View.VISIBLE);
                    lunediE.setText(" e ");
                    lunedi2.setVisibility(View.VISIBLE);
                    lunedi2.setText(value.getString("lunedi2"));
                    colorTextView(lunedi2);
                } else {
                    lunediE.setVisibility(View.GONE);
                    lunedi2.setVisibility(View.GONE);
                }

                martedi1.setText(settimanale.getMartedi1());
                colorTextView(martedi1);
                if (!settimanale.getMartedi2().equals("")) {
                    martediE.setVisibility(View.VISIBLE);
                    martediE.setText(" e ");
                    martedi2.setVisibility(View.VISIBLE);
                    martedi2.setText(value.getString("martedi2"));
                    colorTextView(martedi2);
                } else {
                    martediE.setVisibility(View.GONE);
                    martedi2.setVisibility(View.GONE);
                }

                mercoledi1.setText(settimanale.getMercoledi1());
                colorTextView(mercoledi1);
                if (!settimanale.getMercoledi2().equals("")) {
                    mercolediE.setVisibility(View.VISIBLE);
                    mercolediE.setText(" e ");
                    mercoledi2.setVisibility(View.VISIBLE);
                    mercoledi2.setText(value.getString("mercoledi2"));
                    colorTextView(mercoledi2);
                } else {
                    mercolediE.setVisibility(View.GONE);
                    mercoledi2.setVisibility(View.GONE);
                }

                giovedi1.setText(settimanale.getGiovedi1());
                colorTextView(giovedi1);
                if (!settimanale.getGiovedi2().equals("")) {
                    giovediE.setVisibility(View.VISIBLE);
                    giovediE.setText(" e ");
                    giovedi2.setVisibility(View.VISIBLE);
                    giovedi2.setText(value.getString("giovedi2"));
                    colorTextView(giovedi2);
                } else {
                    giovediE.setVisibility(View.GONE);
                    giovedi2.setVisibility(View.GONE);
                }

                venerdi1.setText(settimanale.getVenerdi1());
                colorTextView(venerdi1);
                if (!settimanale.getVenerdi2().equals("")) {
                    venerdiE.setVisibility(View.VISIBLE);
                    venerdiE.setText(" e ");
                    venerdi2.setVisibility(View.VISIBLE);
                    venerdi2.setText(value.getString("venerdi2"));
                    colorTextView(venerdi2);
                } else {
                    venerdiE.setVisibility(View.GONE);
                    venerdi2.setVisibility(View.GONE);
                }

                sabato.setText(settimanale.getSabato());
                colorTextView(sabato);

                domenica.setText(settimanale.getDomenica());
                colorTextView(domenica);
            }
        });
    }

    // definizione metodo per onClickCardView (valido per ogni carta)
    private void clickCard(View view, String string) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listaCardActivity = new Intent(getContext(), ListaCardActivity.class);
                listaCardActivity.putExtra("com.far.nowaste.CARD_TYPE", string);
                startActivity(listaCardActivity);
            }
        });
    }

    private void colorTextView(TextView textView){
        switch (textView.getText().toString()) {
            case "Plastica":
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.plastica_home));
                break;
            case "Organico":
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.organico_home));
                break;
            case "Carta":
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.carta_home));
                break;
            case "Secco":
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.secco_home));
                break;
        }
    }
}
