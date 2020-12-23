package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.nowaste.objects.Evento;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListaEventiActivity extends AppCompatActivity {
    // definizione variabili
    Toolbar mToolbar;
    RecyclerView recView;

    FloatingActionButton newEventBtn;

    FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventi);
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        recView = findViewById(R.id.eventList_recyclerView);

        // toolbar
        mToolbar = findViewById(R.id.eventList_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // query
        Query query = fStore.collection("events").orderBy("year",Query.Direction.DESCENDING).
                orderBy("month", Query.Direction.DESCENDING).orderBy("day", Query.Direction.DESCENDING);

        // recyclerOptions
        FirestoreRecyclerOptions<Evento> options = new FirestoreRecyclerOptions.Builder<Evento>().setQuery(query,Evento.class).build();

        adapter = new FirestoreRecyclerAdapter<Evento, ListaEventiActivity.EventViewHolder>(options) {
            @NonNull
            @Override
            public ListaEventiActivity.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_event_item, parent, false);
                return new ListaEventiActivity.EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListaEventiActivity.EventViewHolder holder, int position, @NonNull Evento model) {
                String day, month;
                if (model.getDay() < 10) {
                    day = "0" + model.getDay();
                } else {
                    day = model.getDay() + "";
                }
                if (model.getMonth() < 10) {
                    month = "0" + model.getMonth();
                } else  {
                    month = model.getMonth() + "";
                }

                holder.rdata.setText(day + "/" + month + "/" + model.getYear());
                holder.rtitolo.setText(model.getTitle());
                holder.remail.setText("Destinatario: " + model.getEmail());
                holder.rdescrizione.setText(model.getDescription());
            }
        };

        // View Holder
        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);

        newEventBtn = findViewById(R.id.newEvent_ActionButton);
        newEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewEventActivity.class));
            }
        });
    }

    private class EventViewHolder extends RecyclerView.ViewHolder {

        TextView rtitolo, remail, rdescrizione, rdata;
        ConstraintLayout itemLayout;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.recView_eventItem_constraintLayout);
            rtitolo = itemView.findViewById(R.id.recView_eventItem_titoloTextView);
            remail = itemView.findViewById(R.id.recView_eventItem_emailTextView);
            rdescrizione = itemView.findViewById(R.id.recView_eventItem_descrizioneTextView);
            rdata = itemView.findViewById(R.id.recView_eventItem_dataTextView);
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