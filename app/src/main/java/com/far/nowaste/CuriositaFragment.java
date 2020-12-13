package com.far.nowaste;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        Query query = firebaseFirestore.collection("curiosity").orderBy("titolo", Query.Direction.ASCENDING);

        // recyclerOptions
        FirestoreRecyclerOptions<Curiosity> options = new FirestoreRecyclerOptions.Builder<Curiosity>().setQuery(query, Curiosity.class).build();

        adapter = new FirestoreRecyclerAdapter<Curiosity, CuriositaFragment.CuriosityViewHolder>(options) {
            @NonNull
            @Override
            public CuriositaFragment.CuriosityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_curiosity_item_layout, parent, false);
                return new CuriositaFragment.CuriosityViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CuriositaFragment.CuriosityViewHolder holder, int position, @NonNull Curiosity model) {
                // holder.rName.setText(model.getNome());
                // holder.rSmaltimento.setText(model.getSmaltimento());
                holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };

        // View Holder
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFirestoreList.setAdapter(adapter);

        // divider nella recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mFirestoreList.addItemDecoration(dividerItemDecoration);

        return view;
    }

    private class CuriosityViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout itemLayout;

        public CuriosityViewHolder(@NonNull View itemView) {
            super(itemView);

            itemLayout = itemView.findViewById(R.id.recView_curiosityItem_constraintLayout);
        }
    }

}
