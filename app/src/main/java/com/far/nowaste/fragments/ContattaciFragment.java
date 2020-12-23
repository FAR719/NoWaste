package com.far.nowaste.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.far.nowaste.AssistenzaActivity;
import com.far.nowaste.R;
import com.far.nowaste.TabTicketActivity;
import com.google.android.material.card.MaterialCardView;

public class ContattaciFragment extends Fragment {
    MaterialCardView ticketCard, contattaciCard, assistenzaCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contattaci, container, false);

        ticketCard = view.findViewById(R.id.contattaci_ticketCard);
        contattaciCard = view.findViewById(R.id.contattaci_contattaciCard);
        assistenzaCard = view.findViewById(R.id.assistenza_MaterialCard);

        ticketCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TabTicketActivity.class));
            }
        });

        contattaciCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        assistenzaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AssistenzaActivity.class));
            }
        });

        return view;
    }
}
