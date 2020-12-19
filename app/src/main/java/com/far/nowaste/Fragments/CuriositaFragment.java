package com.far.nowaste.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.far.nowaste.Objects.Curiosity;
import com.far.nowaste.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CuriositaFragment extends Fragment {

    RecyclerView mFirestoreList;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_curiosita, container, false);

        // recyclerView + FireBase
        mFirestoreList = view.findViewById(R.id.curiosita_recyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // query
        Query query = firebaseFirestore.collection("curiosity");

        // recyclerOptions
        FirestoreRecyclerOptions<Curiosity> options = new FirestoreRecyclerOptions.Builder<Curiosity>().setQuery(query, Curiosity.class).build();

        adapter = new FirestoreRecyclerAdapter<Curiosity, CuriositaFragment.CuriosityViewHolder>(options) {
            @NonNull
            @Override
            public CuriositaFragment.CuriosityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_curiosity_item, parent, false);
                return new CuriositaFragment.CuriosityViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CuriositaFragment.CuriosityViewHolder holder, int position, @NonNull Curiosity model) {
                holder.descrizione.setText(model.getDescrizione());
                holder.etichetta.setText(model.getEtichetta());
            }
        };

        // View Holder
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFirestoreList.setAdapter(adapter);

        return view;
    }

    private class CuriosityViewHolder extends RecyclerView.ViewHolder{

        TextView descrizione, etichetta;

        public CuriosityViewHolder(@NonNull View itemView) {
            super(itemView);
            descrizione = itemView.findViewById(R.id.recView_curiosityItem_descTextView);
            etichetta = itemView.findViewById(R.id.recView_curiosityItem_etichettaTextView);
        }
    }

    //start&stop listening
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
