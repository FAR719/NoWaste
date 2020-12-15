package com.far.nowaste;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TicketsListActivity extends AppCompatActivity {
    // definizione variabili
    Toolbar mToolbar;
    RecyclerView mFirestoreList;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tickets);

        // toolbar
        mToolbar = findViewById(R.id.ticketsList_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // recyclerView + FireBase
        mFirestoreList = findViewById(R.id.tickets_listaCard_recyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // to launch the activity
        Intent in = getIntent();

        // variabile passata
        String stringCardType = in.getStringExtra("com.far.nowaste.CARD_TYPE");

        // cambia il titolo della toolbar
        mToolbar.setTitle(stringCardType);

        // query
        Query query = firebaseFirestore.collection("tickets").whereEqualTo("email",fAuth.getCurrentUser().getEmail() );

        // recyclerOptions
        FirestoreRecyclerOptions<Tickets> options = new FirestoreRecyclerOptions.Builder<Tickets>().setQuery(query, Tickets.class).build();

        adapter = new FirestoreRecyclerAdapter<Tickets, TicketsListActivity.TicketsViewHolder>(options) {
            @NonNull
            @Override
            public TicketsListActivity.TicketsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_tickets_item_layout, parent, false);
                return new TicketsListActivity.TicketsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TicketsListActivity.TicketsViewHolder holder, int position, @NonNull Tickets model) {
                holder.rOggetto.setText(model.getOggetto());
                holder.rData.setText(model.getData());
                /*holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // apro la chat
                        Intent detailSearchActivity = new Intent(getApplicationContext(), DetailRifiutoActivity.class);
                        detailSearchActivity.putExtra("com.far.nowaste.NAME", model.getNome());
                        startActivity(detailSearchActivity);
                    }
                });*/
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

    private class TicketsViewHolder extends RecyclerView.ViewHolder{

        private TextView rOggetto;
        private TextView rData;
        ConstraintLayout itemLayout;


        public TicketsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.recView_listaItem_constraintLayout);
            rOggetto = itemView.findViewById(R.id.recView_listaItem_nameTextView);
            rData = itemView.findViewById(R.id.recView_listaItem_smaltimentoTextView);
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
