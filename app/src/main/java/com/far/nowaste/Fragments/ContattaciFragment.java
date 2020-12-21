package com.far.nowaste.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.far.nowaste.R;
import com.far.nowaste.TicketListActivity;
import com.google.android.material.card.MaterialCardView;

public class ContattaciFragment extends Fragment {
    MaterialCardView ticketCard, contattaciCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contattaci, container, false);

        ticketCard = view.findViewById(R.id.contattaci_ticketCard);
        contattaciCard = view.findViewById(R.id.contattaci_contattaciCard);

        ticketCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),TicketListActivity.class));
            }
        });

        contattaciCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        return view;
    }
}
