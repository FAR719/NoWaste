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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.nowaste.objects.Rifiuto;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListaCardActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    RecyclerView mFirestoreList;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_card);

        // toolbar
        mToolbar = findViewById(R.id.listaCard_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // recyclerView
        mFirestoreList = findViewById(R.id.tickets_listaCard_recyclerView);

        // to launch the activity
        Intent in = getIntent();

        // variabile passata
        String stringCardType = in.getStringExtra("com.far.nowaste.CARD_TYPE");

        // cambia il titolo della toolbar
        mToolbar.setTitle(stringCardType);

        // query
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("rifiuti").whereEqualTo("smaltimento", stringCardType).orderBy("nome", Query.Direction.ASCENDING);

        // recyclerOptions
        FirestoreRecyclerOptions<Rifiuto> options = new FirestoreRecyclerOptions.Builder<Rifiuto>().setQuery(query, Rifiuto.class).build();

        adapter = new FirestoreRecyclerAdapter<Rifiuto, RifiutoViewHolder>(options) {
            @NonNull
            @Override
            public RifiutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_lista_item, parent, false);
                return new RifiutoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RifiutoViewHolder holder, int position, @NonNull Rifiuto model) {
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

        TextView rName, rSmaltimento;
        ConstraintLayout itemLayout;

        public RifiutoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.recView_listaItem_constraintLayout);
            rName = itemView.findViewById(R.id.recView_listaItem_nameTextView);
            rSmaltimento = itemView.findViewById(R.id.recView_listaItem_smaltimentoTextView);
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