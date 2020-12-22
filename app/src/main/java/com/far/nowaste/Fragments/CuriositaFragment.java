package com.far.nowaste.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.far.nowaste.Objects.Curiosity;
import com.far.nowaste.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        // query
        fStore.collection("curiosity").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Curiosity> curiosityList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
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

                    MyAdapter myAdapter = new MyAdapter(getContext(), randomCuriosityList);
                    recView.setAdapter(myAdapter);
                    recView.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        Context context;
        List<Curiosity> curiosityList;

        public MyAdapter(Context c, List<Curiosity> curList){
            context = c;
            curiosityList = curList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.layout_recycler_view_curiosity_item, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.descrizione.setText(curiosityList.get(position).getDescrizione());
            holder.etichetta.setText(curiosityList.get(position).getEtichetta());
        }

        @Override
        public int getItemCount() {
            return curiosityList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView descrizione, etichetta;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                descrizione = itemView.findViewById(R.id.recView_curiosityItem_descTextView);
                etichetta = itemView.findViewById(R.id.recView_curiosityItem_etichettaTextView);
            }
        }
    }
}
