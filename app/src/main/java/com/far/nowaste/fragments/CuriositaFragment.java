package com.far.nowaste.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.far.nowaste.objects.Curiosity;
import com.far.nowaste.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CuriositaFragment extends Fragment {

    List<Curiosity> randomCuriosityList;

    RecyclerView recView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_curiosita, container, false);
        recView = view.findViewById(R.id.curiosita_recyclerView);

        loadCuriosita();

        return view;
    }

    public void loadCuriosita() {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("curiosity").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Curiosity> curiosityList = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Curiosity curiosity = document.toObject(Curiosity.class);
                    curiosityList.add(curiosity);
                }

                int curiosityCount = curiosityList.size();
                int randomNumber= new Random().nextInt(curiosityCount);

                randomCuriosityList = new ArrayList<>();
                for(Curiosity curiosity : curiosityList) {
                    randomCuriosityList.add(curiosityList.get(randomNumber));
                    if (randomNumber != curiosityCount - 1) {
                        randomNumber++;
                    } else {
                        randomNumber = 0;
                    }
                }

                CuriosityAdapter curiosityAdapter = new CuriosityAdapter(getContext(), randomCuriosityList);
                recView.setAdapter(curiosityAdapter);
                recView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
            }
        });
    }

    class CuriosityAdapter extends RecyclerView.Adapter<CuriosityAdapter.CuriosityViewHolder> {

        Context context;
        List<Curiosity> curiosityList;

        public CuriosityAdapter(Context c, List<Curiosity> curList){
            context = c;
            curiosityList = curList;
        }

        @NonNull
        @Override
        public CuriosityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.layout_recycler_view_curiosity_item, parent, false);

            return new CuriosityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CuriosityViewHolder holder, int position) {
            holder.descrizione.setText(curiosityList.get(position).getDescrizione());
            holder.etichetta.setText(curiosityList.get(position).getEtichetta());
            setViewColor(holder.etichetta);
        }

        @Override
        public int getItemCount() {
            return curiosityList.size();
        }

        public class CuriosityViewHolder extends RecyclerView.ViewHolder {

            TextView descrizione, etichetta;

            public CuriosityViewHolder(@NonNull View itemView) {
                super(itemView);
                descrizione = itemView.findViewById(R.id.recView_curiosityItem_descTextView);
                etichetta = itemView.findViewById(R.id.recView_curiosityItem_etichettaTextView);
            }
        }
    }

    private void setViewColor(TextView textView) {
        switch (textView.getText().toString()) {
            case "Plastica":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.plastica));
                break;
            case "Organico":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.organico));
                break;
            case "Secco":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.secco));
                break;
            case "Carta":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.carta));
                break;
            case "Vetro":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.vetro));
                break;
            case "Metalli":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.metalli));
                break;
            case "Elettrici":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.elettrici));
                break;
            case "Speciali":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.speciali));
                break;
        }
    }
}
