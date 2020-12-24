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
import com.far.nowaste.MainActivity;
import com.far.nowaste.R;
import com.far.nowaste.TabTicketActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class ContattaciFragment extends Fragment {

    MaterialCardView ticketCard, contattaciCard, assistenzaCard;
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contattaci, container, false);

        fAuth = FirebaseAuth.getInstance();

        ticketCard = view.findViewById(R.id.contattaci_ticketCard);
        contattaciCard = view.findViewById(R.id.contattaci_contattaciCard);
        assistenzaCard = view.findViewById(R.id.assistenza_MaterialCard);

        ticketCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getContext(), TabTicketActivity.class));
                } else {
                    ((MainActivity)getActivity()).showSnackbar("Accedi per inviare ticket!");
                }
            }
        });

        contattaciCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        assistenzaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getContext(), AssistenzaActivity.class));
                } else {
                    ((MainActivity)getActivity()).showSnackbar("Accedi per ricevere assistenza!");
                }
            }
        });

        return view;
    }
}
