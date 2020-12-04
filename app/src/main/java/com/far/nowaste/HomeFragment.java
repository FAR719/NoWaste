package com.far.nowaste;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    // cardView
    MaterialCardView seccoCardView;
    MaterialCardView plasticaCardView;
    MaterialCardView cartaCardView;
    MaterialCardView organicoCardView;
    MaterialCardView vetroCardView;
    MaterialCardView metalliCardView;
    MaterialCardView elettriciCardView;
    MaterialCardView specialiCardView;

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
