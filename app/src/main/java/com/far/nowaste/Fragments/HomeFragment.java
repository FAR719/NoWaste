package com.far.nowaste.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.far.nowaste.ListaCardActivity;
import com.far.nowaste.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class HomeFragment extends Fragment {

    // cardView
    MaterialCardView seccoCardView, plasticaCardView, cartaCardView, organicoCardView, vetroCardView;
    MaterialCardView metalliCardView, elettriciCardView, specialiCardView;
    TextView quartiere, lunedi, martedi, mercoledi, giovedi, venerdi, sabato, domenica;

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
        lunedi = view.findViewById(R.id.lun_conferimento);
        martedi = view.findViewById(R.id.mar_conferimento);
        mercoledi = view.findViewById(R.id.mer_conferimento);
        giovedi = view.findViewById(R.id.gio_conferimento);
        venerdi = view.findViewById(R.id.ven_conferimento);
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
                lunedi.setText(value.getString("lunedi"));
                martedi.setText(value.getString("martedi"));
                mercoledi.setText(value.getString("mercoledi"));
                giovedi.setText(value.getString("giovedi"));
                venerdi.setText(value.getString("venerdi"));
                sabato.setText(value.getString("sabato"));
                domenica.setText(value.getString("domenica"));
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
}
