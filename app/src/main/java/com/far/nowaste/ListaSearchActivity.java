package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.nowaste.objects.Rifiuto;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ListaSearchActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    RecyclerView mFirestoreList;
    FirestoreRecyclerAdapter adapter;
    TextView noResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_search);

        // toolbar
        mToolbar = findViewById(R.id.listaSearch_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // to launch the activity
        Intent in = getIntent();

        // recyclerView + noResultTextView
        mFirestoreList = findViewById(R.id.listaSearch_recyclerView);
        noResult = findViewById(R.id.noResultTextView);

        // variabile passata
        String stringName = in.getStringExtra("com.far.nowaste.SEARCH_QUERY");

        // cambia il titolo della toolbar
        mToolbar.setTitle(stringName);

        // query
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("rifiuti").orderBy("nome", Query.Direction.ASCENDING)
                .startAt(stringName).endAt(stringName + "\uf8ff");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean ciSonoRisultati = false;
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ciSonoRisultati = true;
                }
                if (!ciSonoRisultati) {
                    mFirestoreList.setVisibility(View.GONE);
                    noResult.setText("Nessun risultato relativo a " + stringName);
                    noResult.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
            }
        });

        // recyclerOptions
        FirestoreRecyclerOptions<Rifiuto> options = new FirestoreRecyclerOptions.Builder<Rifiuto>().setQuery(query, Rifiuto.class).build();

        adapter = new FirestoreRecyclerAdapter<Rifiuto, ListaSearchActivity.RifiutoViewHolder>(options) {
            @NonNull
            @Override
            public ListaSearchActivity.RifiutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_lista_item, parent, false);
                return new ListaSearchActivity.RifiutoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListaSearchActivity.RifiutoViewHolder holder, int position, @NonNull Rifiuto model) {
                holder.rName.setText(model.getNome());
                holder.rSmaltimento.setText(model.getSmaltimento());
                holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailSearchActivity = new Intent(getApplicationContext(), DetailRifiutoActivity.class);
                        detailSearchActivity.putExtra("com.far.nowaste.NAME", model.getNome());
                        startActivity(detailSearchActivity);
                    }
                });
            }
        };


        // View Holder
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

        // divider nella recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mFirestoreList.addItemDecoration(dividerItemDecoration);
    }

    private class RifiutoViewHolder extends RecyclerView.ViewHolder{

        private TextView rName;
        private TextView rSmaltimento;
        ConstraintLayout itemLayout;

        public RifiutoViewHolder(@NonNull View itemView) {
            super(itemView);

            rName = itemView.findViewById(R.id.recView_listaItem_nameTextView);
            rSmaltimento = itemView.findViewById(R.id.recView_listaItem_smaltimentoTextView);
            itemLayout = itemView.findViewById(R.id.recView_listaItem_constraintLayout);
        }
    }

    //start&stop listening
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // ends this activity (back arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}